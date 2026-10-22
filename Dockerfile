# ==============================================================================
# Etapa 1: Build (Compilación y empaquetado con Maven y Java 21)
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia del descriptor Maven para cachear la descarga de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia del código fuente y empaquetado del artefacto excluyendo tests
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# Etapa 2: Runtime (Imagen ligera Alpine basada en Eclipse Temurin JRE 21)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creación de usuario y grupo no root para mayor seguridad en producción
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia del binario JAR ejecutable desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

# Cambiar a usuario no privilegiado
USER appuser

# Puerto estándar expuesto por el microservicio de pagos
EXPOSE 8086

# Variables de entorno con valores por defecto (preparadas para ser inyectadas en producción)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0" \
    R2DBC_URL="" \
    DB_USERNAME="" \
    DB_PASSWORD=""

# ENTRYPOINT optimizado:
# - Ejecuta con 'exec' para que la JVM reciba las señales del sistema (SIGTERM/SIGINT) directamente como PID 1.
# - Admite la inyección dinámica de variables de entorno (SPRING_PROFILES_ACTIVE, R2DBC_URL, DB_USERNAME, DB_PASSWORD).
# - Permite pasar argumentos adicionales al comando 'docker run' mediante "$@".
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar \"$@\"", "--"]