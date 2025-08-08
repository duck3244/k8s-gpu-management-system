# K8s GPU Management System 배포 가이드

## 목차
- [개발 환경 배포](#개발-환경-배포)
- [Docker 배포](#docker-배포)
- [Kubernetes 배포](#kubernetes-배포)
- [운영 환경 배포](#운영-환경-배포)
- [모니터링 설정](#모니터링-설정)
- [트러블슈팅](#트러블슈팅)

## 개발 환경 배포

### 사전 요구사항
- Java 11+
- Oracle Database 12c+
- Gradle 6.0+

### 1. 저장소 클론
```bash
git clone <repository-url>
cd k8s-gpu-management-system
```

### 2. 데이터베이스 설정
```bash
# Oracle 데이터베이스에 사용자 생성
sqlplus system/password@localhost:1521/xe @database/init/01-create-user.sql

# DDL 스크립트 실행
sqlplus gpu_admin/gpu_password@localhost:1521/xe @database/ddl-script.sql

# 샘플 데이터 삽입 (선택사항)
sqlplus gpu_admin/gpu_password@localhost:1521/xe @database/sample-data.sql
```

### 3. 애플리케이션 실행
```bash
# 빌드 및 테스트
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/k8s-gpu-management-system-1.0.0.jar
```

### 4. 접속 확인
- API 서버: http://localhost:8080/api
- API 문서: http://localhost:8080/api/swagger-ui/
- 헬스체크: http://localhost:8080/api/actuator/health

## Docker 배포

### 1. 환경변수 설정
```bash
cd docker
cp .env.example .env
# .env 파일 수정 (필요한 경우)
```

### 2. 개발환경 시작
```bash
# 개발환경 시작 스크립트 사용
./scripts/start-dev.sh

# 또는 직접 실행
cd docker
docker-compose up -d
```

### 3. 로그 확인
```bash
# 전체 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f k8s-gpu-app
docker-compose logs -f oracle-db
```

### 4. 서비스 접속
- API 서버: http://localhost:8080/api
- Grafana: http://localhost:3000 (admin/admin123)
- Prometheus: http://localhost:9090

### 5. 개발환경 중지
```bash
./scripts/stop-dev.sh
```

## Kubernetes 배포

### 사전 요구사항
- Kubernetes 클러스터 (v1.20+)
- kubectl 설정 완료
- Helm (선택사항)
- Docker Registry 접근 권한

### 1. 네임스페이스 생성
```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. 시크릿 및 설정 적용
```bash
# 시크릿 생성 (실제 값으로 수정 필요)
kubectl apply -f k8s/secret.yaml

# 설정맵 적용
kubectl apply -f k8s/configmap.yaml
```

### 3. 애플리케이션 배포
```bash
# 디플로이먼트 생성
kubectl apply -f k8s/deployment.yaml

# 서비스 생성
kubectl apply -f k8s/service.yaml

# 인그레스 생성 (선택사항)
kubectl apply -f k8s/ingress.yaml
```

### 4. 배포 상태 확인
```bash
# 네임스페이스 내 모든 리소스 확인
kubectl get all -n k8s-gpu-management

# 파드 상태 확인
kubectl get pods -n k8s-gpu-management -w

# 로그 확인
kubectl logs -f deployment/k8s-gpu-management -n k8s-gpu-management
```

### 5. 서비스 접속
```bash
# 포트 포워딩을 통한 접속
kubectl port-forward service/k8s-gpu-management-service 8080:80 -n k8s-gpu-management

# 또는 NodePort 서비스 사용
kubectl get service k8s-gpu-management-nodeport -n k8s-gpu-management
```

## 운영 환경 배포

### 1. 이미지 빌드 및 푸시
```bash
# 도커 이미지 빌드
docker build -f docker/Dockerfile -t k8s-gpu-management:1.0.0 .

# 레지스트리에 푸시
docker tag k8s-gpu-management:1.0.0 registry.company.com/k8s-gpu-management:1.0.0
docker push registry.company.com/k8s-gpu-management:1.0.0
```

### 2. 운영 환경별 설정 조정
```bash
# production profile 적용
kubectl patch deployment k8s-gpu-management -n k8s-gpu-management \
  -p '{"spec":{"template":{"spec":{"containers":[{"name":"k8s-gpu-management","env":[{"name":"SPRING_PROFILES_ACTIVE","value":"production"}]}]}}}}'
```

### 3. 리소스 스케일링
```bash
# 레플리카 수 조정
kubectl scale deployment k8s-gpu-management --replicas=3 -n k8s-gpu-management

# HPA (Horizontal Pod Autoscaler) 설정
kubectl autoscale deployment k8s-gpu-management \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n k8s-gpu-management
```

### 4. 롤링 업데이트
```bash
# 새 이미지로 업데이트
kubectl set image deployment/k8s-gpu-management \
  k8s-gpu-management=registry.company.com/k8s-gpu-management:1.1.0 \
  -n k8s-gpu-management

# 롤아웃 상태 확인
kubectl rollout status deployment/k8s-gpu-management -n k8s-gpu-management

# 롤백 (필요한 경우)
kubectl rollout undo deployment/k8s-gpu-management -n k8s-gpu-management
```

## 모니터링 설정

### 1. Prometheus 메트릭 확인
```bash
curl http://localhost:8080/api/actuator/prometheus
```

### 2. Grafana 대시보드 설정
1. Grafana 접속 (http://localhost:3000)
2. Login: admin/admin123
3. Data Source 확인 (자동 설정됨)
4. 대시보드 임포트 또는 생성

### 3. 알림 설정 (선택사항)
```yaml
# AlertManager 설정 예시
groups:
  - name: k8s-gpu-management
    rules:
      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage detected"
```

## 백업 및 복구

### 1. 데이터베이스 백업
```bash
# Oracle 백업 (Docker 환경)
docker exec k8s-gpu-oracle expdp gpu_admin/gpu_password \
  directory=DATA_PUMP_DIR \
  dumpfile=k8s_gpu_backup.dmp \
  schemas=gpu_admin

# 백업 파일 추출
docker cp k8s-gpu-oracle:/opt/oracle/admin/XE/dpdump/k8s_gpu_backup.dmp ./
```

### 2. 설정 백업
```bash
# Kubernetes 설정 백업
kubectl get all,configmap,secret -n k8s-gpu-management -o yaml > k8s-backup.yaml
```

### 3. 복구
```bash
# 데이터베이스 복구
docker exec k8s-gpu-oracle impdp gpu_admin/gpu_password \
  directory=DATA_PUMP_DIR \
  dumpfile=k8s_gpu_backup.dmp \
  schemas=gpu_admin

# Kubernetes 리소스 복구
kubectl apply -f k8s-backup.yaml
```

## 보안 고려사항

### 1. 시크릿 관리
- 실제 운영 환경에서는 Kubernetes Secret 대신 Vault 등 사용 권장
- 데이터베이스 패스워드 정기 변경
- TLS 인증서 적절한 관리

### 2. 네트워크 보안
```yaml
# NetworkPolicy 예시
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: k8s-gpu-management-netpol
spec:
  podSelector:
    matchLabels:
      app: k8s-gpu-management-system
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - namespaceSelector:
            matchLabels:
              name: ingress-nginx
      ports:
        - protocol: TCP
          port: 8080
```

### 3. RBAC 권한 최소화
- 실제 필요한 권한만 부여
- 정기적인 권한 검토

## 성능 튜닝

### 1. JVM 튜닝
```bash
# 운영 환경 JVM 옵션
JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 2. 데이터베이스 연결 풀 조정
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30
      minimum-idle: 10
```

### 3. 배치 작업 최적화
```yaml
app:
  batch:
    metrics-collection:
      chunk-size: 500
      thread-pool-size: 10
```

## 문제 해결

일반적인 문제 해결 방법은 [troubleshooting.md](troubleshooting.md)를 참조하세요.