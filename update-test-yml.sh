#!/bin/bash

# yml 파일 경로
YML_FILE="./src/main/resources/deploy-application.yml"

# 변경된 내용을 임시 파일에 저장
ENCODED_APPLICATION_YML=$(mktemp)
cat "$YML_FILE" | base64 > "$ENCODED_APPLICATION_YML"

# github secret에 저장
cat "$ENCODED_APPLICATION_YML" | gh secret set -R 42organization/42gg.server.dev.v2 MIGRATE_APPLICATION_YML

rm "$ENCODED_APPLICATION_YML"
