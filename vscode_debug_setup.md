# VS Code Spring Boot 디버깅 환경 구성 가이드

## 1. 필수 확장 프로그램 설치

VS Code에서 다음 확장 프로그램들을 설치해주세요:

### Java 개발 필수 확장
```
- Extension Pack for Java (Microsoft)
  ├── Language Support for Java™ by Red Hat
  ├── Debugger for Java
  ├── Test Runner for Java
  ├── Maven for Java
  ├── Project Manager for Java
  └── Visual Studio IntelliCode

- Gradle for Java (Microsoft)
- Spring Boot Extension Pack (VMware)
  ├── Spring Boot Tools
  ├── Spring Initializr Java Support
  └── Spring Boot Dashboard
```

### 추가 유용한 확장
```
- Oracle Developer Tools for VS Code (Oracle)
- REST Client (Huachao Mao)
- YAML (Red Hat)
- XML (Red Hat)
- GitLens (GitKraken)
```

## 2. Java 환경 설정

### .vscode/settings.json
```json
{
    "java.home": "C:/Program Files/Java/jdk-11.0.19",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-11",
            "path": "C:/Program Files/Java/jdk-11.0.19",
            "default": true
        }
    ],
    "java.compile.nullAnalysis.mode": "automatic",
    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
    "java.format.settings.profile": "GoogleStyle",
    "java.saveActions.organizeImports": true,
    "java.sources.organizeImports.starThreshold": 99,
    "java.sources.organizeImports.staticStarThreshold": 99,
    "java.test.config": {
        "name": "test",
        "workingDirectory": "${workspaceFolder}",
        "args": [
            "-Dspring.profiles.active=test"
        ],
        "vmArgs": [
            "-Xmx1024m"
        ]
    },
    "spring-boot.ls.problem.application-properties.unknown-property": "ignore"
}
```

## 3. 디버그 구성 설정

### .vscode/launch.json
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "🚀 Spring Boot App (Default)",
            "request": "launch",
            "mainClass": "com.k8s.gpu.K8sGpuManagementApplication",
            "projectName": "k8s-gpu-management-system",
            "args": [],
            "vmArgs": [
                "-Dspring.profiles.active=dev",
                "-Xmx1024m",
                "-Xms512m"
            ],
            "env": {
                "SPRING_PROFILES_ACTIVE": "dev"
            },
            "console": "integratedTerminal",
            "internalConsoleOptions": "neverOpen",
            "stopOnEntry": false,
            "stepFilters": {
                "skipClasses": [
                    "java.lang.ClassLoader"
                ],
                "skipSynthetics": false
            }
        },
        {
            "type": "java",
            "name": "🧪 Spring Boot App (Test Profile)",
            "request": "launch",
            "mainClass": "com.k8s.gpu.K8sGpuManagementApplication",
            "projectName": "k8s-gpu-management-system",
            "args": [],
            "vmArgs": [
                "-Dspring.profiles.active=test",
                "-Xmx512m"
            ],
            "env": {
                "SPRING_PROFILES_ACTIVE": "test"
            },
            "console": "integratedTerminal"
        },
        {
            "type": "java",
            "name": "🐛 Debug Current Test",
            "request": "launch",
            "mainClass": "",
            "projectName": "k8s-gpu-management-system",
            "preLaunchTask": "gradle: test --tests ${fileBasenameNoExtension}",
            "vmArgs": [
                "-Dspring.profiles.active=test"
            ]
        },
        {
            "type": "java",
            "name": "🔌 Remote Debug (Port 5005)",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005,
            "projectName": "k8s-gpu-management-system"
        }
    ]
}
```

## 4. Gradle 태스크 구성

### .vscode/tasks.json
```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "gradle: build",
            "type": "shell",
            "command": "./gradlew",
            "args": ["build"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "options": {
                "cwd": "${workspaceFolder}"
            },
            "problemMatcher": ["$gradle"]
        },
        {
            "label": "gradle: bootRun",
            "type": "shell",
            "command": "./gradlew",
            "args": ["bootRun"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "options": {
                "cwd": "${workspaceFolder}",
                "env": {
                    "SPRING_PROFILES_ACTIVE": "dev"
                }
            },
            "isBackground": true,
            "problemMatcher": [
                {
                    "pattern": [
                        {
                            "regexp": ".",
                            "file": 1,
                            "location": 2,
                            "message": 3
                        }
                    ],
                    "background": {
                        "activeOnStart": true,
                        "beginsPattern": "^.*Tomcat initialized.*",
                        "endsPattern": "^.*Started K8sGpuManagementApplication.*"
                    }
                }
            ]
        },
        {
            "label": "gradle: test",
            "type": "shell",
            "command": "./gradlew",
            "args": ["test"],
            "group": "test",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "options": {
                "cwd": "${workspaceFolder}"
            },
            "problemMatcher": ["$gradle"]
        },
        {
            "label": "gradle: clean",
            "type": "shell",
            "command": "./gradlew",
            "args": ["clean"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "options": {
                "cwd": "${workspaceFolder}"
            }
        }
    ]
}
```

## 5. 개발용 환경 설정 파일

### src/main/resources/application-dev.yml
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: k8s-gpu-management-system
  
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=Oracle
    username: sa
    password: 
    hikari:
      connection-test-query: SELECT 1
      minimum-idle: 2
      maximum-pool-size: 10
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
  
  batch:
    job:
      enabled: false
    initialize-schema: always
  
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Seoul

# 로깅 설정 (개발용)
logging:
  level:
    com.k8s.gpu: DEBUG
    org.springframework.batch: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.web: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always

# 개발용 설정
app:
  gpu:
    metrics:
      collection:
        interval: 30000  # 30초 (개발용)
        batch-size: 10
    alert:
      evaluation:
        interval: 60000  # 1분 (개발용)
    optimization:
      schedule: "0 */5 * * * *"  # 5분마다 (개발용)
  
  batch:
    metrics-collection:
      chunk-size: 10
      thread-pool-size: 2
```

## 6. 디버깅 사용법

### 6.1 기본 디버깅 시작
1. **F5 키** 또는 **Run and Debug** 패널에서 "🚀 Spring Boot App (Default)" 선택
2. 브레이크포인트 설정: 코드 라인 번호 왼쪽 클릭
3. **F10** (Step Over), **F11** (Step Into), **Shift+F11** (Step Out) 사용

### 6.2 테스트 디버깅
1. 테스트 클래스에서 **@Test** 메서드 위의 "Debug Test" 클릭
2. 또는 **Ctrl+Shift+P** → "Java: Debug Test Method" 선택

### 6.3 리모트 디버깅
```bash
# JAR 파일을 디버그 모드로 실행
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar build/libs/k8s-gpu-management-system-1.0.0.jar

# VS Code에서 "🔌 Remote Debug (Port 5005)" 구성 실행
```

## 7. 유용한 디버깅 팁

### 7.1 조건부 브레이크포인트
- 브레이크포인트 우클릭 → "Edit Breakpoint"
- 조건 입력 예: `nodeId.equals("test-node-001")`

### 7.2 로그포인트 (Logpoint)
- 브레이크포인트 대신 로그 출력
- 조건 입력 예: `GPU 노드 조회: {nodeId}`

### 7.3 변수 감시 (Watch)
- **Watch** 패널에서 변수나 표현식 추가
- 실시간으로 값 변화 모니터링

### 7.4 콜 스택 분석
- **Call Stack** 패널에서 메서드 호출 순서 확인
- 각 스택 프레임의 변수 상태 확인

## 8. Spring Boot 특화 디버깅

### 8.1 Spring Boot Dashboard 사용
- **Spring Boot Dashboard** 패널에서 앱 상태 모니터링
- 실행 중인 앱의 로그 실시간 확인

### 8.2 Actuator 엔드포인트 활용
```bash
# Health Check
http://localhost:8080/api/actuator/health

# 환경 정보
http://localhost:8080/api/actuator/env

# 메트릭 정보
http://localhost:8080/api/actuator/metrics
```

### 8.3 H2 콘솔 사용 (개발 환경)
- URL: http://localhost:8080/api/h2-console
- JDBC URL: jdbc:h2:mem:devdb
- 실시간 데이터베이스 상태 확인

## 9. REST API 테스트

### 9.1 REST Client 확장 사용
파일명: `test-api.http`
```http
### GPU 노드 목록 조회
GET http://localhost:8080/api/v1/gpu-nodes
Content-Type: application/json

### GPU 노드 상세 조회
GET http://localhost:8080/api/v1/gpu-nodes/test-node-001
Content-Type: application/json

### GPU 노드 등록
POST http://localhost:8080/api/v1/gpu-nodes
Content-Type: application/json

{
    "nodeId": "debug-node-001",
    "nodeName": "debug-gpu-worker-01",
    "clusterName": "debug-cluster",
    "nodeIp": "192.168.1.200",
    "totalGpus": 4,
    "availableGpus": 4,
    "nodeStatus": "ACTIVE"
}

### 대시보드 데이터 조회
GET http://localhost:8080/api/v1/dashboard
Content-Type: application/json
```

## 10. 문제 해결

### 10.1 일반적인 문제들
```
❌ Java Home이 설정되지 않음
✅ VS Code 설정에서 java.home 경로 확인

❌ Gradle 태스크가 실행되지 않음
✅ ./gradlew 파일 권한 확인 (chmod +x gradlew)

❌ 디버거가 연결되지 않음
✅ 방화벽 설정 및 포트 확인

❌ H2 데이터베이스 연결 실패
✅ application-dev.yml 설정 확인
```

### 10.2 성능 최적화
```json
// .vscode/settings.json
{
    "java.maxConcurrentBuilds": 1,
    "java.import.gradle.java.home": "C:/Program Files/Java/jdk-11.0.19",
    "java.jdt.ls.vmargs": "-XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Dsun.zip.disableMemoryMapping=true -Xmx2G -Xms100m"
}
```

이 설정으로 VS Code에서 Spring Boot 프로젝트를 효율적으로 디버깅할 수 있습니다. 각 구성 요소는 프로젝트의 특성에 맞게 조정하여 사용하세요.