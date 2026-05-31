# Stage 1: Build (Etapa de compilación con Maven y Java 17)
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (Etapa de ejecución, imagen ligera solo con JRE 17)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos el .jar generado en la etapa anterior (sea demon o paymentservice)
COPY --from=builder /app/target/*.jar app.jar
# Exponemos tu puerto
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]