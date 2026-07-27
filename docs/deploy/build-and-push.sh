#!/bin/bash
# 로컬(또는 개발 PC)에서 실행: 이미지를 빌드하고 Docker Hub로 push 합니다.
# 사용법: ./build-and-push.sh your-dockerhub-id 0.0.1

set -e

DOCKERHUB_ID=${1:?"Docker Hub 아이디를 입력하세요. 예: ./build-and-push.sh myid 0.0.1"}
VERSION=${2:-0.0.1}
IMAGE="$DOCKERHUB_ID/hotel-backend:$VERSION"

echo ">> Docker Hub 로그인"
docker login

echo ">> 이미지 빌드: $IMAGE"
docker build -f Dockerfile.prod -t "$IMAGE" .

echo ">> latest 태그도 추가"
docker tag "$IMAGE" "$DOCKERHUB_ID/hotel-backend:latest"

echo ">> push"
docker push "$IMAGE"
docker push "$DOCKERHUB_ID/hotel-backend:latest"

echo ">> 완료: $IMAGE"
