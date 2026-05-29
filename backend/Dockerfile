# Etapa 1: Compilar el código fuente
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos el pom.xml y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos saltándonos los tests para agilizar el despliegue del instructor
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el jar directamente desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
