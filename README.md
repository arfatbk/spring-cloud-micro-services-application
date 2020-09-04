# Spring Cloud application

    Author: Arfat Bin Kileb

This application contains multiple smaller individual projects (services)

  1. [Spring Eureka server](#spring-eureka-server)
  2. [Customer service](#customer-service)

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

Spring loads `bootrap.yml` before `application.yml`. Hence we will configure server registry here.

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
