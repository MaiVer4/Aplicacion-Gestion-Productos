# Etapa 1: Compilar Angular
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build -- --configuration=production

# Etapa 2: Meter Angular en Spring Boot y compilar el JAR
FROM eclipse-temurin:21-jdk-alpine AS backend-builder
WORKDIR /app
COPY backend/.mvn/ .mvn
COPY backend/mvnw backend/pom.xml ./
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
# Inyectar el build de Angular en los recursos estáticos de Spring Boot
COPY --from=frontend-builder /app/dist/gestion-productos/browser/ ./src/main/resources/static/
RUN ./mvnw clean package -DskipTests

# Etapa 3: Imagen única final ejecutable
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
