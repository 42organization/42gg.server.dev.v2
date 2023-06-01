#!/bin/bash

# yml 파일 경로
YML_FILE="./src/main/resources/application.yml"
TEST_YML_FILE="./src/main/resources/application-test.yml"
MAIN_YML_FILE="./src/main/resources/application-main.yml"

# yml 파일 읽기
YML_CONTENT=$(cat "$YML_FILE")

# active 값을 test, main로 변경
YML_ACTIVE_TEST=$(echo "$YML_CONTENT" | sed 's/active: local/active: test/g')
YML_ACTIVE_MAIN=$(echo "$YML_CONTENT" | sed 's/active: local/active: main/g')

# 변경된 내용을 임시 파일에 저장
ENCODED_YML_ACTIVE_TEST=$(mktemp)
echo "$YML_ACTIVE_TEST" | base64 > "$ENCODED_YML_ACTIVE_TEST"

ENCODED_YML_ACTIVE_MAIN=$(mktemp)
echo "$YML_ACTIVE_MAIN" | base64 > "$ENCODED_YML_ACTIVE_MAIN"

ENCODED_APP_YEST_YML=$(mktemp)
cat "$TEST_YML_FILE" | base64 > "$ENCODED_APP_YEST_YML"

ENCODED_APP_MAIN_YML=$(mktemp)
cat "$MAIN_YML_FILE" | base64 > "$ENCODED_APP_MAIN_YML"

# github secret에 저장
cat "$ENCODED_YML_ACTIVE_TEST" | gh secret set -R 42organization/42gg.server.dev.v2 YML_ACTIVE_TEST
cat "$ENCODED_YML_ACTIVE_MAIN" | gh secret set -R 42organization/42gg.server.dev.v2 YML_ACTIVE_MAIN
cat "$ENCODED_APP_YEST_YML" | gh secret set -R 42organization/42gg.server.dev.v2 APPLICATION_TEST_YML
cat "$ENCODED_APP_MAIN_YML" | gh secret set -R 42organization/42gg.server.dev.v2 APPLICATION_MAIN_YML

rm "$ENCODED_YML_ACTIVE_TEST"
rm "$ENCODED_YML_ACTIVE_MAIN"
rm "$ENCODED_APP_YEST_YML"
rm "$ENCODED_APP_MAIN_YML"
