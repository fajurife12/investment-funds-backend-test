# Funds Platform API

API reactiva para gestión de fondos de inversión. Construida con **Java 21**, **Spring Boot 4.0.3** y **WebFlux** siguiendo **Clean Architecture**.

---

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints](#endpoints)
- [Ejecución local](#ejecución-local)
- [Pruebas](#pruebas)
- [Despliegue en AWS](#despliegue-en-aws)
- [Variables de entorno](#variables-de-entorno)

---

## Arquitectura

El proyecto sigue **Clean Architecture** con tres capas bien definidas:

```
domain/               → Entidades, Ports (Output Ports), excepciones de negocio
application/          → Use Cases (lógica pura, sin dependencias de frameworks)
infrastructure/       → Adaptadores: MongoDB, Notificaciones, API (WebFlux)
```

La regla de dependencia se respeta estrictamente: el dominio no importa nada de infraestructura ni de Spring.

### Estructura de paquetes

```
com.funds
├── application
│   ├── FundsApplication.java
│   └── usecase
│       ├── SubscribeFundUseCase.java
│       ├── CancelFundUseCase.java
│       └── GetTransactionHistoryUseCase.java
├── domain
│   ├── model
│   │   ├── Fund.java
│   │   ├── Client.java
│   │   ├── Transaction.java
│   │   ├── Notification.java
│   │   ├── FundCategory.java
│   │   ├── TransactionType.java
│   │   └── NotificationPreference.java
│   ├── port
│   │   ├── FundRepositoryOutputPort.java
│   │   ├── ClientRepositoryOutputPort.java
│   │   ├── TransactionRepositoryOutputPort.java
│   │   └── NotificationOutputPort.java
│   └── exception
│       ├── DomainException.java
│       └── ErrorCode.java
└── infrastructure
    ├── adapter
    │   ├── persistence
    │   │   ├── document
    │   │   ├── repository
    │   │   ├── FundAdapter.java
    │   │   ├── ClientAdapter.java
    │   │   └── TransactionAdapter.java
    │   ├── notification
    │   │   ├── EmailAdapter.java
    │   │   ├── SmsAdapter.java
    │   │   └── NotificationDispatcher.java
    │   └── api
    │       ├── dto
    │       │   ├── FundDtos.java
    │       │   └── FundMapper.java
    │       ├── FundRouter.java
    │       ├── FundHandler.java
    │       └── GlobalExceptionHandler.java
    └── config
        ├── ApplicationConfig.java
        └── DataInitializer.java
```

---

## Modelo de datos

### Colección `funds`

```json
{
  "_id": "1",
  "name": "FPV_BTG_PACTUAL_RECAUDADORA",
  "minimumAmount": 75000,
  "category": "FPV"
}
```

### Colección `clients`

```json
{
  "_id": "client-001",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "phone": "+573001234567",
  "notificationPreference": "EMAIL",
  "balance": 500000,
  "activeFundIds": ["1", "3"]
}
```

> `activeFundIds` está desnormalizado intencionalmente: permite verificar suscripciones en O(1) sin joins.

### Colección `transactions`

```json
{
  "_id": "550e8400-e29b-41d4-a716-446655440000",
  "clientId": "client-001",
  "fundId": "1",
  "fundName": "FPV_BTG_PACTUAL_RECAUDADORA",
  "type": "SUBSCRIPTION",
  "amount": 75000,
  "createdAt": "2024-03-15T10:30:00"
}
```

> El `_id` es un UUID generado en el dominio, no delegado a MongoDB. Índice en `clientId` para consultas rápidas del historial.

### Fondos disponibles

| ID | Nombre | Monto mínimo | Categoría |
|----|--------|-------------|-----------|
| 1 | FPV_BTG_PACTUAL_RECAUDADORA | COP $75.000 | FPV |
| 2 | FPV_BTG_PACTUAL_ECOPETROL | COP $125.000 | FPV |
| 3 | DEUDAPRIVADA | COP $50.000 | FIC |
| 4 | FDO-ACCIONES | COP $250.000 | FIC |
| 5 | FPV_BTG_PACTUAL_DINAMICA | COP $100.000 | FPV |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/v1/funds` | Listar todos los fondos disponibles |
| `POST` | `/api/v1/funds/{fundId}/subscribe` | Suscribirse a un fondo |
| `DELETE` | `/api/v1/funds/{fundId}/subscribe?clientId={id}` | Cancelar suscripción |
| `GET` | `/api/v1/clients/{clientId}/transactions` | Historial de transacciones |
| `GET` | `/actuator/health` | Health check |

### POST `/api/v1/funds/{fundId}/subscribe`

**Request body:**
```json
{
  "clientId": "client-001"
}
```

**Response `201`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "clientId": "client-001",
  "fundId": "1",
  "fundName": "FPV_BTG_PACTUAL_RECAUDADORA",
  "type": "SUBSCRIPTION",
  "amount": 75000,
  "createdAt": "2024-03-15T10:30:00"
}
```

**Error `422` — saldo insuficiente:**
```json
{
  "error": "Unprocessable Entity",
  "message": "No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA",
  "status": 422
}
```

**Error `422` — ya suscrito:**
```json
{
  "error": "Unprocessable Entity",
  "message": "El cliente ya está suscrito al fondo FPV_BTG_PACTUAL_RECAUDADORA",
  "status": 422
}
```

### DELETE `/api/v1/funds/{fundId}/subscribe?clientId={id}`

**Response `200`:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "clientId": "client-001",
  "fundId": "1",
  "fundName": "FPV_BTG_PACTUAL_RECAUDADORA",
  "type": "CANCELLATION",
  "amount": 75000,
  "createdAt": "2024-03-15T11:00:00"
}
```

### GET `/api/v1/clients/{clientId}/transactions`

**Response `200`:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "clientId": "client-001",
    "fundId": "1",
    "fundName": "FPV_BTG_PACTUAL_RECAUDADORA",
    "type": "SUBSCRIPTION",
    "amount": 75000,
    "createdAt": "2024-03-15T10:30:00"
  }
]
```

---

## Ejecución local

### Requisitos

- Docker + Docker Compose
- Java 21 (solo para desarrollo sin Docker)

### Con Docker Compose

```bash
# 1. Clonar el repositorio
git clone <repo-url> && cd funds

# 2. Levantar MongoDB y la API
docker-compose up -d

# 3. Verificar que levantó correctamente
curl http://localhost:8080/actuator/health

# 4. Ver los fondos disponibles
curl http://localhost:8080/api/v1/funds
```

### Solo MongoDB (desarrollo con IntelliJ)

```bash
# Levantar solo MongoDB
docker-compose up mongodb -d

# Correr la aplicación desde IntelliJ o terminal
./gradlew bootRun
```

### Cliente de prueba precargado

Al arrancar la aplicación el `DataInitializer` inserta automáticamente:

- **5 fondos** con los montos mínimos requeridos
- **Cliente de prueba** con id `client-001` y saldo COP $500.000

---

## Pruebas

```bash
# Ejecutar pruebas unitarias
./gradlew test
```

### Casos de prueba cubiertos

| Use Case | Escenario |
|----------|-----------|
| `SubscribeFundUseCase` | Suscripción exitosa |
| `SubscribeFundUseCase` | Saldo insuficiente |
| `SubscribeFundUseCase` | Cliente ya suscrito |
| `SubscribeFundUseCase` | Cliente no encontrado |
| `SubscribeFundUseCase` | Fondo no encontrado |
| `CancelFundUseCase` | Cancelación exitosa |
| `CancelFundUseCase` | Cliente no suscrito al fondo |
| `GetTransactionHistoryUseCase` | Historial con transacciones |
| `GetTransactionHistoryUseCase` | Historial vacío |
| `GetTransactionHistoryUseCase` | Cliente no encontrado |

---

## Despliegue en AWS

### Prerrequisitos

- AWS CLI configurado (`aws configure`)
- Key pair EC2 creado en la región destino
- VPC y subnet pública disponibles
- Imagen Docker publicada en ECR o Docker Hub

### 1. Build y push de la imagen

```bash
# Autenticarse en ECR
aws ecr get-login-password --region <region> | \
  docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com

# Crear repositorio
aws ecr create-repository --repository-name funds

# Build y push
docker build -t funds:latest .
docker tag funds:latest <account>.dkr.ecr.<region>.amazonaws.com/funds:latest
docker push <account>.dkr.ecr.<region>.amazonaws.com/funds:latest
```

### 2. Desplegar el stack

```bash
aws cloudformation deploy \
  --template-file cloudformation/funds-stack.yml \
  --stack-name funds-dev \
  --parameter-overrides \
    EnvironmentName=dev \
    InstanceType=t3.medium \
    KeyPairName=mi-keypair \
    VpcId=vpc-xxxxxxxx \
    SubnetId=subnet-xxxxxxxx \
    MongoRootUser=admin \
    MongoRootPassword=MiPassword123 \
    AppDockerImage=<account>.dkr.ecr.<region>.amazonaws.com/funds:latest \
  --capabilities CAPABILITY_NAMED_IAM
```

### 3. Obtener la URL de la API

```bash
aws cloudformation describe-stacks \
  --stack-name funds-dev \
  --query "Stacks[0].Outputs[?OutputKey=='ApiUrl'].OutputValue" \
  --output text
```

### 4. Eliminar el stack

```bash
aws cloudformation delete-stack --stack-name funds-dev
```

### Arquitectura AWS desplegada

```
Internet
    │
    ▼
[Elastic IP]
    │
    ▼
[EC2 t3.medium — Amazon Linux 2]
  ├── Docker: funds-api    → puerto 8080
  └── Docker: funds-mongodb → puerto 27017 (solo interno)
```

---

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `MONGODB_URI` | URI de conexión a MongoDB | `mongodb://localhost:27017/fundsdb` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `MONGO_ROOT_USER` | Usuario root MongoDB (Docker) | `admin` |
| `MONGO_ROOT_PASSWORD` | Contraseña root MongoDB (Docker) | — |

> Las credenciales nunca se hardcodean en el código. Se configuran siempre por variables de entorno.

---

## Buenas prácticas implementadas

- **Inmutabilidad del dominio** — entidades con `@Value` y `@With` de Lombok
- **Flujos 100% reactivos** — sin operaciones bloqueantes en ninguna capa
- **Validaciones reactivas** — sin `if` dentro de `flatMap`, usando `filter` + `switchIfEmpty`
- **UUID generado en dominio** — identificador único de transacción no delegado a MongoDB
- **Credenciales por variables de entorno** — nunca hardcodeadas
- **Usuario no-root en Docker** — el contenedor corre como usuario `funds`
- **EBS cifrado en EC2** — volumen con `Encrypted: true` en CloudFormation
- **IAM Role con mínimo privilegio** — solo permisos SSM y CloudWatch
- **Multi-stage Dockerfile** — imagen final solo con JRE 21, sin JDK
