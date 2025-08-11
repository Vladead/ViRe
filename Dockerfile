FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN ./mvnw -B dependency:go-offline
COPY src ./src
RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:24-jre-alpine AS runtime
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
RUN apk add --no-cache curl
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]