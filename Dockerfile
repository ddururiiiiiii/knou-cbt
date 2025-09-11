# 1단계: 빌드
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/knou-cbt-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
