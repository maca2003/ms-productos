# ms-productos

Microservicio de **productos, categorías y precios**. Forma parte de la solución de microservicios (MS-AUTH, MS-PRODUCTOS, MS-VENTAS, MS-INVENTARIO y API Gateway).

## Tecnologías

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- MySQL 8
- Maven
- JUnit 5 + Mockito

## Estructura

```
src/main/java/cl/duoc/msproductos
├── controller   -> Endpoints REST
├── service      -> Lógica de negocio
├── repository   -> Acceso a datos (Spring Data JPA)
├── model        -> Entidades JPA (Categoria 1 ── * Producto)
├── dto          -> Objetos de entrada y salida
└── exception    -> Manejo global de errores
```

## Requisitos

- Java 21
- Maven 3.9+
- MySQL 8 corriendo en `localhost:3306`

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/maca2003/ms-productos.git
   cd ms-productos
   ```
2. Revisar el usuario y contraseña de MySQL en `src/main/resources/application.properties` (por defecto `root` / `root`). La base de datos `productos_db` se crea automáticamente.
3. Compilar, ejecutar pruebas y empaquetar:
   ```bash
   mvn clean install
   ```
4. Ejecutar:
   ```bash
   java -jar target/ms-productos-1.0.0.jar
   ```
   o bien `mvn spring-boot:run`.

El servicio queda disponible en `http://localhost:8082`.

## Docker

El `Dockerfile` usa dos etapas:

1. **Compilación** (`maven:3.9.9-eclipse-temurin-21`): descarga las dependencias (en una capa separada para aprovechar la caché) y genera el `.jar`.
2. **Ejecución** (`eclipse-temurin:21-jre-alpine`): imagen liviana solo con el JRE, usuario sin privilegios (uid 1001), `HEALTHCHECK` y la JVM limitada al 75% de la memoria del contenedor.

```bash
docker build -t ms-productos:1.0.0 .
docker run -p 8082:8082 -e DB_HOST=host.docker.internal -e DB_PASSWORD=root --name ms-productos ms-productos:1.0.0
```

`DB_HOST=host.docker.internal` permite al contenedor usar el MySQL instalado en tu computador. Para levantar el sistema completo (todos los servicios + MySQL) usa el `docker-compose.yml` del repositorio [infraestructura-swarm](https://github.com/maca2003/infraestructura-swarm).

## CI/CD (GitHub Actions)

Archivo `.github/workflows/ci-cd.yml`. Se ejecuta en cada push o pull request a `main` y `develop`:

| Etapa | Ramas | Qué hace |
|---|---|---|
| Build y test | todas | `mvn clean verify` (compila, ejecuta pruebas y genera el `.jar`) |
| Provisionar Amazon ECR | `main` | Crea el repositorio de imágenes `ms-productos` en ECR si no existe |
| Construir y publicar imagen | `main` | `docker build` y `docker push` a ECR con etiquetas `<commit>` y `latest` |
| Desplegar en Docker Swarm | `main` | Por SSH al manager: `docker service update --image ... sistema_ms-productos` y verifica que `/api/productos` responda 200 a través del API Gateway |

Secrets requeridos (Settings → Secrets and variables → Actions):

| Secret | Valor |
|---|---|
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` | Learner Lab → AWS Details → AWS CLI (cambian en cada sesión) |
| `EC2_SSH_KEY` | Contenido de `labsuser.pem` |
| `EC2_HOST` | IP pública del manager (salida `IpPublicaManager` del stack `cluster-swarm`) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/categorias` | Listar categorías |
| GET | `/api/categorias/{id}` | Obtener categoría |
| POST | `/api/categorias` | Crear categoría |
| PUT | `/api/categorias/{id}` | Actualizar categoría |
| DELETE | `/api/categorias/{id}` | Eliminar categoría (solo si no tiene productos) |
| GET | `/api/productos?categoriaId=` | Listar productos (filtro opcional por categoría) |
| GET | `/api/productos/{id}` | Obtener producto |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### Ejemplos de cuerpo

Crear categoría:
```json
{ "nombre": "Lácteos", "descripcion": "Leche, quesos y yogures" }
```

Crear producto:
```json
{ "nombre": "Leche entera 1L", "descripcion": "Caja", "precio": 1200, "categoriaId": 1 }
```

## Respuestas de error

| Código | Caso |
|---|---|
| 400 | Datos inválidos (por ejemplo, precio negativo o nombre vacío) |
| 404 | Categoría o producto no encontrado |
| 409 | Categoría duplicada o con productos asociados |
