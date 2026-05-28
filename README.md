# Gestión de Negocio - App Unificada 🚀

Este proyecto es una aplicación Full-Stack para la gestión de productos, desarrollada con **Spring Boot** en el backend y **Angular (v17+)** en el frontend. Para simplificar el despliegue y optimizar los recursos, ambas aplicaciones han sido unificadas mediante un *Multi-stage build* de Docker en una sola imagen ligera de producción.

---

## 🛠️ Arquitectura de Despliegue

La aplicación se compone de dos contenedores principales que se comunican de forma aislada dentro de una red virtual de Docker:

* **Database Container:** Servidor MySQL 8.0 que persiste los datos del negocio (`negociodb`).
* **App Container (Monolito):** Un contenedor basado en Linux Alpine y un JRE ligero que corre la API de Spring Boot y sirve los archivos estáticos de Angular directamente a través del puerto `8080`.

---

## 🚀 Requisitos Previos

Solo necesitas tener instalado en tu sistema:
* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)

---

## ⚡ Instrucciones para Ejecutar el Proyecto

Sigue estos pasos para poner a correr la aplicación en tu máquina local sin necesidad de compilar código fuente ni instalar dependencias de desarrollo (como Java o Node.js):

### 1. Crear el archivo de configuración
Crea una carpeta vacía en tu equipo, genera un archivo llamado `docker-compose.yml` y pega el siguiente contenido:

```yaml
services:
  db-mysql:
    image: mysql:8.0
    container_name: mysql-container
    restart: always
    environment:
      MYSQL_DATABASE: negociodb
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot"]
      interval: 5s
      timeout: 5s
      retries: 5

  monolito-app:
    image: vera1091/gestion-app-unificada:v1
    container_name: gestion-unificada-container
    ports:
      - "8080:8080"
    depends_on:
      db-mysql:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db-mysql:3306/negociodb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=root
