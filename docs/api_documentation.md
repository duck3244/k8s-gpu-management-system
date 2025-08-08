# K8s GPU Management System API Documentation

## 개요

K8s GPU Management System은 Kubernetes 환경에서 GPU 리소스를 효율적으로 관리하기 위한 RESTful API를 제공합니다.

- **Base URL**: `http://localhost:8080/api`
- **API Version**: v1
- **Content-Type**: `application/json`
- **Authentication**: 현재 버전에서는 인증 없음 (향후 JWT 토큰 기반 인증 예정)

## 📋 API 목록

### 1. GPU 노드 관리 API

#### 1.1 노드 목록 조회
**GET** `/v1/gpu-nodes`

GPU 노드 목록을 조회합니다. 검색 조건, 페이징, 정렬을 지원합니다.

**Query Parameters:**
- `nodeName` (string, optional): 노드명 부분 검색
- `clusterName` (string, optional): 클러스터명 필터
- `nodeStatus` (string, optional): 노드 상태 필터 (`ACTIVE`, `INACTIVE`, `MAINTENANCE`, `FAILED`)
- `fromDate` (datetime, optional): 검색 시작일 (ISO 8601 형식)
- `toDate` (datetime, optional): 검색 종료일 (ISO 8601 형식)
- `page` (integer, optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (integer, optional, default: 20): 페이지 크기
- `sortBy` (string, optional, default: createdDate): 정렬 필드
- `sortDirection` (string, optional, default: DESC): 정렬 방향 (`ASC`, `DESC`)

**Response Example:**
```json
{
  "content": [
    {
      "nodeId": "node-gpu-001",
      "nodeName": "k8s-gpu-worker-01",
      "clusterName": "gpu-cluster-prod",
      "nodeIp": "192.168.1.101",
      "totalGpus": 8,
      "availableGpus": 3,
      "nodeStatus": "ACTIVE",
      "kubernetesVersion": "v1.28.2",
      "dockerVersion": "24.0.5",
      "nvidiaDriverVersion": "535.154.05",
      "createdDate": "2025-08-01T10:00:00",
      "updatedDate": "2025-08-01T15:30:00"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1
}
```

#### 1.2 노드 상세 조회
**GET** `/v1/gpu-nodes/{nodeId}`

특정 GPU 노드의 상세 정보를 조회합니다.

**Path Parameters:**
- `nodeId` (string, required): 조회할 노드 ID

**Response Example:**
```json
{
  "nodeId": "node-gpu-001",
  "nodeName": "k8s-gpu-worker-01",
  "clusterName": "gpu-cluster-prod",
  "nodeIp": "192.168.1.101",
  "totalGpus": 8,
  "availableGpus": 3,
  "nodeStatus": "ACTIVE",
  "kubernetesVersion": "v1.28.2",
  "dockerVersion": "24.0.5",
  "nvidiaDriverVersion": "535.154.05",
  "nodeLabels": "{\"gpu.type\":\"RTX4090\",\"zone\":\"us-west-1a\"}",
  "taints": "{\"nvidia.com/gpu\":\"NoSchedule\"}",
  "createdDate": "2025-08-01T10:00:00",
  "updatedDate": "2025-08-01T15:30:00"
}
```

#### 1.3 노드 등록
**POST** `/v1/gpu-nodes`

새로운 GPU 노드를 등록합니다.

**Request Body:**
```json
{
  "nodeId": "node-gpu-009",
  "nodeName": "k8s-gpu-worker-09",
  "clusterName": "gpu-cluster-prod",
  "nodeIp": "192.168.1.109",
  "totalGpus": 8,
  "availableGpus": 8,
  "nodeStatus": "ACTIVE",
  "kubernetesVersion": "v1.28.2",
  "dockerVersion": "24.0.5",
  "nvidiaDriverVersion": "535.154.05",
  "nodeLabels": "{\"gpu.type\":\"RTX4090\",\"zone\":\"us-west-1c\"}",
  "taints": "{\"nvidia.com/gpu\":\"NoSchedule\"}"
}
```

**Response:** 등록된 노드 정보 (201 Created)

#### 1.4 노드 수정
**PUT** `/v1/gpu-nodes/{nodeId}`

기존 GPU 노드 정보를 수정합니다.

**Path Parameters:**
- `nodeId` (string, required): 수정할 노드 ID

**Request Body:** 노드 등록과 동일한 형식 (nodeId 제외)

#### 1.5 노드 삭제
**DELETE** `/v1/gpu-nodes/{nodeId}`

GPU 노드를 삭제합니다. (GPU 장비가 있는 노드는 삭제 불가)

**Path Parameters:**
- `nodeId` (string, required): 삭제할 노드 ID

**Response:** 204 No Content

#### 1.6 노드 상태 변경
**PATCH** `/v1/gpu-nodes/{nodeId}/status`

GPU 노드의 상태를 변경합니다.

**Path Parameters:**
- `nodeId` (string, required): 상태를 변경할 노드 ID

**Query Parameters:**
- `status` (string, required): 변경할 상태 (`ACTIVE`, `INACTIVE`, `MAINTENANCE`, `FAILED`)

#### 1.7 활성 노드 목록 조회
**GET** `/v1/gpu-nodes/active`

활성 상태인 GPU 노드 목록을 조회합니다.

#### 1.8 사용 가능한 GPU 노드 조회
**GET** `/v1/gpu-nodes/available`

사용 가능한 GPU가 있는 노드 목록을 조회합니다.

#### 1.9 클러스터별 노드 조회
**GET** `/v1/gpu-nodes/cluster/{clusterName}`

특정 클러스터의 GPU 노드 목록을 조회합니다.

**Path Parameters:**
- `clusterName` (string, required): 클러스터명

#### 1.10 최근 변경된 노드 조회
**GET** `/v1/gpu-nodes/recent`

최근 변경된 GPU 노드 목록을 조회합니다.

**Query Parameters:**
- `hours` (integer, optional, default: 24): 조회 시간(시간)

### 2. GPU 대시보드 API

#### 2.1 대시보드 전체 데이터 조회
**GET** `/v1/dashboard`

GPU 클러스터의 전체 대시보드 데이터를 조회합니다.

**Response Example:**
```json
{
  "overallStats": {
    "totalNodes": 12,
    "activeNodes": 10,
    "totalGpus": 96,
    "usedGpus": 73,
    "availableGpus": 23,
    "overallUtilization": 76.04,
    "averageTemperature": 67.5,
    "totalPowerConsumption": 18500.5,
    "migEnabledGpus": 15,
    "totalMigInstances": 42
  },
  "clusterStats": [
    {
      "clusterName": "gpu-cluster-prod",
      "nodeCount": 8,
      "totalGpus": 64,
      "usedGpus": 48,
      "utilizationRate": 75.0,
      "status": "HEALTHY"
    }
  ],
  "modelStats": [
    {
      "modelName": "GeForce RTX 4090",
      "architecture": "Ada Lovelace",
      "deviceCount": 16,
      "usedDevices": 12,
      "utilizationRate": 75.0,
      "averageTemperature": 68.5,
      "averagePowerConsumption": 285.7
    }
  ],
  "usageTrends": [
    {
      "timestamp": "2025-08-01T10:00:00",
      "overallUtilization": 76.5,
      "memoryUtilization": 68.2,
      "averageTemperature": 67.8,
      "totalPowerConsumption": 18245.3
    }
  ],
  "activeAlerts": [
    {
      "alertId": "alert-001",
      "alertType": "TEMPERATURE",
      "severity": "HIGH",
      "targetType": "DEVICE",
      "targetId": "gpu-001",
      "message": "GPU 온도가 임계값을 초과했습니다",
      "createdDate": "2025-08-01T14:30:00",
      "status": "ACTIVE"
    }
  ],
  "recentActivities": [
    {
      "activityType": "GPU_ALLOCATION",
      "description": "GPU가 pod 'training-job-001'에 할당되었습니다",
      "target": "gpu-device-001",
      "user": "ai-team",
      "timestamp": "2025-08-01T15:45:00",
      "status": "SUCCESS"
    }
  ]
}
```

#### 2.2 전체 통계 조회
**GET** `/v1/dashboard/stats/overall`

GPU 클러스터의 전체 통계를 조회합니다.

#### 2.3 클러스터별 통계 조회
**GET** `/v1/dashboard/stats/clusters`

클러스터별 GPU 통계를 조회합니다.

#### 2.4 GPU 모델별 통계 조회
**GET** `/v1/dashboard/stats/models`

GPU 모델별 통계를 조회합니다.

#### 2.5 사용량 추이 조회
**GET** `/v1/dashboard/trends/usage`

지정된 시간 동안의 GPU 사용량 추이를 조회합니다.

**Query Parameters:**
- `hours` (integer, optional, default: 24): 조회 시간(시간)

#### 2.6 활성 알림 조회
**GET** `/v1/dashboard/alerts/active`

현재 활성 상태인 알림 목록을 조회합니다.

#### 2.7 최근 활동 조회
**GET** `/v1/dashboard/activities/recent`

최근 GPU 관련 활동 내역을 조회합니다.

**Query Parameters:**
- `limit` (integer, optional, default: 50): 조회 건수

#### 2.8 실시간 성능 메트릭 조회
**GET** `/v1/dashboard/metrics/realtime`

모든 GPU의 실시간 성능 메트릭을 조회합니다.

**Response Example:**
```json
[
  {
    "deviceId": "gpu-device-001",
    "gpuUtilization": 85.7,
    "memoryUtilization": 73.2,
    "temperature": 72.5,
    "powerDraw": 320.5,
    "fanSpeed": 65.0,
    "timestamp": "2025-08-01T16:00:00"
  }
]
```

## 📊 HTTP 상태 코드

| 상태 코드 | 설명 | 사용 케이스 |
|-----------|------|-------------|
| 200 OK | 성공 | 조회, 수정 성공 |
| 201 Created | 생성됨 | 리소스 생성 성공 |
| 204 No Content | 내용 없음 | 삭제 성공 |
| 400 Bad Request | 잘못된 요청 | 유효성 검증 실패, 잘못된 매개변수 |
| 404 Not Found | 찾을 수 없음 | 리소스가 존재하지 않음 |
| 409 Conflict | 충돌 | 중복된 데이터, 삭제할 수 없는 상태 |
| 500 Internal Server Error | 서버 오류 | 서버 내부 오류 |

## 🚨 에러 응답 형식

모든 에러는 다음 형식으로 응답됩니다:

```json
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "GPU 노드를 찾을 수 없습니다. nodeId: invalid-id",
  "details": {
    "field": "nodeId",
    "rejectedValue": "invalid-id"
  },
  "path": "/api/v1/gpu-nodes/invalid-id"
}
```

## 🔍 검색 및 필터링

### 노드 검색 조건
- **부분 검색**: `nodeName` 매개변수는 LIKE 검색을 지원합니다
- **정확 일치**: `clusterName`, `nodeStatus`는 정확한 값만 매칭됩니다
- **날짜 범위**: `fromDate`, `toDate`를 사용하여 생성일 기준 범위 검색이 가능합니다
- **페이징**: `page`와 `size`로 결과를 페이지별로 나누어 조회할 수 있습니다
- **정렬**: `sortBy`와 `sortDirection`으로 원하는 필드 기준 정렬이 가능합니다

### 지원되는 정렬 필드
- `nodeId`: 노드 ID
- `nodeName`: 노드명
- `clusterName`: 클러스터명
- `totalGpus`: 총 GPU 수
- `availableGpus`: 사용 가능한 GPU 수
- `createdDate`: 생성일 (기본값)
- `updatedDate`: 수정일

## 🔐 보안 및 인증

현재 버전에서는 인증이 필요하지 않지만, 향후 릴리스에서는 다음과 같은 보안 기능을 제공할 예정입니다:

- **JWT 토큰 기반 인증**
- **역할 기반 접근 제어 (RBAC)**
- **API 키 인증**
- **요청 제한 (Rate Limiting)**

## 📈 페이징 및 정렬

### 페이징 매개변수
```
GET /v1/gpu-nodes?page=0&size=20&sortBy=createdDate&sortDirection=DESC
```

### 응답에서 페이징 정보
```json
{
  "content": [...],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0
  },
  "totalElements": 50,
  "totalPages": 3,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "size": 20,
  "number": 0
}
```

## 🎯 사용 사례별 API 호출 예시

### 1. 새로운 GPU 노드 추가 워크플로우

```bash
# 1. 중복 확인을 위해 기존 노드 검색
curl -X GET "http://localhost:8080/api/v1/gpu-nodes?nodeName=worker-10"

# 2. 새 노드 등록
curl -X POST "http://localhost:8080/api/v1/gpu-nodes" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeId": "node-gpu-010",
    "nodeName": "k8s-gpu-worker-10",
    "clusterName": "gpu-cluster-prod",
    "nodeIp": "192.168.1.110",
    "totalGpus": 8,
    "availableGpus": 8,
    "nodeStatus": "ACTIVE",
    "kubernetesVersion": "v1.28.2",
    "dockerVersion": "24.0.5",
    "nvidiaDriverVersion": "535.154.05"
  }'

# 3. 등록 확인
curl -X GET "http://localhost:8080/api/v1/gpu-nodes/node-gpu-010"
```

### 2. 클러스터 상태 모니터링

```bash
# 1. 전체 대시보드 데이터 조회
curl -X GET "http://localhost:8080/api/v1/dashboard"

# 2. 특정 클러스터의 노드 상태 확인
curl -X GET "http://localhost:8080/api/v1/gpu-nodes/cluster/gpu-cluster-prod"

# 3. 활성 알림 확인
curl -X GET "http://localhost:8080/api/v1/dashboard/alerts/active"

# 4. 실시간 성능 메트릭 확인
curl -X GET "http://localhost:8080/api/v1/dashboard/metrics/realtime"
```

### 3. 노드 유지보수 워크플로우

```bash
# 1. 노드를 유지보수 모드로 변경
curl -X PATCH "http://localhost:8080/api/v1/gpu-nodes/node-gpu-001/status?status=MAINTENANCE"

# 2. 유지보수 완료 후 활성 상태로 변경
curl -X PATCH "http://localhost:8080/api/v1/gpu-nodes/node-gpu-001/status?status=ACTIVE"

# 3. 노드 정보 업데이트 (드라이버 버전 등)
curl -X PUT "http://localhost:8080/api/v1/gpu-nodes/node-gpu-001" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeName": "k8s-gpu-worker-01",
    "clusterName": "gpu-cluster-prod",
    "nodeIp": "192.168.1.101",
    "totalGpus": 8,
    "availableGpus": 8,
    "nodeStatus": "ACTIVE",
    "nvidiaDriverVersion": "545.23.08"
  }'
```

### 4. 리소스 사용량 분석

```bash
# 1. 최근 24시간 사용량 추이
curl -X GET "http://localhost:8080/api/v1/dashboard/trends/usage?hours=24"

# 2. GPU 모델별 통계
curl -X GET "http://localhost:8080/api/v1/dashboard/stats/models"

# 3. 클러스터별 통계
curl -X GET "http://localhost:8080/api/v1/dashboard/stats/clusters"

# 4. 최근 활동 내역
curl -X GET "http://localhost:8080/api/v1/dashboard/activities/recent?limit=100"
```

## 🧪 테스트 가이드

### 단위 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests com.k8s.gpu.controller.GpuNodeControllerTest

# 테스트 커버리지 리포트 생성
./gradlew jacocoTestReport
```

### API 테스트 (Postman Collection)

프로젝트에는 다음과 같은 Postman Collection이 포함되어 있습니다:
- `tests/postman/K8s-GPU-Management-API.postman_collection.json`
- `tests/postman/environments/development.postman_environment.json`

### 통합 테스트

```bash
# H2 in-memory 데이터베이스를 사용한 통합 테스트
./gradlew test --tests "*IntegrationTest"

# 특정 통합 테스트 실행
./gradlew test --tests com.k8s.gpu.integration.GpuNodeIntegrationTest
```

## 📊 성능 및 제한사항

### API 성능
- **노드 목록 조회**: 평균 응답 시간 < 100ms (1000개 노드 기준)
- **대시보드 데이터**: 평균 응답 시간 < 500ms (복합 쿼리)
- **실시간 메트릭**: 평균 응답 시간 < 200ms

### 제한사항
- **페이지 크기**: 최대 1000개
- **검색 결과**: 최대 10,000개
- **동시 요청**: 초당 최대 100개 (향후 설정 가능)

### 권장사항
- 대용량 데이터 조회 시 페이징 사용
- 실시간 메트릭은 캐싱을 통해 성능 최적화
- 복잡한 통계 쿼리는 배치 처리 결과 활용

## 🔧 문제 해결

### 일반적인 오류

#### 1. 404 Not Found
```json
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "GPU 노드를 찾을 수 없습니다. nodeId: invalid-id"
}
```
**해결방법**: 요청한 리소스 ID가 올바른지 확인

#### 2. 400 Bad Request (유효성 검증 실패)
```json
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "입력값 검증에 실패했습니다",
  "details": {
    "nodeId": "노드 ID는 필수입니다",
    "nodeName": "노드명은 필수입니다"
  }
}
```
**해결방법**: 필수 필드가 모두 포함되었는지, 데이터 형식이 올바른지 확인

#### 3. 409 Conflict (중복 데이터)
```json
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "이미 존재하는 노드 ID입니다: node-gpu-001"
}
```
**해결방법**: 고유한 ID나 이름을 사용

### 디버깅 팁
1. **로그 확인**: `logs/k8s-gpu-management.log` 파일 참조
2. **헬스 체크**: `GET /api/actuator/health`로 시스템 상태 확인
3. **메트릭 확인**: `GET /api/actuator/metrics`로 성능 지표 확인

## 📚 추가 자료

- **OpenAPI 스펙**: `http://localhost:8080/api/v3/api-docs`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui/`
- **프로젝트 README**: `README.md`
- **배포 가이드**: `docs/deployment-guide.md`
- **트러블슈팅**: `docs/troubleshooting.md`

## 🔄 버전 관리

현재 API 버전: **v1**

### 버전 호환성
- **v1**: 현재 안정 버전
- **v2**: 계획 중 (향후 MIG 인스턴스 관리, 고급 할당 기능 포함)

### API 변경 정책
- **하위 호환성**: 기존 v1 API는 v2 릴리스 후에도 최소 1년간 지원
- **Deprecation**: 중단 예정 API는 최소 6개월 전에 공지
- **Breaking Changes**: 새로운 메이저 버전에서만 허용

---

## 📞 지원 및 피드백

- **이슈 리포팅**: GitHub Issues
- **개발팀 연락**: k8s-gpu-team@company.com
- **API 개선 제안**: Pull Request 환영
- **문서 개선**: docs/ 폴더의 파일들에 대한 개선 제안 환영

이 문서는 지속적으로 업데이트됩니다. 최신 버전은 프로젝트 저장소에서 확인하세요.