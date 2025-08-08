# K8s GPU Management System API 문서

## 개요

K8s GPU Management System은 Kubernetes 환경의 GPU 리소스를 관리하기 위한 RESTful API를 제공합니다.

- **Base URL**: `http://localhost:8080/api`
- **API 문서**: `http://localhost:8080/api/swagger-ui/`
- **API 스펙**: `http://localhost:8080/api/v2/api-docs`

## 인증 및 권한

현재 버전에서는 기본 인증을 사용하지 않습니다. 운영 환경에서는 JWT 또는 OAuth2 인증 구현을 권장합니다.

## API 엔드포인트

### 1. GPU 노드 관리 API

#### 1.1 노드 목록 조회
```http
GET /api/v1/gpu-nodes
```

**파라미터:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)
- `sortBy` (optional): 정렬 필드 (기본값: createdDate)
- `sortDirection` (optional): 정렬 방향 (ASC/DESC, 기본값: DESC)
- `nodeName` (optional): 노드명 검색 (부분 검색)
- `clusterName` (optional): 클러스터명 필터
- `nodeStatus` (optional): 노드 상태 필터 (ACTIVE/INACTIVE/MAINTENANCE/FAILED)
- `fromDate` (optional): 검색 시작일 (yyyy-MM-dd'T'HH:mm:ss)
- `toDate` (optional): 검색 종료일 (yyyy-MM-dd'T'HH:mm:ss)

**응답 예시:**
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
```http
GET /api/v1/gpu-nodes/{nodeId}
```

**응답 예시:**
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
```http
POST /api/v1/gpu-nodes
```

**요청 본문:**
```json
{
  "nodeId": "node-gpu-001",
  "nodeName": "k8s-gpu-worker-01",
  "clusterName": "gpu-cluster-prod",
  "nodeIp": "192.168.1.101",
  "totalGpus": 8,
  "availableGpus": 8,
  "nodeStatus": "ACTIVE",
  "kubernetesVersion": "v1.28.2",
  "dockerVersion": "24.0.5",
  "nvidiaDriverVersion": "535.154.05",
  "nodeLabels": "{\"gpu.type\":\"RTX4090\",\"zone\":\"us-west-1a\"}",
  "taints": "{\"nvidia.com/gpu\":\"NoSchedule\"}"
}
```

#### 1.4 노드 수정
```http
PUT /api/v1/gpu-nodes/{nodeId}
```

#### 1.5 노드 삭제
```http
DELETE /api/v1/gpu-nodes/{nodeId}
```

#### 1.6 노드 상태 변경
```http
PATCH /api/v1/gpu-nodes/{nodeId}/status?status=MAINTENANCE
```

#### 1.7 특수 조회 API

**활성 노드 목록:**
```http
GET /api/v1/gpu-nodes/active
```

**사용 가능한 GPU가 있는 노드:**
```http
GET /api/v1/gpu-nodes/available
```

**클러스터별 노드 조회:**
```http
GET /api/v1/gpu-nodes/cluster/{clusterName}
```

**최근 변경된 노드:**
```http
GET /api/v1/gpu-nodes/recent?hours=24
```

### 2. GPU 대시보드 API

#### 2.1 대시보드 전체 데이터
```http
GET /api/v1/dashboard
```

**응답 예시:**
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
  ]
}
```

#### 2.2 전체 통계
```http
GET /api/v1/dashboard/stats/overall
```

#### 2.3 클러스터별 통계
```http
GET /api/v1/dashboard/stats/clusters
```

#### 2.4 GPU 모델별 통계
```http
GET /api/v1/dashboard/stats/models
```

#### 2.5 사용량 추이
```http
GET /api/v1/dashboard/trends/usage?hours=24
```

**응답 예시:**
```json
[
  {
    "timestamp": "2025-08-01T14:00:00",
    "overallUtilization": 76.5,
    "memoryUtilization": 68.2,
    "averageTemperature": 67.8,
    "totalPowerConsumption": 18245.3
  }
]
```

#### 2.6 활성 알림
```http
GET /api/v1/dashboard/alerts/active
```

**응답 예시:**
```json
[
  {
    "alertId": "alert-001",
    "alertType": "TEMPERATURE",
    "severity": "HIGH",
    "targetType": "DEVICE",
    "targetId": "gpu-001",
    "message": "GPU 온도가 임계값 80°C를 초과했습니다 (현재: 84.2°C)",
    "createdDate": "2025-08-01T14:30:00",
    "status": "ACTIVE"
  }
]
```

#### 2.7 최근 활동
```http
GET /api/v1/dashboard/activities/recent?limit=50
```

#### 2.8 실시간 성능 메트릭
```http
GET /api/v1/dashboard/metrics/realtime
```

## 에러 응답 형식

모든 API는 표준화된 에러 응답 형식을 사용합니다:

```json
{
  "timestamp": "2025-08-01T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "GPU 노드를 찾을 수 없습니다. nodeId: invalid-id",
  "path": "/api/v1/gpu-nodes/invalid-id"
}
```

### HTTP 상태 코드

- `200 OK`: 성공적인 조회
- `201 Created`: 성공적인 생성
- `204 No Content`: 성공적인 삭제
- `400 Bad Request`: 잘못된 요청 (유효성 검증 실패)
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 중복 데이터 또는 비즈니스 규칙 위반
- `500 Internal Server Error`: 서버 내부 오류

## 유효성 검증

### 노드 ID
- 필수 값
- 최대 50자
- 유니크해야 함

### 노드명
- 필수 값
- 최대 100자
- 유니크해야 함

### IP 주소
- IPv4 형식 유효성 검증
- 예: `192.168.1.100`

### 노드 상태
- 허용 값: `ACTIVE`, `INACTIVE`, `MAINTENANCE`, `FAILED`

## 페이징 및 정렬

### 페이징 파라미터
- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본값: 20, 최대: 100)

### 정렬 파라미터
- `sortBy`: 정렬 필드 (예: `createdDate`, `nodeName`, `totalGpus`)
- `sortDirection`: 정렬 방향 (`ASC` 또는 `DESC`)

### 페이징 응답 구조
```json
{
  "content": [...],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true,
  "numberOfElements": 20
}
```

## 필터링 및 검색

### 노드 검색 조건
- **텍스트 검색**: `nodeName` 파라미터로 노드명 부분 검색
- **상태 필터**: `nodeStatus` 파라미터로 특정 상태 필터링
- **클러스터 필터**: `clusterName` 파라미터로 특정 클러스터 필터링
- **날짜 범위**: `fromDate`, `toDate` 파라미터로 생성일 기준 범위 검색

### 예시
```http
GET /api/v1/gpu-nodes?nodeName=worker&nodeStatus=ACTIVE&clusterName=prod&page=0&size=10&sortBy=createdDate&sortDirection=DESC
```

## 모니터링 및 헬스체크

### 헬스체크
```http
GET /api/actuator/health
```

**응답 예시:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Oracle",
        "validationQuery": "SELECT 1 FROM DUAL"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 334904631296,
        "threshold": 10485760
      }
    }
  }
}
```

### 메트릭
```http
GET /api/actuator/metrics
```

### 프로메테우스 메트릭
```http
GET /api/actuator/prometheus
```

## 개발 가이드

### API 클라이언트 예시

#### cURL
```bash
# 노드 목록 조회
curl -X GET "http://localhost:8080/api/v1/gpu-nodes?page=0&size=10" \
  -H "Accept: application/json"

# 노드 등록
curl -X POST "http://localhost:8080/api/v1/gpu-nodes" \
  -H "Content-Type: application/json" \
  -d '{
    "nodeId": "test-node",
    "nodeName": "test-worker",
    "clusterName": "test-cluster",
    "nodeStatus": "ACTIVE",
    "totalGpus": 4,
    "availableGpus": 4
  }'
```

#### JavaScript (Fetch API)
```javascript
// 노드 목록 조회
const response = await fetch('/api/v1/gpu-nodes?page=0&size=10');
const data = await response.json();

// 노드 등록
const newNode = {
  nodeId: 'test-node',
  nodeName: 'test-worker',
  clusterName: 'test-cluster',
  nodeStatus: 'ACTIVE',
  totalGpus: 4,
  availableGpus: 4
};

const response = await fetch('/api/v1/gpu-nodes', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(newNode)
});
```

#### Python (requests)
```python
import requests

# 노드 목록 조회
response = requests.get('http://localhost:8080/api/v1/gpu-nodes', 
                       params={'page': 0, 'size': 10})
data = response.json()

# 노드 등록
new_node = {
    'nodeId': 'test-node',
    'nodeName': 'test-worker',
    'clusterName': 'test-cluster',
    'nodeStatus': 'ACTIVE',
    'totalGpus': 4,
    'availableGpus': 4
}

response = requests.post('http://localhost:8080/api/v1/gpu-nodes', 
                        json=new_node)
```

## OpenAPI 스펙

전체 OpenAPI 3.0 스펙은 다음 URL에서 확인할 수 있습니다:
- JSON: `http://localhost:8080/api/v2/api-docs`
- YAML: `http://localhost:8080/api/v2/api-docs?format=yaml`

Swagger UI를 통한 대화형 API 문서:
- `http://localhost:8080/api/swagger-ui/`

## 변경 이력

### v1.0.0 (2025-08-01)
- 초기 API 릴리스
- GPU 노드 관리 API
- GPU 대시보드 API
- 기본 모니터링 엔드포인트