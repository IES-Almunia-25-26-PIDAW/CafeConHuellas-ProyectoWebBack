# 🐾 Café con Huellas — Backend API

> API REST para la gestión integral de un refugio de animales

---

## ¿Qué es Café con Huellas?

**Café con Huellas** es una plataforma digital para la gestión de un refugio de animales. La idea es centralizar todo lo que necesita un refugio en una sola aplicación: el catálogo de animales disponibles para adopción, la gestión de usuarios, el proceso completo de adopción, el registro de donaciones, la organización de eventos y el historial médico de cada mascota.

El sistema distingue dos tipos de usuarios:
- **ADMIN**: el personal del refugio, que gestiona mascotas, procesa adopciones, crea eventos y consulta estadísticas.
- **USER**: personas interesadas en adoptar o colaborar, que pueden explorar el catálogo, donar y rellenar formularios de adopción.

---

## ✨ Funcionalidades principales

### 🐶 Gestión de mascotas
- Catálogo completo con ficha detallada de cada animal (nombre, raza, edad, peso, descripción, estado de esterilización...)
- Galería de imágenes por mascota
- Filtros por categoría (perro/gato) y por estado de esterilización
- Historial de vacunas por mascota

### 📋 Proceso de adopción
- El admin envía un formulario de adopción personalizado al usuario interesado **por correo electrónico**, con un enlace único y seguro (token con expiración de 48h)
- El usuario rellena el formulario desde el enlace sin necesidad de estar registrado
- Al enviar el formulario, se notifica automáticamente al admin y se confirma la recepción al usuario por email
- El admin puede revisar, aprobar o rechazar las solicitudes y registrar los detalles post-adopción

### 💰 Donaciones
- Los usuarios pueden registrar donaciones económicas
- El admin puede consultar el historial completo, filtrar por categoría y ver estadísticas de totales

### 📅 Eventos
- Creación y gestión de eventos del refugio (jornadas de adopción, charlas, mercadillos...)
- Filtro de próximos eventos y por estado

### 👥 Usuarios
- Registro y login con JWT
- Gestión de perfiles por parte del admin
- Los usuarios pueden guardar mascotas como favoritas

---

## 🗂️ Modelo de datos

El proyecto cuenta con las siguientes entidades y relaciones:

### Entidades principales

| Entidad | Descripción |
|---|---|
| `User` | Usuarios del sistema (adoptantes y administradores). Tiene rol ADMIN o USER. |
| `Pet` | Mascotas del refugio con toda su información (raza, edad, peso, categoría...). |
| `Vaccine` | Catálogo de vacunas disponibles (datos maestros). |
| `Event` | Eventos organizados por el refugio (jornadas, mercadillos, charlas...). |
| `Donation` | Donaciones registradas en el sistema (pueden ser anónimas). |

### Entidades de relación

| Entidad | Descripción |
|---|---|
| `PetImage` | Fotos adicionales de una mascota. Relación **N:1** con `Pet`. |
| `PetVaccine` | Registro de vacunas aplicadas a una mascota con fecha. Relación **N:1** con `Pet` y `Vaccine`. |
| `UserPetRelationship` | Vínculo formal entre un usuario y una mascota (adopción, acogida, paseo, voluntariado). Relación **N:1** con `User` y `Pet`. |
| `UserPetFavorite` | Lista de mascotas marcadas como favoritas por un usuario. Relación **N:1** con `User` y `Pet`. |

### Entidades del flujo de adopción

| Entidad | Descripción |
|---|---|
| `AdoptionFormToken` | Token único y temporal (48h) que el admin envía por email al usuario para acceder al formulario. Relación **N:1** con `User` y `Pet`. |
| `AdoptionRequest` | Formulario rellenado por el usuario con sus datos de vivienda, convivencia y motivación. Relación **1:1** con `AdoptionFormToken`. |
| `AdoptionDetail` | Detalles técnicos y de seguimiento registrados por el admin tras formalizar la adopción. Relación **1:1** con `UserPetRelationship`. |

### Diagrama de relaciones

```
User ──────────────────────────────────────────────────┐
 │                                                      │
 ├──< Donation (N:1)                                    │
 ├──< UserPetFavorite (N:1) >── Pet                     │
 ├──< UserPetRelationship (N:1) >── Pet                 │
 │         └──── AdoptionDetail (1:1)                   │
 └──< AdoptionFormToken (N:1) >── Pet                   │
           └──── AdoptionRequest (1:1)                  │
                                                        │
Pet ────────────────────────────────────────────────────┘
 ├──< PetImage (N:1)
 └──< PetVaccine (N:1) >── Vaccine

Event  (entidad independiente)
```

---

## 🛠️ Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Security + JWT** (autenticación stateless)
- **Spring Data JPA + Hibernate**
- **MySQL** (base de datos principal)
- **H2** (base de datos en memoria para tests)
- **MapStruct** (mapeo entre entidades y DTOs)
- **Lombok**
- **Swagger / OpenAPI 3** (documentación interactiva)
- **Spring Mail** (envío de correos automáticos)
- **Maven**

---

## ✅ Requisitos previos

Antes de levantar el proyecto, asegúrate de tener instalado:

- [Java 17](https://adoptium.net/) o superior
- [Maven 3.9+](https://maven.apache.org/download.cgi) (o usa el wrapper incluido `./mvnw`)
- [MySQL 8+](https://dev.mysql.com/downloads/)
- Una cuenta de Gmail con [contraseña de aplicación](https://myaccount.google.com/apppasswords) habilitada (para el envío de correos)

---

## 🚀 Instalación y puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/IES-Almunia-25-26-PIDAW/CafeConHuellas-ProyectoWebBack.git
cd CafeConHuellas-ProyectoWebBack
```



---

### 2. Crear la base de datos en MySQL

Accede a tu cliente MySQL y ejecuta:

```sql
CREATE DATABASE cafe_con_huellas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
> ℹ️ Solo es necesario crear la base de datos. Las tablas se generan
> automáticamente al arrancar la aplicación gracias a Hibernate (`ddl-auto=update`).

---

### 3. Configurar las variables de entorno

El proyecto requiere las siguientes variables. Puedes definirlas en tu IDE (IntelliJ → Run/Edit Configurations → Environment Variables) o crear el archivo que se indica abajo.

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_URL` | URL JDBC de tu base de datos | `jdbc:mysql://localhost:3306/cafe_con_huellas` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `tu_password` |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT (mín. 32 chars) | `mi_clave_super_secreta_2024_jwt` |
| `JWT_EXPIRATION` | Duración del token en milisegundos | `86400000` (= 24h) |
| `MAIL_USERNAME` | Tu correo Gmail | `tucorreo@gmail.com` |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail | `xxxx xxxx xxxx xxxx` |
| `ADMIN_EMAIL` | Email del administrador del refugio | `admin@cafeconhuellas.com` |
| `FRONTEND_URL` | URL del frontend (para los links en los correos) | `http://localhost:4200` |

#### Opción rápida: archivo `application-local.properties`

Crea el archivo `src/main/resources/application-local.properties` (está en `.gitignore`, nunca se sube al repositorio):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cafe_con_huellas
spring.datasource.username=root
spring.datasource.password=tu_password

app.jwt.secret=mi_clave_super_secreta_para_jwt_2024
app.jwt.expiration=86400000

spring.mail.username=tucorreo@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx

app.admin.email=admin@cafeconhuellas.com
app.frontend.url=http://localhost:4200
```

---

### 4. Ejecutar la aplicación

#### Con Maven Wrapper (recomendado, no necesita Maven instalado):

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

#### Con perfil local (si creaste `application-local.properties`):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

La API arrancará en: **http://localhost:8080**

---

## 📚 Documentación de la API (Swagger)

Una vez levantada la aplicación, accede a la documentación interactiva:

```
http://localhost:8080/swagger-ui/index.html
```

Desde ahí puedes explorar y probar todos los endpoints. Para los protegidos, primero haz login y pega el token con el botón **Authorize 🔒**.

---

## 🔐 Autenticación

La API usa **JWT Bearer Token**. El flujo es:

1. **Registro** → `POST /api/auth/register`
2. **Login** → `POST /api/auth/login` → devuelve un `token`
3. Incluye el token en las siguientes peticiones: `Authorization: Bearer <token>`

### Endpoints públicos (no requieren token)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/auth/register` | Registro de nuevo usuario |
| `POST` | `/api/auth/login` | Login y obtención de token |
| `GET` | `/api/adoption-form/validate/{token}` | Validar token de formulario de adopción |
| `POST` | `/api/adoption-form/submit/{token}` | Enviar formulario de adopción |

---

## 🧪 Ejecutar los tests

```bash
./mvnw test
```

Los tests usan una base de datos **H2 en memoria**, por lo que no necesitan MySQL ni variables de entorno. Se ejecutan de forma completamente aislada.

---

## 📁 Estructura del proyecto

```
src/
├── main/
│   ├── java/com/example/cafe_con_huellas/
│   │   ├── config/          # SecurityConfig, SwaggerConfig
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Excepciones personalizadas y handler global
│   │   ├── mapper/          # Mappers MapStruct (entidad ↔ DTO)
│   │   ├── model/entity/    # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── security/        # JwtService, JwtAuthFilter
│   │   └── service/         # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/
    ├── java/                # Tests unitarios e integración
    └── resources/
        └── application.properties  # Configuración H2 para tests
```

---

## 👩‍💻 Autoras

**Yolanda Cabrera Naranjo, Ana Cruces López y Andrea González Llamas** — Proyecto de fin de curso · Backend con Spring Boot
