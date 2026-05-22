# Usar la versión de Java 17 (la que usaste para compilar)
FROM eclipse-temurin:17-jdk-alpine

# Crear un volumen temporal
VOLUME /tmp

# Copiar tu archivo ejecutable al contenedor
COPY target/demon-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de tu microservicio (asegúrate que sea el que usas, por defecto 8086)
EXPOSE 8086

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]