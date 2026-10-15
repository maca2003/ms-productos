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
