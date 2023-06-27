# 42arcade.gg.server.v2


## ⚡️ 프로젝트 소개
42 서울 내에서 탁구 경기 매칭, 전적 서비스를 제공하는 프로젝트 입니다.
<br>
향후 추가 서비스 확장 예정
<br>

## ⚡️ 기술 스택

<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/spring_boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring_security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/apache_tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=white">
<img src="https://img.shields.io/badge/linux-FCC624?style=for-the-badge&logo=linux&logoColor=black">
<img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=aws&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/DOCKER-007396?style=for-the-badge&logo=DOCKER&logoColor=white">


## ⚡️ 프로젝트 관리
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/JIRA-0052CC?style=for-the-badge&logo=jirasoftware&logoColor=white">

## ⚡️ 프로젝트 개발기간
- 2023.04.16 ~ 2023.06.23

## ⚡️ 프로젝트 아키텍처
<img alt="systemArchitecture" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/c51e8d73-d8f2-4f5e-935c-325ec263857e" >&nbsp;&nbsp;&nbsp;&nbsp;

## ⚡️ V1과 달라진 점

### ⚡️⚡ 로그인 연동 추가
- v1에서 지원하지 않던 카카오계정 연동 기능 추가(좌 : v1, 우: v2) </br></br>
  <img width=25% alt="loginv1" src="https://user-images.githubusercontent.com/58678617/177508304-6d7d2e49-5b07-4d6a-a5b9-59c5f3ddb6ce.png" >&nbsp;&nbsp;&nbsp;&nbsp;
  <img width=50% alt="loginv2" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/f63aa7ae-4c60-4fbc-a192-037bf880a03f" >&nbsp;&nbsp;&nbsp;&nbsp;


### ⚡️⚡ DB table 구조 변경
- v1에서 확장을 위해 열어둔 구조나 테이블마다 여러 곳에 있던 중복된 속성 제거
- 테이블 수 감소 :  14 -> 12

<img width=90% alt="erdv1" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/4570fa91-1311-4d6f-a5ee-b356bcd95fbc" >&nbsp;&nbsp;&nbsp;&nbsp;
</br>
<img width=90% alt="erdv2" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/b698ec11-ad81-4504-8a52-2c3d2c8e63b1" >&nbsp;&nbsp;&nbsp;&nbsp;

### ⚡️⚡ 게임추가 기능
- v1에서 1개의 예약만 되던 것에서 최대 3개까지 예약을 잡을 수 있도록 변경
  </br></br>
  <div style="text-align : center;">
  <img width=60% alt="matchv2" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/2b6e53d7-48de-4d43-8a4d-5f45ebf11097" >&nbsp;&nbsp;&nbsp;&nbsp;
  </div>

### ⚡️⚡ 도커 도입
- v2에서 도커 도입을 통해 컨테이너를 통한 서버 관리 도입
</br>
<div style="text-align : center;">
<img width=80% alt="dockerPs" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/cd4d2d37-4082-4bd1-99a6-bb1728be1700" >&nbsp;&nbsp;&nbsp;&nbsp;
 </div>

### ⚡️⚡ 모니터링 도입
- grafana를 통한 서버 모니터링 도입
</br>
<div style="text-align : center;">
<img width=80% alt="dockerPs" src="https://github.com/42organization/42gg.server.dev.v2/assets/67796301/7da03342-14ed-47c7-9183-a68ad663109c" >&nbsp;&nbsp;&nbsp;&nbsp;
 </div>

## ⚡️ 팀소개

<table>
  <thead>
    <tr>
        <td align=center>🏓</td>
        <td align=center>🏓</td>
        <td align=center>🏓</td>
        <td align=center>🏓</td>
        <td align=center>👨🏻‍💻</td>
    </tr>
  </thead>
    <tr>
        <td align=center><a href="https://github.com/AYoungSn">안영선 @yoahn</a></td>
        <td align=center><a href="https://github.com/greatSweetMango">김재혁 @jaehyuki</a></td>
        <td align=center><a href="https://github.com/kmularise">김의진 @yuikim</a></td>
        <td align=center><a href="https://github.com/wken5577">이현규 @hyunkyle</a></td>
        <td align=center><a href="https://github.com/FeFe2200">이 철 @cheolee</a></td>
    </tr>
    <tr>
        <td align=center>PM<br>Game기능 담당</td>
        <td align=center>DB 마이그레이션&관리,<br>알림,관리자 기능 일부담당</td>
        <td align=center>매칭 기능,<br>
로그인기능 일부담당,<br>관리자기능 일부담당</td>
        <td align=center>로그인기능,<br>인프라 담당</td>
        <td align=center>관리자 기능,<br>로그 담당</td>
    </tr>

</table>



## ⚡️ 필요 파일
다음과 같은 양식의 "application.xml"파일이 "src/main/resources/"경로에 필요합니다.
```
spring:
  profiles:
    active: main

  security:
    oauth2.client:
      authenticationScheme: ""
      registration:
        42:
          redirect-uri: ""
          authorization-grant-type: ""
          scope: public
        kakao:
          redirect-uri: ""
          authorization-grant-type: ""
          scope: ""
      provider:
        42:
          authorization-uri: ""
          token-uri: ""
          user-info-uri: ""
          user-name-attribute: ""
        kakao:
          authorization-uri: ""
          token-uri: ""
          user-info-uri: ""
          user-name-attribute: ""

  mvc:
    hiddenmethod:
      filter:
        enabled: ""
  data:
    web:
      pageable:
        default-page-size: ""
        one-indexed-parameters: ""

  mail:
    host: ""
    port: ""
    username: ""
    password: ""
    properties:
      mail:
        smtp:
          starttls:
            enable: ""
            required: ""
          auth: ""

  # Message 설정
  messages:
    basename: ""
    encoding: ""

springdoc:
  swagger-ui:
    path: ""
  default-consumes-media-type: ""
  default-produces-media-type: ""

app:
  auth:
    tokenSecret: ""
    refreshTokenSecret: ""

info:
  image:
    defaultUrl: '유저 기본 이미지 경로'

---
spring.config.activate.on-profile: main
spring:
  # main server에서는 swagger-ui를 사용하지 않음
  springdoc:
    swagger-ui:
      enabled: ""

  # 데이터 소스 설정
  datasource:
    url: ""
    driverClassName: ""
    user: ""
    password: ""

  flyway:
    enabled: ""
    baselineOnMigrate: ""
    locations: ""
    url: ""
    user: ""
    password: ""

  jpa:
    database-platform: ""
    hibernate:
      ddl-auto: ""
    properties:
      hibernate:
        show_sql: ""
        format_sql: ""
        use_sql_comments: ""

  security:
    oauth2.client:
      registration:
        42:
          client-id: ""
          client-secret: ""
        kakao:
          client-id: ""
          client-secret: ""
          client-authentication-method: ""

  # Redis 설정
  cache:
    type: ""
  redis:
    host: ""
    port: ""

# cors 설정
cors:
  allowed-origins: ""
  allowed-methods: ""
  allowed-headers: ""
  allowed-credentials: ""
  max-age: ""

info:
  web:
    frontUrl: ""
    domain: ""

cloud:
  aws:
    credentials:
      accessKey: ""
      secretKey: ""
    s3:
      bucket: ""
      dir: ""
    region:
      static: ""
    stack:
      auto: ""

slack:
  xoxbToken: ""

app:
  auth:
    tokenExpiry: ""
    refreshTokenExpiry: ""

```
