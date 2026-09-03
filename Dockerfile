# 1단계: 빌드
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: 실행
FROM eclipse-temurin:17-jdk
WORKDIR /app
# 컨테이너 기본 타임존이 UTC라 LocalDate.now()/LocalDateTime.now() 등이 KST 기준과
# 9시간 어긋나던 문제를 막기 위해 명시적으로 KST로 고정 (JVM이 TZ 환경변수를 따라감)
ENV TZ=Asia/Seoul
COPY --from=build /app/build/libs/knou-cbt-0.0.1-SNAPSHOT.jar app.jar
RUN useradd --system --no-create-home appuser
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
