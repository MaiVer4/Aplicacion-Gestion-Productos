# 🚀 Gestión de Negocio — App Full-Stack

![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-17+-DD0031?logo=angular&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)

Aplicación Full-Stack para la **gestión de productos**, construida con Spring Boot en el backend y Angular (v17+) en el frontend. Ambos servicios, junto con la base de datos MySQL, están publicados como imágenes en Docker Hub y orquestados con Docker Compose, por lo que **cualquier persona puede levantar el sistema completo con un solo comando**, sin instalar Java, Node.js, Angular CLI ni MySQL.

---

## 📐 Arquitectura de Despliegue

Tres contenedores se comunican dentro de una red virtual privada de Docker:

| Contenedor | Imagen | Puerto | Descripción |
|---|---|---|---|
| `mysql-container` | `mysql:8.0` | `3306` | Base de datos. Persiste los datos en un volumen Docker (`mysql_data`) |
| `springboot-container` | `maicoldev/gestion-app-backend:latest` | `8080` | API REST de Spring Boot |
| `angular-container` | `maicoldev/gestion-app-frontend:latest` | `4200` | Interfaz de usuario en Angular |

> El `healthcheck` del contenedor MySQL garantiza que Spring Boot solo arranque cuando la base de datos está completamente lista para recibir conexiones, evitando errores de inicio.

---

## ✅ Requisito Previo

Solo se necesita **una herramienta** instalada en el equipo:

**[Docker Desktop](https://docs.docker.com/get-docker/)** (Windows / Mac) o **Docker Engine** (Linux).

Las versiones modernas de Docker ya incluyen Docker Compose de forma nativa. No se requiere Java, Node.js, Angular CLI, MySQL ni ninguna otra dependencia.

> **¿Versión antigua de Docker?** Si el comando `docker compose` no funciona, prueba con `docker-compose` (con guion).

---

## ⚡ Guía de Instalación y Ejecución

### Paso 1 — Crear la carpeta y el archivo de configuración

Crea una carpeta vacía en tu equipo (por ejemplo `revision-proyecto`) y dentro de ella crea un archivo llamado exactamente **`docker-compose.yml`** con el siguiente contenido:

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
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot"]
      interval: 5s
      timeout: 5s
      retries: 5

  api-app:
    # AQUÍ ESTÁ LA CORRECCIÓN DEL BACKEND
    image: vera1091/gestion-app-api-app:latest
    container_name: springboot-container
    ports:
      - "8080:8080"
    depends_on:
      db-mysql:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db-mysql:3306/negociodb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=root

  angular-frontend:
    # AQUÍ ESTÁ LA CORRECCIÓN DEL FRONTEND
    image: vera1091/gestion-app-angular-frontend:latest
    container_name: angular-container
    ports:
      - "4200:4200"
    depends_on:
      - api-app

volumes:
  mysql_data:
```

---

### Paso 2 — Abrir la terminal y navegar a la carpeta

Abre la terminal de tu sistema operativo:
- **Windows:** PowerShell o CMD
- **Mac / Linux:** Terminal

Navega hasta la carpeta donde guardaste el archivo:

```bash
cd /ruta/de/la/carpeta/revision-proyecto
```

---

### Paso 3 — Lanzar el ecosistema completo

Ejecuta el siguiente comando:

```bash
docker compose up
```

Docker hará todo el trabajo de forma automática en este orden:

1. Descargará las imágenes del backend, frontend y MySQL desde Docker Hub.
2. Creará y configurará la base de datos `negociodb` con sus credenciales.
3. Esperará (gracias al `healthcheck`) a que MySQL esté listo antes de iniciar Spring Boot.
4. Levantará la API REST de Spring Boot.
5. Levantará la interfaz de Angular.

> 💡 **Primera ejecución:** Docker descargará las imágenes desde Docker Hub. Dependiendo de tu conexión esto puede tomar un par de minutos. Las siguientes veces, el arranque es casi inmediato.

---

### Paso 4 — Verificar que todo está en marcha

La aplicación está lista cuando los logs de la terminal se estabilicen. Busca estas líneas como confirmación de Spring Boot:

```
INFO 1 --- [main] o.s.b.w.e.tomcat.TomcatWebServer : Tomcat started on port 8080
INFO 1 --- [main] c.a.g.GestionNegocioApplication  : Started GestionNegocioApplication in X.XXX seconds
```

---

### Paso 5 — Abrir la aplicación en el navegador

Minimiza la terminal **(no la cierres)** y abre tu navegador:

| Qué probar | URL |
|---|---|
| 🖥️ **Interfaz de usuario** (Angular) | [http://localhost:4200](http://localhost:4200) |
| 🔌 **API REST** (Spring Boot) | [http://localhost:8080](http://localhost:8080) |

Desde el frontend puedes registrar, listar y editar productos. Cada operación realiza una petición HTTP a la API en el puerto `8080`, y los datos se persisten en la base de datos MySQL de forma automática.

---

## 🛑 Cómo Detener la Aplicación

Para apagar el entorno de forma segura y liberar los puertos:

1. En la terminal donde corren los logs, presiona **`Ctrl + C`**.
2. Luego ejecuta:

```bash
docker compose down
```

> Si también quieres eliminar los datos almacenados en la base de datos (el volumen), ejecuta `docker compose down -v` en su lugar.

---

## 🗺️ Referencia de Puertos

| Puerto | Servicio | URL |
|--------|----------|-----|
| `4200` | Frontend Angular | http://localhost:4200 |
| `8080` | Backend Spring Boot | http://localhost:8080 |
| `3306` | MySQL | Solo acceso local (no se expone en producción) |

---

## 📦 Decisiones Técnicas

**Imágenes separadas por servicio**
Backend y frontend se publican como imágenes independientes en Docker Hub. Esto permite actualizar o escalar cada capa de forma independiente sin recompilar todo el proyecto.

**Persistencia de datos con volúmenes Docker**
El volumen `mysql_data` garantiza que los datos de la base de datos sobreviven reinicios de contenedores. Solo se pierden si se ejecuta `docker compose down -v` explícitamente.

**Arranque ordenado con healthcheck**
El `healthcheck` de MySQL evita la condición de carrera más común en stacks Docker: Spring Boot intentando conectar antes de que la base de datos esté lista.

---

## 🛠️ Modo Desarrollo (Para Contribuidores)

Si clonas el repositorio completo y quieres modificar el código fuente en tiempo real, desde la raíz del proyecto ejecuta:

```bash
docker compose up --build
```

Esto compilará las imágenes localmente en lugar de descargarlas de Docker Hub. Los cambios en el código se reflejarán al reconstruir.

---

## 📁 Estructura del Repositorio

```
gestion-app/
├── backend/            # Proyecto Spring Boot (Maven)
├── frontend/           # Proyecto Angular
├── docker-compose.yml  # Orquestación de los 3 servicios
└── README.md
```

---

## 🐛 Solución de Problemas Comunes

| Síntoma | Causa probable | Solución |
|---------|---------------|----------|
| `Port 8080 already in use` | Otro proceso usa el puerto | Cierra la app que ocupa ese puerto o cambia `"8080:8080"` a `"9090:8080"` en el `docker-compose.yml` |
| `Port 4200 already in use` | Otra instancia de Angular corriendo | Detén el proceso o cambia el mapeo a `"4201:4200"` |
| `Port 3306 already in use` | MySQL local instalado y corriendo | Detén el servicio MySQL local o cambia el mapeo a `"3307:3306"` |
| Spring Boot no conecta a MySQL | MySQL aún inicializando | Es normal en la primera ejecución; espera el healthcheck. Spring Boot reintentará automáticamente |
| Cambios en el código no se ven | Se usa la imagen de Docker Hub | Reconstruye con `docker compose up --build` |
