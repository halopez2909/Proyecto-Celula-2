#  Order Service - Microservicio de Gestión de Pedidos (Día 1)

Este microservicio forma parte del ecosistema de e-commerce del **Proyecto Célula 2**. Se encarga de la creación de órdenes, gestión de ítems y mantenimiento de estados de pedidos.

## Avance
- **Arquitectura Base**: Configuración completa de Spring Boot 3 trabajando en el puerto `8083`.
- **Persistencia**: Implementación de modelos relacionales (Órdenes e Ítems) en **PostgreSQL**.
- **Seguridad**: Integración exitosa con el servicio de Autenticación (Puerto `8081`) mediante **Bearer Tokens**.
- **Trazabilidad**: Consumo y registro del header `X-Correlation-Id` para seguimiento de peticiones.

---

##  Stack Tecnológico
- **Java 17** / **Spring Boot 3**
- **Spring Data JPA**: Gestión de base de datos.
- **PostgreSQL**: Almacenamiento persistente.
- **JUnit/Mockito**: (Opcional) Preparado para pruebas unitarias.

---

##  Guía de Uso Rápido

### 1. Obtener Token (Auth Service)
* **Endpoint**: `POST http://localhost:8081/auth/login`
* **Credenciales**: `{"username": "admin", "password": "password"}`

### 2. Crear una Orden (Order Service)
* **Endpoint**: `POST http://localhost:8083/orders`
* **Auth**: Seleccionar `Bearer Token` y pegar el token obtenido.
* **Headers**: 
    - `X-Correlation-Id`: `RETO-TEST-001` (o cualquier ID de seguimiento).
* **Body (JSON)**:
```json
{
    "userId": 1,
    "items": [
        {
            "productId": 10,
            "quantity": 2
        }
    ]
}


 Resultado Esperado (200 OK)
El servicio responderá con el objeto creado, incluyendo el id generado por la base de datos y el estado automático CREATED.

JSON

{
    "id": 1,
    "userId": 1,
    "status": "CREATED",
    "createdAt": "2026-03-03...",
    "correlationId": "RETO-TEST-001",
    "items": [...]
}
