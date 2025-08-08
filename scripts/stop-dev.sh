#!/bin/bash

echo "🛑 K8s GPU Management System 개발환경 중지..."

# 모든 컨테이너 중지
docker-compose down

echo "🧹 사용하지 않는 Docker 리소스 정리 중..."
docker system prune -f

echo "✅ 개발환경 중지 완료!"