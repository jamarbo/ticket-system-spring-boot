
# 🎫 Sistema de Gestión de Tickets

Un sistema de gestión de tickets construido con **Spring Boot 3 (Java 21)** y **PostgreSQL**, totalmente containerizado con **Docker**.

El proyecto está configurado para ser levantado con un solo comando, sin necesidad de tener Java o Maven instalados localmente.

## 🚀 Características

- **API RESTful** para la gestión completa de Tickets (CRUD).
- **Autenticación y Autorización** basada en JWT con Spring Security.
- **Roles de Usuario**: `ADMIN` y `USER`.
- **Búsqueda y filtrado** de tickets.
- **Base de datos PostgreSQL** con migraciones manejadas por Flyway.
- **Build y ejecución autocontenidos** en Docker.

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 21, Spring Boot 3.3.2, Spring Security, Spring Data JPA.
- **Base de Datos**: PostgreSQL 16.
- **Build/Container**: Maven, Docker, Docker Compose.

## � Instalación y Ejecución

Siga estos pasos para levantar todo el entorno.

### Prerrequisitos
- **Git**
- **Docker** y **Docker Compose**

### 1. Clonar el Repositorio
```bash
git clone <URL-DEL-REPOSITORIO>
cd ProyectoTickets
```

### 2. Levantar los Servicios
Ejecute el siguiente comando en la raíz del proyecto. Este comando construirá la imagen de la aplicación (si no existe) y levantará los contenedores de la aplicación y la base de datos.

```bash
docker-compose up --build
```
La primera vez, Docker descargará las imágenes necesarias y Maven construirá el proyecto, lo que puede tardar unos minutos. Las ejecuciones posteriores serán mucho más rápidas.

La aplicación estará disponible en `http://localhost:8080`.

## � API Endpoints (Ejemplos con curl)

Puede utilizar Postman o cualquier otro cliente API, o `curl` como se muestra a continuación.

### 🔐 1. Autenticación (Obtener Token)

Para obtener un token de autenticación, use las credenciales del usuario `admin`.

```bash
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email": "admin@example.com", "password": "admin123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400000
}
```
**Guarde este token para usarlo en las siguientes peticiones.**

### 🎫 2. Crear un Ticket (Sin Autenticación)

Cualquier persona puede crear un ticket. Por defecto, se asignará a un usuario del sistema.

```bash
curl -X POST http://localhost:8080/api/tickets `
  -H "Content-Type: application/json" `
  -d '{"title": "Fallo en la web", "description": "La página de inicio no carga.", "priority": "HIGH"}'
```

### 🎫 3. Crear un Ticket (Autenticado)

Si está autenticado, el ticket se creará a su nombre. Reemplace `<TOKEN>` con el token obtenido en el paso 1.

```bash
curl -X POST http://localhost:8080/api/tickets `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer <TOKEN>" `
  -d '{"title": "Ticket autenticado", "description": "Creado por un admin.", "priority": "LOW"}'
```

### 📋 4. Listar todos los Tickets

Esta operación requiere autenticación.

```bash
curl -X GET http://localhost:8080/api/tickets `
  -H "Authorization: Bearer <TOKEN>"
```

## 🗃️ Base de Datos y Usuarios

- **Motor**: PostgreSQL.
- **Migraciones**: Flyway se encarga de crear el esquema y sembrar los datos iniciales.
- **Usuarios por Defecto**:
  - **Admin**: `admin@example.com` / `admin123`
  - **User**: `user@example.com` / `user123`

## 🧹 Detener el Entorno

Para detener y eliminar los contenedores, presione `Ctrl + C` en la terminal donde ejecutó `docker-compose up`. Si lo ejecutó en modo detached (`-d`), use:

```bash
docker-compose down
```
Para eliminar también los volúmenes (¡esto borrará los datos de la base de datos!):
```bash
docker-compose down -v
```


## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.3.2** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos principal
- **Flyway** - Migración de base de datos
- **Lombok** - Reducción de código boilerplate
- **JWT (jjwt)** - Tokens de autenticación

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para unit tests
- **Spring Boot Test** - Testing de integración
- **Testcontainers** - Testing con PostgreSQL real

### DevOps
- **Docker & Docker Compose** - Containerización
- **Maven** - Gestión de dependencias

## 📦 Instalación y Configuración

### Prerrequisitos
- **Java 17+**
- **Maven 3.8+** 
- **Docker & Docker Compose**
- **Git**

### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd ProyectoTickets
```

### 2. Configurar Base de Datos
```bash
# Iniciar PostgreSQL con Docker
docker compose up -d db

# Verificar que la base de datos esté corriendo
docker compose logs db
```

### 3. Configurar Variables de Entorno (Opcional)
```bash
export DB_URL=jdbc:postgresql://localhost:5432/tickets
export DB_USER=postgres
export DB_PASSWORD=postgres
```

### 4. Ejecutar la Aplicación
```bash
# Opción 1: Con Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Opción 2: Con Maven instalado
mvn spring-boot:run

# Opción 3: Compilar y ejecutar JAR
mvn clean package
java -jar target/tickets-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

## ☁️ Despliegue en Render (sin staging)

1) En Render crea un servicio de base de datos Postgres para este proyecto.
2) Copia la "Internal Database URL" (o la External para DataGrip) y conviértela a JDBC:
  - jdbc:postgresql://<HOST_REAL>:5432/<DB_REAL>?sslmode=require
3) Configura en tu Web Service estas variables:
  - SPRING_PROFILES_ACTIVE=render
  - SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST_REAL>:5432/<DB_REAL>?sslmode=require
  - SPRING_DATASOURCE_USERNAME=<USER>
  - SPRING_DATASOURCE_PASSWORD=<PASSWORD>
  - SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5
  - SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT=0
4) Comandos de Render:
  - Build: mvn -DskipTests clean package
  - Start: java -jar target/tickets-0.0.1-SNAPSHOT.jar
5) Asegúrate de que la rama a desplegar es la que deseas (por ejemplo, `feature/Deploy-to-Render`).

## 🔗 API Endpoints

### 🔐 Autenticación
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400000
}
```

### 📋 Gestión de Tickets

#### Listar Tickets (con filtros y paginación)
```http
GET /api/tickets?q=search&status=OPEN&priority=HIGH&page=0&size=10
Authorization: Bearer <token>
```

#### Crear Ticket
```http
POST /api/tickets
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Nuevo ticket",
  "description": "Descripción del problema",
  "priority": "MEDIUM",
  "tags": "bug,frontend",
  "assignedToId": "uuid-del-usuario"
}
```

#### Obtener Ticket por ID
```http
GET /api/tickets/{id}
Authorization: Bearer <token>
```

#### Actualizar Ticket
```http
PATCH /api/tickets/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Título actualizado",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```

#### Eliminar Ticket (Solo ADMIN)
```http
DELETE /api/tickets/{id}
Authorization: Bearer <token>
```

### 🔍 Parámetros de Búsqueda

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `q` | String | Búsqueda de texto en título y descripción |
| `status` | Enum | Filtrar por estado: `OPEN`, `IN_PROGRESS`, `CLOSED` |
| `priority` | Enum | Filtrar por prioridad: `LOW`, `MEDIUM`, `HIGH` |
| `page` | Integer | Número de página (base 0) |
| `size` | Integer | Cantidad de elementos por página |

## 🗃️ Estructura de Base de Datos

### Tabla: `users`
```sql
id          UUID PRIMARY KEY
email       VARCHAR(255) UNIQUE NOT NULL
password    VARCHAR(255) NOT NULL
role        user_role NOT NULL
created_at  TIMESTAMP NOT NULL
updated_at  TIMESTAMP NOT NULL
```

### Tabla: `tickets`
```sql
id           UUID PRIMARY KEY
title        VARCHAR(120) NOT NULL
description  TEXT
priority     ticket_priority NOT NULL
status       ticket_status NOT NULL
tags         JSONB
created_at   TIMESTAMP NOT NULL
updated_at   TIMESTAMP NOT NULL
created_by   UUID NOT NULL (FK to users)
assigned_to_id UUID (FK to users)
```

### Índices de Rendimiento
- **Índice GIN**: Para búsqueda de texto completo en `title` y `description`
- **Índices B-tree**: En columnas de filtrado frecuente (`status`, `priority`)

## 🧪 Testing

### Ejecutar Todas las Pruebas
```bash
mvn clean test
```

### Ejecutar con Cobertura
```bash
mvn clean test jacoco:report
```

### Tipos de Pruebas Implementadas

#### Unit Tests
- **TicketServiceTest**: Testing de lógica de negocio aislada
- Cobertura de métodos: `findAll`, `createTicket`, `updateTicket`
- Mocking de dependencias con Mockito

#### Integration Tests
- **TicketControllerIT**: Testing end-to-end de endpoints
- **PingControllerIT**: Health check de la aplicación
- Testing con base de datos PostgreSQL real (Testcontainers)

### Resultados de Testing
```
Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
```

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/tickets` |
| `DB_USER` | Usuario de base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de base de datos | `postgres` |
| `JWT_SECRET` | Clave secreta para JWT | (configurada en application.yml) |
| `JWT_EXPIRATION` | Tiempo de expiración del JWT (ms) | `86400000` (24h) |

### Perfiles de Spring

#### Desarrollo (default)
```yaml
spring:
  profiles:
    active: default
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
```

#### Producción
```yaml
spring:
  profiles:
    active: prod
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
logging:
  level:
    org.springframework.security: WARN
```

## 👥 Usuarios por Defecto

La aplicación viene con usuarios predefinidos para testing:

| Email | Password | Rol |
|-------|----------|-----|
| `admin@example.com` | `admin123` | ADMIN |
| `user@example.com` | `user123` | USER |

## 🔒 Seguridad

### Autenticación JWT
- Tokens firmados con HMAC SHA-256
- Expiración configurable (24h por defecto)
- Refresh automático no implementado (fuera del scope)

### Autorización por Roles
- **USER**: Puede crear, leer y modificar tickets
- **ADMIN**: Puede realizar todas las operaciones + eliminar tickets

### CORS
Configurado para permitir requests desde:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)
- `http://localhost:5173` (Vite)
- Dominios de producción configurables

### Validaciones
- **Entrada**: Bean Validation en DTOs
- **Negocio**: Validaciones custom en servicios
- **SQL**: Prepared statements (JPA/Hibernate)

## 📊 Performance

### Métricas Objetivo
- **Lista de tickets**: < 200ms (p99 < 500ms) con 1k registros ✅
- **Búsqueda paginada**: Optimizada con índices GIN ✅
- **Operaciones CRUD**: < 100ms promedio ✅

### Optimizaciones Implementadas
1. **Índice GIN** para búsqueda de texto completo
2. **Paginación** eficiente con Spring Data
3. **JPA Specifications** para filtrado dinámico
4. **Connection pooling** con HikariCP
5. **Lazy loading** de relaciones JPA

## 🚀 Deployment

### Docker Production
```bash
# Construir imagen
docker build -t tickets-app .

# Ejecutar con Docker Compose
docker compose -f docker-compose.prod.yml up -d
```

### Variables de Producción
```env
DB_URL=jdbc:postgresql://prod-db:5432/tickets
DB_USER=tickets_user
DB_PASSWORD=secure_password
JWT_SECRET=your-production-secret-key
SPRING_PROFILES_ACTIVE=prod
```

## 🧹 Arquitectura

### Capas de la Aplicación
```
┌─────────────────┐
│   Controllers   │ ← REST Endpoints + Validation
├─────────────────┤
│    Services     │ ← Business Logic
├─────────────────┤
│  Repositories   │ ← Data Access (JPA)
├─────────────────┤
│    Database     │ ← PostgreSQL
└─────────────────┘
```

### Patrones Implementados
- **Repository Pattern**: Abstracción de acceso a datos
- **DTO Pattern**: Transferencia de datos entre capas
- **Specification Pattern**: Filtrado dinámico
- **Dependency Injection**: Inversión de control
- **Builder Pattern**: Construcción de entidades

## 🐛 Troubleshooting

### Errores Comunes

#### 1. Error de Conexión a Base de Datos
```bash
# Verificar que PostgreSQL esté corriendo
docker compose ps

# Ver logs de la base de datos
docker compose logs db

# Reiniciar servicios
docker compose restart db
```

#### 2. Error de Compilación
```bash
# Limpiar y recompilar
mvn clean compile

# Verificar versión de Java
java -version
```

#### 3. Tests Fallando
```bash
# Ejecutar tests individuales
mvn test -Dtest=TicketServiceTest

# Limpiar target y reejecutar
mvn clean test
```

## 📋 TODO / Mejoras Futuras

### Funcionalidades
- [ ] Comentarios en tickets
- [ ] Historial de cambios (audit log)
- [ ] Notificaciones por email
- [ ] Adjuntos de archivos
- [ ] Dashboard con métricas

### Técnicas
- [ ] Cache con Redis
- [ ] Rate limiting con Redis
- [ ] Monitoring con Actuator + Prometheus
- [ ] API Documentation con OpenAPI/Swagger
- [ ] Refresh tokens para JWT

### DevOps
- [ ] CI/CD pipeline
- [ ] Health checks avanzados
- [ ] Métricas de performance
- [ ] Logging estructurado
- [ ] Backup automático de BD

## 📄 Licencia

Este proyecto fue desarrollado como parte de una prueba técnica.

## 👨‍💻 Decisiones Técnicas y Trade-offs

### Decisiones de Arquitectura

#### 1. **Spring Boot 3 + Java 17**
- **Decisión**: Usar la versión más reciente estable
- **Beneficio**: Performance mejorado, features modernas, soporte LTS
- **Trade-off**: Requiere Java 17+, algunas librerías pueden no ser compatibles

#### 2. **PostgreSQL vs SQLite**
- **Decisión**: PostgreSQL en Docker
- **Beneficio**: Full-text search nativo, JSONB, producción-ready
- **Trade-off**: Más complejo de configurar que SQLite

#### 3. **JPA Specifications vs Criteria API**
- **Decisión**: JPA Specifications para filtrado dinámico
- **Beneficio**: Code más limpio, type-safe, reutilizable
- **Trade-off**: Curva de aprendizaje más alta

#### 4. **JWT Stateless vs Sessions**
- **Decisión**: JWT sin refresh tokens
- **Beneficio**: Stateless, escalable horizontalmente
- **Trade-off**: No se pueden revocar tokens antes de expiración

### Performance Trade-offs

#### 1. **Índice GIN vs Multiple B-tree**
- **Decisión**: Índice GIN combinado para text search
- **Beneficio**: Búsquedas de texto ultra-rápidas
- **Trade-off**: Inserts ligeramente más lentos, más espacio en disco

#### 2. **Eager vs Lazy Loading**
- **Decisión**: Lazy loading por defecto
- **Beneficio**: Memoria eficiente, menos queries innecesarias
- **Trade-off**: N+1 problem potencial, requiere más cuidado

### Seguridad Trade-offs

#### 1. **CORS Permisivo en Development**
- **Decisión**: Permitir múltiples localhost ports
- **Beneficio**: Facilita desarrollo con diferentes frameworks
- **Trade-off**: Menos restrictivo que producción

#### 2. **Password Hashing con BCrypt**
- **Decisión**: BCrypt con strength 12
- **Beneficio**: Seguro contra ataques de fuerza bruta
- **Trade-off**: Login ligeramente más lento (trade-off aceptable)

### Testing Strategy

#### 1. **Testcontainers vs H2**
- **Decisión**: Testcontainers con PostgreSQL real
- **Beneficio**: Tests más realistas, detecta bugs específicos de DB
- **Trade-off**: Tests más lentos, requiere Docker

#### 2. **Integration vs Unit Test Ratio**
- **Decisión**: Más integration tests que unit tests
- **Beneficio**: Mayor confianza en funcionalidad end-to-end
- **Trade-off**: Feedback loop más lento en desarrollo

## ⏱️ Tiempo Invertido

### Breakdown de Desarrollo

| Fase | Tiempo Estimado | Descripción |
|------|----------------|-------------|
| **Setup Inicial** | 1h | Configuración proyecto, Docker, dependencias |
| **Autenticación JWT** | 2h | Security config, JWT service, user management |
| **CRUD Básico** | 2h | Entities, repositories, controllers básicos |
| **Filtrado Avanzado** | 1.5h | JPA Specifications, query parameters |
| **Validaciones** | 1h | Bean validation, error handling |
| **Testing** | 2h | Unit tests, integration tests, Testcontainers |
| **Performance** | 0.5h | Índices de DB, optimizaciones |
| **CORS & Security** | 0.5h | CORS config, security hardening |
| **Documentación** | 1h | README, comments, API docs |
| **Debug & Polish** | 1.5h | Bug fixes, edge cases, code cleanup |

**Total: ~12 horas**

### Distribución de Esfuerzo
- **Backend Core**: 60%
- **Testing**: 20%
- **Setup & Config**: 10%
- **Documentación**: 10%

### Lecciones Aprendidas
1. **Testcontainers** añade complejidad pero mejora significativamente la calidad de tests
2. **JPA Specifications** son poderosas pero requieren entender bien JPA
3. **Spring Security 6** tiene cambios importantes vs versiones anteriores
4. **PostgreSQL full-text search** es muy potente pero requiere índices específicos

---

**Desarrollado con ❤️ en Java 17 + Spring Boot 3**