# K8s GPU Management System 트러블슈팅 가이드

## 목차
- [일반적인 문제](#일반적인-문제)
- [데이터베이스 관련 문제](#데이터베이스-관련-문제)
- [Docker 관련 문제](#docker-관련-문제)
- [Kubernetes 관련 문제](#kubernetes-관련-문제)
- [배치 작업 관련 문제](#배치-작업-관련-문제)
- [성능 관련 문제](#성능-관련-문제)
- [로깅 및 모니터링](#로깅-및-모니터링)

## 일반적인 문제

### 1. 애플리케이션이 시작되지 않음

#### 증상
```
Error starting ApplicationContext
Failed to configure a DataSource
```

#### 원인
- 데이터베이스 연결 실패
- 잘못된 데이터베이스 설정
- Oracle 서버가 실행되지 않음

#### 해결책
```bash
# 1. Oracle 서버 상태 확인
docker ps | grep oracle
# 또는
systemctl status oracle-xe

# 2. 데이터베이스 연결 테스트
sqlplus gpu_admin/gpu_password@localhost:1521/xe

# 3. 설정 파일 확인
cat src/main/resources/application.yml | grep datasource -A 10

# 4. 포트 충돌 확인
netstat -an | grep 1521
```

### 2. API 호출 시 404 오류

#### 증상
```
HTTP 404 Not Found
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/v1/gpu-nodes"
}
```

#### 원인
- 잘못된 URL 경로
- 애플리케이션 컨텍스트 경로 설정 오류

#### 해결책
```bash
# 1. 정확한 URL 확인
# 올바른 URL: http://localhost:8080/api/v1/gpu-nodes
# 잘못된 URL: http://localhost:8080/v1/gpu-nodes

# 2. 애플리케이션 상태 확인
curl http://localhost:8080/api/actuator/health

# 3. 로그 확인
tail -f logs/k8s-gpu-management.log | grep ERROR
```

### 3. 메모리 부족 오류

#### 증상
```
java.lang.OutOfMemoryError: Java heap space
```

#### 해결책
```bash
# JVM 힙 메모리 증가
export JAVA_OPTS="-Xms1g -Xmx2g"
java $JAVA_OPTS -jar k8s-gpu-management-system-1.0.0.jar

# Docker 환경에서
docker run -e JAVA_OPTS="-Xms1g -Xmx2g" k8s-gpu-management:1.0.0
```

## 데이터베이스 관련 문제

### 1. Oracle 연결 오류

#### 증상
```
ORA-12541: TNS:no listener
ORA-01017: invalid username/password
```

#### 해결책
```bash
# 1. Oracle 리스너 상태 확인
lsnrctl status

# 2. Oracle 서비스 재시작
sudo systemctl restart oracle-xe
# 또는 Docker
docker restart k8s-gpu-oracle

# 3. 사용자 권한 확인
sqlplus system/OraclePassword123@localhost:1521/xe
SQL> SELECT username FROM all_users WHERE username = 'GPU_ADMIN';

# 4. 사용자 재생성 (필요한 경우)
SQL> @database/init/01-create-user.sql
```

### 2. 테이블 스페이스 부족

#### 증상
```
ORA-01653: unable to extend table GPU_ADMIN.GPU_USAGE_METRICS
```

#### 해결책
```sql
-- 테이블스페이스 공간 확인
SELECT tablespace_name, 
       ROUND(SUM(bytes)/1024/1024, 2) AS size_mb,
       ROUND(SUM(maxbytes)/1024/1024, 2) AS max_size_mb
FROM dba_data_files 
GROUP BY tablespace_name;

-- 데이터파일 확장
ALTER DATABASE DATAFILE '/opt/oracle/oradata/XE/system01.dbf' 
RESIZE 1000M;

-- 자동 확장 설정
ALTER DATABASE DATAFILE '/opt/oracle/oradata/XE/system01.dbf' 
AUTOEXTEND ON NEXT 100M MAXSIZE 2000M;
```

### 3. 배치 테이블 오류

#### 증상
```
Table 'BATCH_JOB_INSTANCE' doesn't exist
```

#### 해결책
```bash
# Spring Batch 스키마 초기화
java -jar k8s-gpu-management-system-1.0.0.jar \
  --spring.batch.initialize-schema=always
```

## Docker 관련 문제

### 1. 컨테이너 시작 실패

#### 증상
```
docker: Error response from daemon: driver failed programming external connectivity
```

#### 해결책
```bash
# 1. 포트 충돌 확인
netstat -tulpn | grep :8080

# 2. 다른 포트 사용
docker run -p 8081:8080 k8s-gpu-management:1.0.0

# 3. Docker 서비스 재시작
sudo systemctl restart docker
```

### 2. Oracle 컨테이너 초기화 실패

#### 증상
```
ORA-01012: not logged on
Database is not ready
```

#### 해결책
```bash
# 1. 컨테이너 로그 확인
docker logs k8s-gpu-oracle

# 2. 초기화 대기 시간 증가
# docker-compose.yml에서 healthcheck interval 조정

# 3. 수동 초기화
docker exec -it k8s-gpu-oracle bash
sqlplus sys/OraclePassword123@localhost:1521/xe as sysdba
SQL> @/container-entrypoint-initdb.d/01-create-user.sql
```

### 3. 볼륨 마운트 문제

#### 증상
```
Permission denied
Cannot write to /app/logs
```

#### 해결책
```bash
# 1. 권한 확인 및 수정
sudo chown -R 1001:1001 ./logs

# 2. SELinux 설정 (필요한 경우)
sudo setsebool -P container_manage_cgroup on

# 3. 볼륨 재생성
docker-compose down -v
docker-compose up -d
```

## Kubernetes 관련 문제

### 1. 파드가 Pending 상태

#### 증상
```bash
kubectl get pods -n k8s-gpu-management
NAME                                    READY   STATUS    RESTARTS   AGE
k8s-gpu-management-7d4b9c8f5d-xyz123   0/1     Pending   0          5m
```

#### 해결책
```bash
# 1. 파드 이벤트 확인
kubectl describe pod k8s-gpu-management-7d4b9c8f5d-xyz123 -n k8s-gpu-management

# 2. 노드 리소스 확인
kubectl top nodes

# 3. 스케줄링 조건 확인
kubectl get nodes --show-labels

# 4. 리소스 요청량 조정
kubectl patch deployment k8s-gpu-management -n k8s-gpu-management \
  -p '{"spec":{"template":{"spec":{"containers":[{"name":"k8s-gpu-management","resources":{"requests":{"memory":"256Mi","cpu":"100m"}}}]}}}}'
```

### 2. 파드가 CrashLoopBackOff 상태

#### 증상
```bash
kubectl get pods -n k8s-gpu-management
NAME                                    READY   STATUS             RESTARTS   AGE
k8s-gpu-management-7d4b9c8f5d-abc123   0/1     CrashLoopBackOff   5          10m
```

#### 해결책
```bash
# 1. 파드 로그 확인
kubectl logs k8s-gpu-management-7d4b9c8f5d-abc123 -n k8s-gpu-management

# 2. 이전 로그 확인
kubectl logs k8s-gpu-management-7d4b9c8f5d-abc123 -n k8s-gpu-management --previous

# 3. 설정 확인
kubectl get configmap k8s-gpu-management-config -n k8s-gpu-management -o yaml

# 4. 시크릿 확인
kubectl get secret k8s-gpu-management-secret -n k8s-gpu-management -o yaml
```

### 3. 서비스 접근 불가

#### 증상
```bash
curl: (7) Failed to connect to service:80
```

#### 해결책
```bash
# 1. 서비스 상태 확인
kubectl get svc -n k8s-gpu-management

# 2. 엔드포인트 확인
kubectl get endpoints -n k8s-gpu-management

# 3. 파드 라벨 확인
kubectl get pods -n k8s-gpu-management --show-labels

# 4. 포트 포워딩으로 테스트
kubectl port-forward service/k8s-gpu-management-service 8080:80 -n k8s-gpu-management
```

## 배치 작업 관련 문제

### 1. 배치 작업이 실행되지 않음

#### 증상
```
No jobs found to execute
Batch job not triggered
```

#### 해결책
```bash
# 1. 스케줄러 활성화 확인
grep -r "@EnableScheduling" src/

# 2. 배치 설정 확인
grep -A 10 "spring.batch" src/main/resources/application.yml

# 3. 수동 배치 실행 테스트
curl -X POST "http://localhost:8080/api/actuator/batch/jobs/gpuMetricsCollectionJob"

# 4. 로그 레벨 변경
# application.yml에서 com.k8s.gpu.batch: DEBUG 설정
```

### 2. 배치 작업 실패

#### 증상
```
Job execution failed
Step execution failed with exit code: FAILED
```

#### 해결책
```bash
# 1. 배치 메타데이터 테이블 확인
sqlplus gpu_admin/gpu_password@localhost:1521/xe
SQL> SELECT * FROM BATCH_JOB_EXECUTION ORDER BY CREATE_TIME DESC;

# 2. 실패한 Step 확인
SQL> SELECT * FROM BATCH_STEP_EXECUTION WHERE EXIT_CODE = 'FAILED';

# 3. 배치 테이블 초기화 (필요한 경우)
SQL> DELETE FROM BATCH_STEP_EXECUTION_CONTEXT;
SQL> DELETE FROM BATCH_JOB_EXECUTION_CONTEXT;
SQL> DELETE FROM BATCH_STEP_EXECUTION;
SQL> DELETE FROM BATCH_JOB_EXECUTION_PARAMS;
SQL> DELETE FROM BATCH_JOB_EXECUTION;
SQL> DELETE FROM BATCH_JOB_INSTANCE;
```

## 성능 관련 문제

### 1. API 응답 속도 저하

#### 증상
```
API response time > 5 seconds
High CPU usage
```

#### 해결책
```bash
# 1. JVM 메모리 프로파일링
jstat -gc [PID] 1s

# 2. 데이터베이스 연결 풀 최적화
# application.yml에서 HikariCP 설정 조정
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

# 3. 데이터베이스 인덱스 확인
SQL> SELECT * FROM USER_INDEXES WHERE TABLE_NAME = 'GPU_NODES';

# 4. 쿼리 실행 계획 분석
SQL> EXPLAIN PLAN FOR SELECT * FROM GPU_NODES WHERE NODE_STATUS = 'ACTIVE';
SQL> SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
```

### 2. 메모리 누수

#### 증상
```
Memory usage continuously increasing
Frequent garbage collection
```

#### 해결책
```bash
# 1. 힙 덤프 생성
jcmd [PID] GC.run_finalization
jcmd [PID] VM.gc
jmap -dump:format=b,file=heapdump.hprof [PID]

# 2. GC 로그 활성화
JAVA_OPTS="-XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# 3. 메모리 분석 도구 사용
# VisualVM, Eclipse MAT 등으로 힙 덤프 분석
```

## 로깅 및 모니터링

### 1. 로그 파일 위치 및 설정

#### 로그 파일 경로
```
logs/k8s-gpu-management.log       # 애플리케이션 로그
logs/k8s-gpu-management-error.log # 에러 로그
logs/k8s-gpu-batch.log           # 배치 작업 로그
```

#### 로그 레벨 동적 변경
```bash
# 런타임 중 로그 레벨 변경
curl -X POST "http://localhost:8080/api/actuator/loggers/com.k8s.gpu" \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'

# 현재 로그 레벨 확인
curl "http://localhost:8080/api/actuator/loggers/com.k8s.gpu"
```

### 2. 유용한 로그 검색 명령어

```bash
# 에러 로그 검색
grep -i error logs/k8s-gpu-management.log

# 특정 시간 범위 로그
grep "2025-08-01 14:" logs/k8s-gpu-management.log

# 배치 작업 로그
grep "GpuMetricsBatchJob" logs/k8s-gpu-batch.log

# 데이터베이스 관련 로그
grep -i "database\|oracle\|sql" logs/k8s-gpu-management.log

# API 호출 로그
grep "HTTP" logs/k8s-gpu-management.log
```

### 3. 모니터링 메트릭 확인

```bash
# 헬스체크
curl http://localhost:8080/api/actuator/health

# JVM 메트릭
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used

# 데이터베이스 연결 풀 상태
curl http://localhost:8080/api/actuator/metrics/hikaricp.connections.active

# HTTP 요청 메트릭
curl http://localhost:8080/api/actuator/metrics/http.server.requests
```

## 디버깅 도구 및 기법

### 1. 원격 디버깅 설정

```bash
# 원격 디버깅 활성화
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
java $JAVA_OPTS -jar k8s-gpu-management-system-1.0.0.jar

# Docker에서
docker run -p 5005:5005 -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" k8s-gpu-management:1.0.0
```

### 2. 스레드 덤프 생성

```bash
# 스레드 덤프 생성
jstack [PID] > threaddump.txt
# 또는
jcmd [PID] Thread.print > threaddump.txt

# 애플리케이션을 통한 스레드 덤프
curl "http://localhost:8080/api/actuator/threaddump"
```

### 3. 프로파일링 도구

```bash
# JProfiler 연결
java -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849 -jar app.jar

# 간단한 성능 측정
time curl "http://localhost:8080/api/v1/gpu-nodes"
```

## 자주 묻는 질문 (FAQ)

### Q1: 애플리케이션이 정상적으로 시작되었는지 어떻게 확인하나요?

```bash
# 헬스체크로 확인
curl http://localhost:8080/api/actuator/health

# 프로세스 확인
ps aux | grep k8s-gpu-management

# 포트 리스닝 확인
netstat -an | grep 8080
```

### Q2: 데이터베이스 연결이 안 됩니다.

1. Oracle 서버가 실행 중인지 확인
2. 방화벽 설정 확인 (1521 포트)
3. 사용자 권한 및 패스워드 확인
4. TNS 설정 확인

### Q3: Docker 컨테이너에서 한글이 깨집니다.

```bash
# 컨테이너에 한글 로케일 설정
docker run -e LANG=ko_KR.UTF-8 -e LC_ALL=ko_KR.UTF-8 k8s-gpu-management:1.0.0
```

### Q4: Kubernetes에서 설정을 변경하려면?

```bash
# ConfigMap 수정
kubectl edit configmap k8s-gpu-management-config -n k8s-gpu-management

# 파드 재시작 (설정 반영)
kubectl rollout restart deployment k8s-gpu-management -n k8s-gpu-management
```

## 긴급 복구 절차

### 1. 데이터베이스 복구

```bash
# 백업에서 복구
impdp gpu_admin/gpu_password directory=DATA_PUMP_DIR dumpfile=backup.dmp

# 테이블 재생성
sqlplus gpu_admin/gpu_password@localhost:1521/xe @database/ddl-script.sql
```

### 2. 애플리케이션 롤백

```bash
# Kubernetes 롤백
kubectl rollout undo deployment k8s-gpu-management -n k8s-gpu-management

# Docker 이전 버전 실행
docker run k8s-gpu-management:previous-tag
```

### 3. 긴급 점검 모드

```bash
# 애플리케이션을 읽기 전용 모드로 전환
kubectl patch deployment k8s-gpu-management -n k8s-gpu-management \
  -p '{"spec":{"template":{"spec":{"containers":[{"name":"k8s-gpu-management","env":[{"name":"SPRING_PROFILES_ACTIVE","value":"readonly"}]}]}}}}'
```

## 연락처 및 지원

문제가 해결되지 않는 경우:
- 이슈 트래커: [GitHub Issues](https://github.com/company/k8s-gpu-management/issues)
- 기술 지원: k8s-gpu-team@company.com
- 긴급 상황: +82-2-1234-5678 (24시간 대응)