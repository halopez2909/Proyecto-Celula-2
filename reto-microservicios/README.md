# Reto Microservicios - Sprint 1
## Plataforma Distribuida de Gestión de Pedidos

---

## Arquitectura

```
Cliente (Postman / Angular)
        │
        ▼
┌─────────────────────┐
│  API Gateway :8080   │  ← Punto único de entrada
│  - JWT Filter        │  ← Valida tokens en rutas protegidas
│  - CorrelationId     │  ← Genera X-Correlation-Id
└────────┬────────────┘
         │
    ┌────┼────────────┐
    ▼    ▼            ▼
 :8081  :8082       :8083
 Auth   Catalog     Orders
  │      │            │
  ▼      ▼            ▼
authdb  catalogdb   orderdb     ← PostgreSQL (Docker)
```

---

## Requisitos Previos

- Java 21 (LTS)
- Maven 3.9+
- Docker Desktop
- Postman

### Verificar instalación
```bash
java -version        # openjdk 21.x.x
mvn -version         # Apache Maven 3.9.x
docker --version     # Docker 2x.x.x
docker compose version
```

---

## Paso 1: Levantar infraestructura (Docker)

```bash
cd reto-microservicios

# Si tenías un postgres anterior, limpia el volumen:
docker compose down -v

# Levantar PostgreSQL + RabbitMQ
docker compose up -d

# Verificar que estén corriendo
docker ps
```

Deberías ver 2 contenedores:
- `reto_postgres` → puerto 5432
- `reto_rabbitmq` → puertos 5672 + 15672

### Verificar que se crearon las 3 bases de datos:
```bash
docker exec -it reto_postgres psql -U reto -d authdb -c "\l"
```
Debes ver: `authdb`, `catalogdb`, `orderdb`

### RabbitMQ UI:
- http://localhost:15672
- Usuario: `reto` / Contraseña: `reto`

---

## Paso 2: Levantar los servicios Java (cada uno en una terminal separada)

### Terminal 1 - Auth Service
```bash
cd services/auth-service
mvn spring-boot:run
```
✅ Debe iniciar en puerto **8081** y crear la tabla `users` automáticamente.

### Terminal 2 - Catalog Service
```bash
cd services/catalog-service
mvn spring-boot:run
```
✅ Debe iniciar en puerto **8082**.

### Terminal 3 - Order Service
```bash
cd services/order-service
mvn spring-boot:run
```
✅ Debe iniciar en puerto **8083**.

### Terminal 4 - API Gateway
```bash
cd services/api-gateway
mvn spring-boot:run
```
✅ Debe iniciar en puerto **8080**.

> **IMPORTANTE**: El Gateway se levanta AL FINAL porque necesita que los demás servicios estén arriba.

---

## Paso 3: Verificar con Postman

### 3.1 Health Checks (todos deben responder `{"status": "UP"}`)
```
GET http://localhost:8080/actuator/health    ← Gateway
GET http://localhost:8081/actuator/health    ← Auth
GET http://localhost:8082/actuator/health    ← Catalog
GET http://localhost:8083/actuator/health    ← Orders
```

### 3.2 Routing a través del Gateway
```
GET http://localhost:8080/catalog/ping   ← debe llegar a catalog-service
GET http://localhost:8080/orders/ping    ← debe llegar a order-service
```
⚠️ Estos retornarán **401** porque el Gateway requiere JWT para /catalog/** y /orders/**.

### 3.3 Registrar un usuario
```
POST http://localhost:8080/auth/register
Content-Type: application/json

{
    "email": "admin@test.com",
    "password": "admin123",
    "role": "ADMIN"
}
```
Respuesta esperada: `201 Created`

### 3.4 Login (obtener JWT)
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
    "email": "admin@test.com",
    "password": "admin123"
}
```
Respuesta esperada: `200 OK` con token JWT.

### 3.5 Probar rutas protegidas CON token
```
GET http://localhost:8080/catalog/ping
Header: Authorization: Bearer <PEGAR_TOKEN_AQUI>
```
Respuesta esperada: `200 OK` con `{ "service": "catalog-service", "status": "ok" }`

### 3.6 Probar sin token → 401
```
GET http://localhost:8080/orders/ping
(sin header Authorization)
```
Respuesta esperada: `401 Unauthorized`

### 3.7 Probar con token inválido → 401
```
GET http://localhost:8080/orders/ping
Header: Authorization: Bearer token.invalido.aqui
```
Respuesta esperada: `401 Unauthorized`

---

## Estructura del Proyecto

```
reto-microservicios/
├── docker-compose.yml          ← PostgreSQL + RabbitMQ
├── init-db.sql                 ← Crea catalogdb y orderdb
├── README.md
└── services/
    ├── api-gateway/            ← Puerto 8080
    │   ├── pom.xml
    │   └── src/main/
    │       ├── java/com/reto/gateway/
    │       │   ├── ApiGatewayApplication.java
    │       │   └── filter/
    │       │       ├── CorrelationIdFilter.java
    │       │       └── JwtAuthFilter.java
    │       └── resources/application.yml
    │
    ├── auth-service/           ← Puerto 8081
    │   ├── pom.xml
    │   └── src/main/
    │       ├── java/com/reto/auth/
    │       │   ├── AuthServiceApplication.java
    │       │   ├── config/SecurityConfig.java
    │       │   ├── controller/AuthController.java
    │       │   ├── dto/
    │       │   │   ├── LoginRequest.java
    │       │   │   ├── RegisterRequest.java
    │       │   │   └── TokenResponse.java
    │       │   ├── entity/
    │       │   │   ├── Role.java
    │       │   │   └── UserEntity.java
    │       │   ├── repository/UserRepository.java
    │       │   └── security/JwtService.java
    │       └── resources/application.yml
    │
    ├── catalog-service/        ← Puerto 8082
    │   ├── pom.xml
    │   └── src/main/
    │       ├── java/com/reto/catalog/
    │       │   ├── CatalogServiceApplication.java
    │       │   └── controller/PingController.java
    │       └── resources/application.yml
    │
    └── order-service/          ← Puerto 8083
        ├── pom.xml
        └── src/main/
            ├── java/com/reto/order/
            │   ├── OrderServiceApplication.java
            │   └── controller/PingController.java
            └── resources/application.yml
```

---

## Criterios de Aceptación Sprint 1

| HU  | Criterio                          | Cómo verificar                                    |
|-----|-----------------------------------|----------------------------------------------------|
| HU1 | Ruta a Auth funciona              | POST /auth/login responde                          |
| HU1 | Ruta a Catalog funciona           | GET /catalog/ping con JWT → 200                    |
| HU1 | Ruta a Orders funciona            | GET /orders/ping con JWT → 200                     |
| HU2 | Genera JWT firmado                | Login retorna token con sub, role, exp             |
| HU2 | Token expira                      | Token tiene exp (15 min)                           |
| HU2 | 401 si credenciales inválidas     | Login con password mal → 401                       |
| —   | X-Correlation-Id se propaga       | Response headers incluyen X-Correlation-Id         |
| —   | Health checks funcionan           | /actuator/health → UP en todos los servicios       |

---

## Troubleshooting

**Puerto 5432 ocupado:**
Si ya tienes PostgreSQL local, cámbialo o detenlo antes de hacer `docker compose up`.

**Gateway no enruta:**
Revisa que los 3 servicios estén levantados ANTES de probar el Gateway.

**JWT no valida:**
El `secret` en `application.yml` del Gateway y del Auth Service DEBEN ser idénticos.

**"Connection refused" a la base de datos:**
Asegúrate de que el contenedor `reto_postgres` esté corriendo: `docker ps`
