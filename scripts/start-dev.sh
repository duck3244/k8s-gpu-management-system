#!/bin/bash

echo "🚀 K8s GPU Management System 개발환경 시작..."

# 환경 변수 파일 확인
if [ ! -f .env ]; then
    echo "⚠️  .env 파일이 없습니다. .env.example을 복사하여 .env 파일을 생성하세요."
    cp .env.example .env
    echo "📝 .env 파일이 생성되었습니다. 필요한 값들을 수정하세요."
fi

# Docker 컨테이너 시작
echo "🐳 Docker 컨테이너 시작 중..."
docker-compose up -d oracle-db

echo "⏳ Oracle 데이터베이스 초기화 대기 중... (약 2-3분)"
docker-compose exec oracle-db bash -c "
until sqlplus system/OraclePassword123@//localhost:1521/XE < /dev/null; do
    echo '데이터베이스 준비 중...'
    sleep 10
done
echo '✅ Oracle 데이터베이스 준비 완료!'
"

# 데이터베이스 스키마 생성
echo "📊 데이터베이스 스키마 생성 중..."
docker-compose exec oracle-db sqlplus system/OraclePassword123@//localhost:1521/XE @/container-entrypoint-initdb.d/01-create-user.sql

# 애플리케이션 빌드
echo "🔨 애플리케이션 빌드 중..."
./gradlew clean build -x test

# 전체 서비스 시작
echo "🚀 전체 서비스 시작 중..."
docker-compose up -d

echo "✅ 개발환경 시작 완료!"
echo ""
echo "📋 서비스 접근 정보:"
echo "   - API 서버: http://localhost:8080/api"
echo "   - API 문서: http://localhost:8080/api/swagger-ui/"
echo "   - 헬스체크: http://localhost:8080/api/actuator/health"
echo "   - Grafana: http://localhost:3000 (admin/admin123)"
echo "   - Prometheus: http://localhost:9090"
echo "   - Oracle DB: localhost:1521/XE (gpu_admin/gpu_password)"
echo ""
echo "📝 로그 확인: docker-compose logs -f k8s-gpu-app"