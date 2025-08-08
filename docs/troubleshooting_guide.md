# K8s GPU Management System 트러블슈팅 가이드

## 🎯 개요

이 문서는 K8s GPU Management System 운영 중 발생할 수 있는 문제들과 해결 방법을 제공합니다.

## 🚨 일반적인 문제 및 해결방법

### 1. 애플리케이션 시작 문제

#### 1.1 애플리케이션이 시작되지 않는 경우

**증상:**
```
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
```

**원인 및 해결방법:**

1. **포트 충돌**
   ```bash
   # 포트 사용 확인
   netstat -tuln | grep 8080
   lsof -i :8080
   
   # 해결방법: 다른 포트 사용 또는 기존 프로세스 종료
   kill -9 <PID>
   ```

2. **Java 버전 문제**
   ```bash
   # Java 버전 확인
   java -version
   
   # OpenJDK 11 이상 필요
   # 해결방법: 올바른 Java 버전 설치 및 JAVA_HOME 설정
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
   ```

3. **메모리 부족**
   ```bash
   # 메모리 사용량 확인
   free -h
   
   # 해결방법: JVM 힙 메모리 조정
   java -Xmx2g -Xms1g -jar app.jar
   ```

#### 1.2 데이터베이스 연결 실패

**증상:**
```
Could not open JDBC Connection for transaction
java.sql.SQLException: No suitable driver found for jdbc:oracle:thin:@localhost:1521:xe
```

**해결방법:**

1. **Oracle JDBC 드라이버 확인**
   ```bash
   # 의존성 확인
   ./gradlew dependencies | grep oracle
   
   # build.gradle에 올바른 드라이버 포함 확인
   implementation 'com.oracle.database.jdbc:ojdbc8:21.7.0.0'
   ```

2. **Oracle 서비스 상태 확인**
   ```bash
   # Oracle 리스너 상태
   lsnrctl status
   
   # Oracle 인스턴스 상태
   sqlplus / as sysdba
   SELECT instance_name, status FROM v$instance;
   ```

3. **연결 정보 확인**
   ```yaml
   # application.yml 확인
   spring:
     datasource:
       url: jdbc:oracle:thin:@localhost:1521:xe
       username: gpu_admin
       password: gpu_password
   ```

4. **방화벽/네트워크 확인**
   ```bash
   # 포트 연결 테스트
   telnet localhost 1521
   
   # TNS 연결 테스트
   tnsping xe
   ```

### 2. 런타임 문제

#### 2.1 OutOfMemoryError

**증상:**
```
java.lang.OutOfMemoryError: Java heap space
```

**해결방법:**

1. **힙 메모리 증가**
   ```bash
   # JVM 옵션 조정
   java -Xmx4g -Xms2g -jar app.jar
   
   # 또는 환경변수 설정
   export JAVA_OPTS="-Xmx4g -Xms2g"
   ```

2. **메모리 누수 확인**
   ```bash
   # 힙 덤프 생성
   jmap -dump:format=b,file=heap.hprof <pid>
   
   # 메모리 사용량 모니터링
   jstat -gc <pid> 5s
   ```

3. **GC 튜닝**
   ```bash
   # G1GC 사용
   -XX:+UseG1GC -XX:MaxGCPauseMillis=200
   
   # GC 로깅
   -Xlog:gc*:logs/gc.log:time,tags
   ```

#### 2.2 데이터베이스 연결 풀 고갈

**증상:**
```
Unable to acquire JDBC Connection
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

**해결방법:**

1. **연결 풀 설정 조정**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 20
         minimum-idle: 5
         connection-timeout: 30000
         idle-timeout: 600000
         max-lifetime: 1800000
   ```

2. **활성 연결 확인**
   ```sql
   -- Oracle에서 활성 세션 확인
   SELECT username, count(*) 
   FROM v$session 
   WHERE username = 'GPU_ADMIN'
   GROUP BY username;
   ```

3. **느린 쿼리 확인**
   ```sql
   -- 실행 시간이 긴 쿼리 찾기
   SELECT sql_text, elapsed_time, executions
   FROM v$sql
   WHERE elapsed_time > 1000000
   ORDER BY elapsed_time DESC;
   ```

### 3. 배치 작업 문제

#### 3.1 배치 작업이 실행되지 않는 경우

**증상:**
```
No job configuration with the name [gpuMetricsCollectionJob] was registered
```

**해결방법:**

1. **스케줄러 활성화 확인**
   ```java
   // 메인 클래스에 @EnableScheduling 확인
   @SpringBootApplication
   @EnableScheduling
   @EnableBatchProcessing
   public class K8sGpuManagementApplication {
   ```

2. **배치 설정 확인**
   ```yaml
   spring:
     batch:
       job:
         enabled: false  # 자동 실행 비활성화
       initialize-schema: never
   ```

3. **로그 확인**
   ```bash
   # 배치 전용 로그 확인
   tail -f logs/k8s-gpu-batch.log
   ```

#### 3.2 배치 작업 실패

**증상:**
```
Job execution failed: Step [gpuMetricsCollectionStep] completed with errors
```

**해결방법:**

1. **배치 실행 이력 확인**
   ```sql
   -- 실패한 배치 작업 조회
   SELECT job_execution_id, job_name, status, exit_code, exit_message
   FROM batch_job_execution
   WHERE status = 'FAILED'
   ORDER BY start_time DESC;
   ```

2. **스텝 실행 상세 확인**
   ```sql
   -- 스텝별 실행 결과 확인
   SELECT step_name, status, read_count, write_count, exit_message
   FROM batch_step_execution
   WHERE job_execution_id = <FAILED_JOB_ID>;
   ```

3. **청크 크기 조정**
   ```yaml
   app:
     batch:
       metrics-collection:
         chunk-size: 50  # 100에서 50으로 감소
   ```

### 4. API 응답 문제

#### 4.1 API 응답이 느린 경우

**증상:**
- API 응답 시간이 5초 이상
- 타임아웃 오류 발생

**해결방법:**

1. **쿼리 성능 분석**
   ```sql
   -- 실행 계획 확인
   EXPLAIN PLAN FOR
   SELECT * FROM gpu_nodes WHERE cluster_name = 'gpu-cluster-prod';
   
   SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
   ```

2. **인덱스 확인 및 생성**
   ```sql
   -- 누락된 인덱스 확인
   SELECT table_name, column_name
   FROM user_tab_columns
   WHERE table_name = 'GPU_NODES'
   AND column_name NOT IN (
     SELECT column_name FROM user_ind_columns 
     WHERE table_name = 'GPU_NODES'
   );
   
   -- 필요한 인덱스 생성
   CREATE INDEX IDX_GPU_NODES_CLUSTER_STATUS 
   ON GPU_NODES(CLUSTER_NAME, NODE_STATUS);
   ```

3. **페이징 사용**
   ```bash
   # 대용량 데이터 조회 시 페이징 적용
   curl "http://localhost:8080/api/v1/gpu-nodes?page=0&size=20"
   ```

#### 4.2 JSON 직렬화 오류

**증상:**
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException
```

**해결방법:**

1. **Jackson 설정 확인**
   ```yaml
   spring:
     jackson:
       serialization:
         write-dates-as-timestamps: false
       date-format: yyyy-MM-dd HH:mm:ss
   ```

2. **DTO 어노테이션 확인**
   ```java
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime createdDate;
   ```

### 5. Docker 환경 문제

#### 5.1 컨테이너 시작 실패

**증상:**
```
docker-compose up fails with "service 'k8s-gpu-app' failed to build"
```

**해결방법:**

1. **Docker 이미지 빌드 확인**
   ```bash
   # 로그 상세 확인
   docker-compose up --build
   
   # 개별 서비스 빌드
   docker-compose build k8s-gpu-app
   ```

2. **JAR 파일 존재 확인**
   ```bash
   # 빌드 먼저 실행
   ./gradlew clean build
   ls -la build/libs/
   ```

3. **Dockerfile 권한 확인**
   ```dockerfile
   # Dockerfile에서 권한 설정 확인
   RUN chown -R appuser:appuser /app
   USER appuser
   ```

#### 5.2 Oracle 컨테이너 연결 실패

**증상:**
```
ORA-12541: TNS:no listener
```

**해결방법:**

1. **Oracle 컨테이너 상태 확인**
   ```bash
   # 컨테이너 상태 확인
   docker-compose ps
   
   # Oracle 로그 확인
   docker-compose logs oracle-db
   ```

2. **네트워크 연결 확인**
   ```bash
   # 네트워크 내 연결 테스트
   docker-compose exec k8s-gpu-app ping oracle-db
   ```

3. **초기화 대기**
   ```bash
   # Oracle 완전 초기화까지 대기 (2-3분)
   docker-compose exec oracle-db sqlplus system/OraclePassword123@//localhost:1521/XE
   ```

### 6. 성능 문제

#### 6.1 메모리 사용량 과다

**해결방법:**

1. **JVM 메모리 모니터링**
   ```bash
   # 메모리 사용량 실시간 모니터링
   jstat -gc <pid> 5s
   
   # 힙 히스토그램
   jmap -histo <pid>
   ```

2. **애플리케이션 프로파일링**
   ```bash
   # JProfiler, VisualVM 등 사용
   # 또는 Flight Recorder 사용
   -XX:+FlightRecorder
   -XX:StartFlightRecording=duration=60s,filename=app.jfr
   ```

#### 6.2 CPU 사용률 과다

**해결방법:**

1. **스레드 덤프 분석**
   ```bash
   # 스레드 덤프 생성
   jstack <pid> > thread_dump.txt
   
   # 또는 kill -3 사용
   kill -3 <pid>
   ```

2. **배치 작업 최적화**
   ```yaml
   app:
     batch:
       metrics-collection:
         thread-pool-size: 2  # CPU 코어 수에 맞게 조정
   ```

## 🔍 로그 분석

### 로그 레벨별 분석

1. **ERROR 레벨 로그**
   ```bash
   # 에러 로그만 필터링
   grep "ERROR" logs/k8s-gpu-management.log
   
   # 최근 에러 로그
   tail -f logs/k8s-gpu-management-error.log
   ```

2. **WARN 레벨 로그**
   ```bash
   # 경고 로그 확인
   grep "WARN" logs/k8s-gpu-management.log | tail -20
   ```

3. **배치 작업 로그**
   ```bash
   # 배치 관련 로그만 확인
   grep "batch" logs/k8s-gpu-batch.log
   ```

### 로그 설정 변경

```yaml
# logback-spring.xml에서 로그 레벨 조정
logging:
  level:
    com.k8s.gpu: DEBUG  # 상세 디버깅
    org.springframework.batch: INFO
    org.hibernate.SQL: DEBUG  # SQL 쿼리 로깅
```

## 📊 모니터링 및 알림

### 헬스 체크

```bash
# 애플리케이션 상태 확인
curl http://localhost:8080/api/actuator/health

# 상세 헬스 정보
curl http://localhost:8080/api/actuator/health?show-details=always
```

### 메트릭 확인

```bash
# JVM 메트릭
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used

# 데이터베이스 연결 풀 메트릭
curl http://localhost:8080/api/actuator/metrics/hikaricp.connections.active
```

### Prometheus 메트릭

```bash
# Prometheus 형식 메트릭
curl http://localhost:8080/api/actuator/prometheus
```

## 🛠 디버깅 도구

### 1. 원격 디버깅

```bash
# 원격 디버깅 활성화
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
```

### 2. 프로파일링

```bash
# JVM 프로파일링 옵션
-XX:+UnlockCommercialFeatures
-XX:+FlightRecorder
-XX:StartFlightRecording=duration=300s,filename=profile.jfr
```

### 3. 메모리 덤프

```bash
# 힙 덤프 생성
jmap -dump:live,format=b,file=heapdump.hprof <pid>

# Eclipse MAT 또는 VisualVM으로 분석
```

## 🚀 성능 최적화 팁

### 1. 데이터베이스 최적화

```sql
-- 통계 정보 업데이트
EXEC DBMS_STATS.GATHER_SCHEMA_STATS('GPU_ADMIN');

-- 인덱스 재구성
ALTER INDEX IDX_GPU_NODES_CLUSTER REBUILD;

-- 테이블 공간 정리
ALTER TABLE GPU_USAGE_METRICS SHRINK SPACE;
```

### 2. JVM 튜닝

```bash
# G1 GC 사용 (대용량 힙에 적합)
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m

# 큰 객체 처리 최적화
-XX:G1MixedGCLiveThresholdPercent=85
```

### 3. 애플리케이션 최적화

```yaml
# 연결 풀 최적화
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

# 배치 최적화
app:
  batch:
    metrics-collection:
      chunk-size: 100
      thread-pool-size: 4
```

## 📞 지원 요청

문제가 해결되지 않는 경우 다음 정보와 함께 지원을 요청하세요:

### 필수 정보
1. **시스템 정보**
   ```bash
   uname -a
   java -version
   docker --version
   ```

2. **애플리케이션 정보**
   ```bash
   # 프로세스 정보
   ps aux | grep java
   
   # 포트 사용 정보
   