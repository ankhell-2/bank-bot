FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY . .

RUN ./gradlew bootJar --no-daemon

FROM azul/zulu-openjdk:21-latest

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]