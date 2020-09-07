# Spring Cloud application

    Author: Arfat Bin Kileb

This application contains multiple smaller individual projects (services)

  1. [Spring Eureka server](#spring-eureka-server)
  2. [Customer service](#customer-service)
  3. [OAuthServer](#oauthserver)

## Spring Eureka server

:file_folder: discovery-server

:memo: application.yml

    server:
        port: 9999
    spring:
        application:
            name: dicovery-server
    eureka:
        client:
            register-with-eureka: false
            fetch-registry: false

## Customer service

:file_folder: customer-service

Spring loads `bootstrap.yml` before `application.yml`. Hence we will configure server registry here.

Because of `server.port=0`, `port` will be dynamically assigned each time customer-service runs.

:memo: bootstrap.yml

    spring:
        application:
            name: customer-service
    server:
        port: 0

    eureka:
        instance:
            hostname: localhost
            instanceId: ${spring.application.name}-${random.int}
    client:
        register-with-eureka: true
        fetch-registry: true
        service-url:
            defaultZone: http://localhost:9999/eureka

## OAuthServer
:file_folder: OAuthServer

OAuth server will run on `port 8282`. This is defined in :memo: application.yml
    
    server:
      port: 8282
      
- Getting `access_token`

> Default endpoint to get `access token` is `<host>/oauth/token`, and 
to check token `<host>/oauth/check_token`
 
Request will look something like this:

    curl --request POST \
      --url http://localhost:8282/oauth/token \
      --header 'authorization: Basic bW9iaWxlOnBpbg==' \
      --header 'content-type: application/x-www-form-urlencoded' \
      --data grant_type=password \
      --data username=arfat \
      --data password=pass123 \
      

- Checking `access_token`

This request will validate `access_token`

    curl --request GET \
      --url 'http://localhost:8282/oauth/check_token?\
            token=f27fca4d-e0d8-4ac4-b9b0-b9d8dfee79f3' \
      --header 'authorization: Basic bW9iaWxlOnBpbg=='
      
- Generate new `access_token` with `refresh_token`

This request will generate new `access_token`

    curl --request POST \
      --url http://localhost:8282/oauth/token \
      --header 'authorization: Basic bW9iaWxlOnBpbg==' \
      --header 'content-type: application/x-www-form-urlencoded' \
      --data grant_type=refresh_token \
      --data refresh_token=a56ccec4-df05-488a-8a5f-ec0ce38f4bc4

> `authorization` header is to provide client credentials

