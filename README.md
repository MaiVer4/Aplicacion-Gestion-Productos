# 🚀 Gestión de Negocio — App Unificada

![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-17+-DD0031?logo=angular&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)

Aplicación Full-Stack para la **gestión de productos**, construida con Spring Boot en el backend y Angular (v17+) en el frontend. Gracias a un *multi-stage build* de Docker, ambas aplicaciones se empaquetan en una **única imagen ligera de producción**, lista para correr con un solo comando.

---

## 📐 Arquitectura de Despliegue

La aplicación corre mediante dos contenedores que se comunican dentro de una red virtual privada de Docker:

| Contenedor | Tecnología | Descripción |
|---|---|---|
| `mysql-container` | MySQL 8.0 | Base de datos que persiste los datos del negocio (`negociodb`) |
| `gestion-unificada-container` | Alpine JRE | Sirve la API de Spring Boot **y** los archivos estáticos de Angular por el puerto `8080` |

> **¿Por qué un monolito?** Al servir el frontend desde el mismo origen que el backend (`http://localhost:8080`), se eliminan por completo los errores de CORS sin configuración adicional.

---

## ✅ Requisitos Previos

Solo necesitas tener instalado:

- [Docker](https://docs.docker.com/get-docker/) (incluye Docker Compose en versiones modernas)
- [Docker Compose](https://docs.docker.com/compose/install/) (si usas una versión antigua de Docker)

> No se requiere Java, Node.js ni ninguna otra dependencia de desarrollo.

---

## ⚡ Guía de Inicio Rápido

### Paso 1 — Crear el archivo `docker-compose.yml`

Crea una carpeta vacía en tu equipo, genera dentro un archivo llamado `docker-compose.yml` y pega el siguiente contenido:

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
```

---

### Paso 2 — Lanzar la aplicación

Abre la terminal (PowerShell / CMD en Windows, Terminal en Linux/Mac), navega a la carpeta donde guardaste el archivo y ejecuta:

```bash
cd /ruta/de/tu/carpeta/gestion-app-prod
docker compose up
```

> 💡 **Primera ejecución:** Docker descargará las imágenes desde Docker Hub (~400 MB). Dependiendo de tu conexión esto puede tardar un par de minutos. Las ejecuciones siguientes arrancan en menos de 5 segundos.

---

### Paso 3 — Verificar que todo está listo

La aplicación está lista cuando veas estas líneas en la terminal:

```
INFO 1 --- [main] o.s.b.w.a.WelcomePageHandlerMapping : Adding welcome page: class path resource [static/index.html]
INFO 1 --- [main] o.s.b.w.a.TomcatWebServer           : Tomcat started on port 8080 (http) with context path '/'
INFO 1 --- [main] c.a.g.GestionNegocioApplication     : Started GestionNegocioApplication in X.XXX seconds
```

---

### Paso 4 — Abrir la aplicación

Minimiza la terminal (no la cierres) y abre tu navegador en:

**👉 [http://localhost:8080](http://localhost:8080)**

Desde la interfaz puedes registrar, listar y actualizar productos. Las peticiones HTTP van al mismo puerto `8080` y los datos se guardan de forma persistente en MySQL.

---

## 🛑 Cómo Detener la Aplicación

Nunca cierres la terminal de golpe. Sigue estos dos pasos para apagar todo de forma segura:

1. En la terminal donde corren los logs, presiona `Ctrl + C`.
2. Luego ejecuta el siguiente comando para eliminar los contenedores temporales:

```bash
docker compose down
```

---

## 🗺️ Referencia de Puertos

| Puerto | Servicio | Descripción |
|--------|----------|-------------|
| `8080` | App (Spring Boot + Angular) | Interfaz web y API REST |
| `3306` | MySQL | Base de datos (acceso local, no expuesto en prod) |

---

## 📦 Ventajas Técnicas

**Sin errores de CORS**
Angular corre dentro del ecosistema estático de Spring Boot. Ambos comparten el mismo origen (`http://localhost:8080`), así que no existe cruce de dominios.

**Imagen ultra-ligera**
La imagen final pesa ~398 MB, frente a los más de 1.8 GB que ocuparían ambos entornos de desarrollo por separado. Menos RAM, menos disco, más velocidad.

**Arranque con un solo comando**
No hay que compilar código ni instalar SDKs. Con `docker compose up` el entorno completo está en marcha.

---

## 🛠️ Modo Desarrollo (Opcional)

Si clonas el repositorio completo y quieres modificar el código fuente en tiempo real, **no uses** el `docker-compose.yml` de producción anterior. Desde la raíz del proyecto fuente, ejecuta:

```bash
docker compose up --build
```

Esto compilará el código local en caliente. Los servicios quedarán disponibles así:

| Servicio | URL de desarrollo |
|----------|-------------------|
| Frontend Angular | `http://localhost:4200` |
| Backend Spring Boot | `http://localhost:8080` |

---

## 📁 Estructura del Proyecto (Referencia)

```
gestion-app/
├── backend/          # Proyecto Spring Boot (Maven)
├── frontend/         # Proyecto Angular
├── Dockerfile        # Multi-stage build (build Angular → empaqueta con Spring Boot)
└── docker-compose.yml
```

---

## 🐛 Solución de Problemas Comunes

| Síntoma | Causa probable | Solución |
|---------|---------------|----------|
| `Port 8080 already in use` | Otro proceso usa el puerto | Cierra la aplicación que usa ese puerto o cambia `"8080:8080"` a `"9090:8080"` |
| `Port 3306 already in use` | MySQL local corriendo | Detén tu MySQL local o cambia el puerto en el `docker-compose.yml` |
| La app tarda en cargar | MySQL aún no está listo | Espera el healthcheck; Spring Boot reintentará la conexión automáticamente |
| Cambios en el código no se reflejan | Usas la imagen de producción | Usa `docker compose up --build` en modo desarrollo |
