# K8s GPU Management System 배포 가이드

## 🎯 개요

이 문서는 K8s GPU Management System의 배포 및 운영에 대한 종합 가이드입니다.

## 📋 사전 요구사항

### 시스템 요구사항
- **OS**: Windows 10 Pro / Ubuntu 20.04+ / CentOS 8+
- **Java**: OpenJDK 11 이상
- **Memory**: 최소 4GB, 권장 8GB
- **Disk**: 최소 20GB 여유공간
- **Database**: Oracle 12c 이상

### 필수 소프트웨어
- Docker & Docker Compose
- Git
- Gradle 6.0+
- Oracle JDBC Driver

## 🚀 배포 방법

### 1. 로컬 개발환경 배포

#### 1-1. 저장소 클론
```bash
git clone <repository-url>
cd k8s-gpu-management-system
```

#### 1-2. 환경 설정
```bash
# 환경변수 파일 생성
cp .env.example .env

# 필요에 따라 .env 파일 수정
vi .env
```

#### 1-3. 개발환경 시작
```bash
# 개발환경 자동 설정 스크립트 실행
chmod +x scripts/start-dev.sh
./scripts/start-dev.sh

# 또는 수동으로 실행
docker-compose up -d oracle-db
# Oracle DB 초기화 대기 (2-3분)
./gradlew clean build
docker-compose up -d
```

#### 1-4. 데이터베이스 초기화
```bash
# DDL 스크립트 실행
docker-compose exec oracle-db sqlplus gpu_admin/gpu_password@//localhost:1521/XE @/path/to/ddl-script.sql

# 샘플 데이터 삽입
docker-compose exec oracle-db sqlplus gpu_admin/gpu_password@//localhost:1521/XE @/path/to/sample-data.sql
```

### 2. 운영환경 배포

#### 2-1. JAR 파일 생성
```bash
./gradlew clean build -x test
```

#### 2-2. 운영 서버 설정
```bash
# 애플리케이션 디렉토리 생성
sudo mkdir -p /opt/k8s-gpu-management
sudo mkdir -p /opt/k8s-gpu-management/logs
sudo mkdir -p /opt/k8s-gpu-management/config

# JAR 파일 복사
sudo cp build/libs/k8s-gpu-management-system-1.0.0.jar /opt/k8s-gpu-management/

# 설정 파일 복사
sudo cp src/main/resources/application-prod.yml /opt/k8s-gpu-management/config/

# 권한 설정
sudo chown -R app:app /opt/k8s-gpu-management
```

#### 2-3. Systemd 서비스 등록
```bash
# 서비스 파일 생성
sudo tee /etc/systemd/system/k8s-gpu-management.service > /dev/null <<EOF
[Unit]
Description=K8s GPU Management System
After=network.target

[Service]
Type=simple
User=app
Group=app
WorkingDirectory=/opt/k8s-gpu-management
ExecStart=/usr/bin/java -Xmx4g -Xms2g -Dspring.profiles.active=prod -Dspring.config.location=config/ -jar k8s-gpu-management-system-1.0.0.jar
ExecStop=/bin/kill -SIGTERM $MAINPID
TimeoutStopSec=20
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# 서비스 등록 및 시작
sudo systemctl daemon-reload
sudo systemctl enable k8s-gpu-management
sudo systemctl start k8s-gpu-management
```

### 3. Kubernetes 배포

#### 3-1. 네임스페이스 생성
```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: gpu-management
```

#### 3-2. ConfigMap 생성
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: k8s-gpu-config
  namespace: gpu-management
data:
  application.yml: |
    server:
      port: 8080
    spring:
      datasource:
        url: jdbc:oracle:thin:@oracle-service:1521:XE
        username: gpu_admin
        password: gpu_password
    # ... 추가 설정
```

#### 3-3. Secret 생성
```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: k8s-gpu-secrets
  namespace: gpu-management
type: Opaque
data:
  db-password: Z3B1X3Bhc3N3b3Jk  # base64 인코딩된 패스워드
```

#### 3-4. Deployment 생성
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-gpu-management
  namespace: gpu-management
spec:
  replicas: 3
  selector:
    matchLabels:
      app: k8s-gpu-management
  template:
    metadata:
      labels:
        app: k8s-gpu-management
    spec:
      containers:
      - name: k8s-gpu-management
        image: k8s-gpu-management:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: k8s-gpu-secrets
              key: db-password
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        resources:
          requests:
            memory: "2Gi"
            cpu: "500m"
          limits:
            memory: "4Gi"
            cpu: "2"
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
      volumes:
      - name: config-volume
        configMap:
          name: k8s-gpu-config
```

#### 3-5. Service 생성
```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-gpu-service
  namespace: gpu-management
spec:
  selector:
    app: k8s-gpu-management
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
```

#### 3-6. Ingress 생성
```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: k8s-gpu-ingress
  namespace: gpu-management
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: gpu-management.company.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: k8s-gpu-service
            port:
              number: 80
```

## 🔧 운영 관리

### 1. 애플리케이션 모니터링

#### 1-1. 헬스체크 엔드포인트
```bash
# 애플리케이션 상태 확인
curl http://localhost:8080/api/actuator/health

# 상세 메트릭 확인
curl http://localhost:8080/api/actuator/metrics

# Prometheus 메트릭 확인
curl http://localhost:8080/api/actuator/prometheus
```

#### 1-2. 로그 모니터링
```bash
# 애플리케이션 로그 확인
tail -f /opt/k8s-gpu-management/logs/k8s-gpu-management.log

# 에러 로그 확인
tail -f /opt/k8s-gpu-management/logs/k8s-gpu-management-error.log

# 배치 로그 확인
tail -f /opt/k8s-gpu-management/logs/k8s-gpu-batch.log

# Docker 환경에서 로그 확인
docker-compose logs -f k8s-gpu-app
```

### 2. 데이터베이스 관리

#### 2-1. 백업
```bash
# 전체 스키마 백업
expdp gpu_admin/gpu_password@XE \
  schemas=gpu_admin \
  directory=backup_dir \
  dumpfile=gpu_management_backup_$(date +%Y%m%d).dmp \
  logfile=backup_$(date +%Y%m%d).log

# 특정 테이블 백업
expdp gpu_admin/gpu_password@XE \
  tables=gpu_nodes,gpu_devices,gpu_usage_metrics \
  directory=backup_dir \
  dumpfile=gpu_core_tables_$(date +%Y%m%d).dmp
```

#### 2-2. 복원
```bash
# 전체 스키마 복원
impdp gpu_admin/gpu_password@XE \
  schemas=gpu_admin \
  directory=backup_dir \
  dumpfile=gpu_management_backup_20250808.dmp \
  table_exists_action=replace
```

#### 2-3. 성능 모니터링
```sql
-- 활성 세션 확인
SELECT s.sid, s.serial#, s.username, s.program, s.status
FROM v$session s
WHERE s.username = 'GPU_ADMIN';

-- 실행 중인 SQL 확인
SELECT sql_text, executions, elapsed_time
FROM v$sql
WHERE parsing_schema_name = 'GPU_ADMIN'
ORDER BY elapsed_time DESC;

-- 테이블별 통계
SELECT table_name, num_rows, last_analyzed
FROM user_tables
ORDER BY num_rows DESC;
```

### 3. 배치 작업 관리

#### 3-1. 배치 실행 상태 확인
```sql
-- 배치 작업 실행 이력 조회
SELECT job_execution_id, job_name, status, start_time, end_time
FROM batch_job_execution
ORDER BY start_time DESC;

-- 실패한 배치 작업 조회
SELECT je.job_execution_id, je.job_name, je.status, je.exit_message
FROM batch_job_execution je
WHERE je.status = 'FAILED'
ORDER BY je.start_time DESC;
```

#### 3-2. 배치 작업 수동 실행
```bash
# GPU 메트릭 수집 배치 실행
curl -X POST http://localhost:8080/api/admin/batch/run \
  -H "Content-Type: application/json" \
  -d '{"jobName": "gpuMetricsCollectionJob"}'
```

### 4. 성능 튜닝

#### 4-1. JVM 튜닝
```bash
# 메모리 사용량 최적화
-Xmx4g -Xms2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/opt/k8s-gpu-management/logs/

# GC 로깅
-Xlog:gc*:logs/gc.log:time,tags
```

#### 4-2. 데이터베이스 튜닝
```sql
-- 인덱스 분석
SELECT index_name, table_name, uniqueness, status
FROM user_indexes
WHERE table_name IN ('GPU_NODES', 'GPU_DEVICES', 'GPU_USAGE_METRICS');

-- 통계 정보 업데이트
EXEC DBMS_STATS.GATHER_SCHEMA_STATS('GPU_ADMIN');

-- 느린 쿼리 분석
SELECT sql_text, elapsed_time, cpu_time, executions
FROM v$sql
WHERE elapsed_time > 1000000  -- 1초 이상
ORDER BY elapsed_time DESC;
```

### 5. 보안 관리

#### 5-1. 패스워드 변경
```sql
-- 데이터베이스 사용자 패스워드 변경
ALTER USER gpu_admin IDENTIFIED BY new_password;
```

#### 5-2. 접근 제어
```yaml
# application-prod.yml에서 관리 엔드포인트 보안 설정
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
  security:
    enabled: true
```

### 6. 장애 대응

#### 6-1. 일반적인 문제 해결

**애플리케이션 시작 실패**
```bash
# 포트 충돌 확인
netstat -tuln | grep 8080

# Java 프로세스 확인
ps aux | grep java

# 로그 확인
tail -f logs/k8s-gpu-management.log
```

**데이터베이스 연결 실패**
```bash
# Oracle 리스너 상태 확인
lsnrctl status

# TNS 연결 테스트
tnsping XE

# 방화벽 확인
telnet oracle-host 1521
```

**메모리 부족**
```bash
# 메모리 사용량 확인
free -h
ps aux --sort=-%mem | head

# JVM 힙 덤프 분석
jmap -dump:format=b,file=heap.hprof <pid>
```

#### 6-2. 롤백 절차
```bash
# 1. 서비스 중지
sudo systemctl stop k8s-gpu-management

# 2. 이전 버전으로 롤백
sudo cp backup/k8s-gpu-management-system-0.9.0.jar \
       /opt/k8s-gpu-management/k8s-gpu-management-system-1.0.0.jar

# 3. 데이터베이스 롤백 (필요시)
# impdp 명령으로 이전 백업 복원

# 4. 서비스 재시작
sudo systemctl start k8s-gpu-management
```

## 📊 모니터링 대시보드

### Grafana 대시보드 설정
1. Grafana 접속: http://localhost:3000
2. 데이터소스 추가: Prometheus (http://prometheus:9090)
3. 대시보드 import: monitoring/grafana/dashboards/

### 주요 모니터링 지표
- **시스템 메트릭**: CPU, Memory, Disk 사용률
- **애플리케이션 메트릭**: Request rate, Response time, Error rate
- **데이터베이스 메트릭**: Connection pool, Query performance
- **배치 작업 메트릭**: Job execution time, Success rate

## 📞 지원 및 문의

- **개발팀**: k8s-gpu-team@company.com
- **이슈 트래킹**: GitHub Issues
- **문서**: 프로젝트 Wiki
- **긴급 연락**: On-call 담당자

---

## 📝 체크리스트

### 배포 전 체크리스트
- [ ] 사전 요구사항 확인
- [ ] 환경변수 설정 완료
- [ ] 데이터베이스 백업 완료
- [ ] 설정 파일 검토 완료
- [ ] 테스트 환경에서 검증 완료

### 배포 후 체크리스트
- [ ] 애플리케이션 정상 시작 확인
- [ ] 헬스체크 엔드포인트 응답 확인
- [ ] 데이터베이스 연결 확인
- [ ] API 기능 테스트 완료
- [ ] 배치 작업 정상 실행 확인
- [ ] 모니터링 대시보드 설정 완료

이 가이드를 통해 K8s GPU Management System을 안전하고 효율적으로 배포하고 운영할 수 있습니다.