# K8s GPU Management System

Kubernetes 환경의 GPU 리소스 관리 시스템

## 프로젝트 구조

```
k8s-gpu-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── k8s/
│   │   │           └── gpu/
│   │   │               ├── K8sGpuManagementApplication.java
│   │   │               ├── config/
│   │   │               │   ├── SwaggerConfig.java
│   │   │               │   ├── JpaConfig.java
│   │   │               │   ├── DatabaseConfig.java
│   │   │               │   ├── BatchConfig.java
│   │   │               │   └── WebConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── GpuNodeController.java
│   │   │               │   └── GpuDashboardController.java
│   │   │               ├── service/
│   │   │               │   ├── GpuNodeService.java
│   │   │               │   └── GpuDashboardService.java
│   │   │               ├── repository/
│   │   │               │   ├── GpuNodeRepository.java
│   │   │               │   ├── GpuDeviceRepository.java
│   │   │               │   ├── GpuUsageMetricsRepository.java
│   │   │               │   ├── SystemAlertsRepository.java
│   │   │               │   └── GpuAllocationsRepository.java
│   │   │               ├── entity/
│   │   │               │   ├── BaseEntity.java
│   │   │               │   ├── GpuNode.java
│   │   │               │   ├── GpuDevice.java
│   │   │               │   ├── GpuModel.java
│   │   │               │   ├── GpuUsageMetrics.java
│   │   │               │   ├── SystemAlerts.java
│   │   │               │   └── GpuAllocations.java
│   │   │               ├── dto/
│   │   │               │   ├── GpuNodeDto.java
│   │   │               │   └── GpuDashboardDto.java
│   │   │               ├── mapper/
│   │   │               │   └── GpuNodeMapper.java
│   │   │               ├── batch/
│   │   │               │   └── GpuMetricsBatchJob.java
│   │   │               └── exception/
│   │   │                   ├── ResourceNotFoundException.java
│   │   │                   ├── GlobalExceptionHandler.java
│   │   │                   └── ErrorResponse.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/
│               └── k8s/
│                   └── gpu/
│                       └── K8sGpuManagementApplicationTests.java
├── build.gradle
├── settings.gradle
├── gradle/
│   └── wrapper/
├── gradlew
├── gradlew.bat
└── README.md
```

## 기능 구현 상태

### ✅ 완료된 기능

1. **K8s 노드 이력관리**
   - [x] 노드 목록 조회 (검색, 페이징, 정렬)
   - [x] 노드 상세 조회
   - [x] 노드 등록
   - [x] 노드 수정
   - [x] 노드 삭제
   - [x] 노드 상태별 조회
   - [x] 클러스터별 노드 조회
   - [x] 최근 변경된 노드 조회

2. **GPU 대시보드**
   - [x] 전체 GPU 통계
   - [x] 클러스터별 통계
   - [x] GPU 모델별 통계
   - [x] 사용량 추이 조회
   - [x] 활성 알림 조회
   - [x] 최근 활동 조회
   - [x] 실시간 성능 메트릭

3. **GPU 통계 데이터 수집 배치**
   - [x] Spring Batch 기반 메트릭 수집
   - [x] 스케줄 기반 자동 수집 (1분마다)
   - [x] GPU 자원 최적화 스케줄러 (30분마다)
   - [x] 청크 기반 배치 처리

4. **기본 인프라**
   - [x] Spring Boot 2.3.2 설정
   - [x] Oracle OJDBC 7 연동
   - [x] Swagger 2 API 문서화
   - [x] 전역 예외 처리
   - [x] JPA 엔티티 매핑
   - [x] MapStruct DTO 매핑
   - [x] HikariCP 커넥션 풀

## 기술 스택

- **Java**: 11
- **Spring Boot**: 2.3.2.RELEASE
- **Spring Batch**: 배치 처리
- **Spring Data JPA**: 데이터 액세스
- **Oracle Database**: OJDBC 7
- **Swagger**: 2.9.2 (API 문서화)
- **Gradle**: 빌드 도구
- **MapStruct**: DTO 매핑
- **Lombok**: 코드 간소화
- **HikariCP**: 커넥션 풀

## 환경 요구사항

- Windows 10 Pro
- Java 11+
- Oracle Database 12c+
- Gradle 6.0+

## 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd k8s-gpu-management-system
```

### 2. 데이터베이스 설정
- Oracle 데이터베이스에 제공된 DDL 스크립트 실행
- `application.yml`에서 데이터베이스 연결 정보 수정

### 3. 애플리케이션 실행
```bash
# 개발 환경
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/k8s-gpu-management-system-1.0.0.jar
```

### 4. API 문서 확인
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API Docs: http://localhost:8080/api/v2/api-docs

## API 엔드포인트

### GPU 노드 관리
- `GET /api/v1/gpu-nodes` - 노드 목록 조회
- `GET /api/v1/gpu-nodes/{nodeId}` - 노드 상세 조회
- `POST /api/v1/gpu-nodes` - 노드 등록
- `PUT /api/v1/gpu-nodes/{nodeId}` - 노드 수정
- `DELETE /api/v1/gpu-nodes/{nodeId}` - 노드 삭제
- `PATCH /api/v1/gpu-nodes/{nodeId}/status` - 노드 상태 변경

### GPU 대시보드
- `GET /api/v1/dashboard` - 대시보드 전체 데이터
- `GET /api/v1/dashboard/stats/overall` - 전체 통계
- `GET /api/v1/dashboard/stats/clusters` - 클러스터별 통계
- `GET /api/v1/dashboard/stats/models` - 모델별 통계
- `GET /api/v1/dashboard/trends/usage` - 사용량 추이
- `GET /api/v1/dashboard/alerts/active` - 활성 알림
- `GET /api/v1/dashboard/activities/recent` - 최근 활동
- `GET /api/v1/dashboard/metrics/realtime` - 실시간 메트릭

## 배치 작업

### GPU 메트릭 수집
- **스케줄**: 1분마다 실행
- **기능**: 모든 활성 GPU 장비의 성능 메트릭 수집
- **처리 방식**: Spring Batch 청크 기반 처리

### GPU 자원 최적화
- **스케줄**: 30분마다 실행
- **기능**: 만료된 할당 정리, 노드 가용성 업데이트

## 모니터링

### Health Check
- `GET /api/actuator/health` - 애플리케이션 상태 확인

### Metrics
- `GET /api/actuator/metrics` - 애플리케이션 메트릭

## 로깅

- 로그 파일: `logs/k8s-gpu-management.log`
- 로그 레벨: DEBUG (개발), INFO (운영)
- 로그 로테이션: 100MB, 30일 보관

## 개발 가이드

### 코드 스타일
- Lombok 사용으로 boilerplate 코드 최소화
- MapStruct를 통한 DTO 변환
- JPA 네이밍 전략: 물리적 테이블명 그대로 사용

### 예외 처리
- `@RestControllerAdvice`를 통한 전역 예외 처리
- 사용자 친화적인 에러 메시지 제공
- 적절한 HTTP 상태 코드 반환

### 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests com.k8s.gpu.service.GpuNodeServiceTest
```

## 향후 개발 계획

1. **GPU 장비 관리 API** 추가
2. **MIG 인스턴스 관리** 기능
3. **GPU 할당/해제** 워크플로우
4. **알림 시스템** 구현
5. **비용 분석** 리포트
6. **Kubernetes 연동** (실제 클러스터와 동기화)
7. **GPU 메트릭 실시간 수집** (nvidia-smi 연동)

## 라이선스

Apache License 2.0