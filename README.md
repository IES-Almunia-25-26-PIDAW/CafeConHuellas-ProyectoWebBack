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
- Filtros por categoría (perro/gato), estado de esterilización y estado de adopción (disponible, en proceso, adoptado)
- Historial de vacunas por mascota

### 📋 Proceso de adopción
- El admin envía un formulario de adopción personalizado al usuario interesado **por correo electrónico**, con un enlace único y seguro (token con expiración de 48h)
- El usuario rellena el formulario desde el enlace sin necesidad de estar registrado
- Al enviar el formulario, se notifica automáticamente al admin y se confirma la recepción al usuario por email
- El admin puede revisar, aprobar o rechazar las solicitudes y registrar los detalles post-adopción
- Al crear la relación de tipo ADOPCION, el sistema vincula automáticamente la solicitud aprobada con dicha relación, permitiendo al administrador consultar el formulario original desde el historial de relaciones
- Cuando el admin acepta o rechaza cualquier tipo de vínculo usuario-mascota
  (adopción, acogida, paseo...), el usuario recibe automáticamente un email
  de notificación. Los vínculos con fecha de fin vencida se desactivan
  automáticamente cada noche.
- El usuario registrado puede solicitar directamente desde la web el envío
  del formulario de adopción para una mascota concreta. Su identidad se
  extrae automáticamente del JWT, garantizando que solo puede solicitarlo
  para sí mismo.
- El usuario registrado puede solicitar vínculos de tipo ACOGIDA, PASEO o
  VOLUNTARIADO directamente desde la web. La relación se crea con estado
  inactivo, pendiente de aprobación por el administrador. El tipo ADOPCION
  está reservado al flujo del formulario.
- Al aprobar una solicitud de adopción, el sistema actualiza automáticamente
  el estado de la mascota a ADOPTADO.

### 💰 Donaciones
- Los usuarios pueden registrar donaciones económicas
- El admin puede consultar el historial completo, filtrar por categoría y ver estadísticas de totales

### 📅 Eventos
- Creación y gestión de eventos del refugio (jornadas de adopción, charlas, mercadillos...)
- Filtro de próximos eventos y por estado

### 👥 Usuarios
- Registro y login con JWT
- Gestión de perfiles por parte del admin

---

## 🛠️ Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Security + JWT** (autenticación stateless)
- **Spring Data JPA + Hibernate**
- **Liquibase** (migraciones de base de datos)
- **MySQL 8** (base de datos principal)
- **H2** (base de datos en memoria para tests)
- **MapStruct** (mapeo entre entidades y DTOs)
- **Lombok**
- **Swagger / OpenAPI 3** (documentación interactiva)
- **Spring Mail** (envío de correos automáticos)
- **Docker + Docker Compose**
- **Maven**

---

## ✅ Requisitos previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución
- No necesitas cuenta de Gmail para desarrollo. Los emails se capturan localmente con Mailpit.

> No necesitas tener Java, Maven ni MySQL instalados localmente. Docker se encarga de todo: descarga las imágenes necesarias, compila el proyecto y levanta tanto la base de datos como el servidor de forma automática.

---

## 🚀 Instalación y puesta en marcha con Docker

### 1. Clonar el repositorio

```bash
git clone https://github.com/IES-Almunia-25-26-PIDAW/CafeConHuellas-ProyectoWebBack.git
cd CafeConHuellas-ProyectoWebBack
```

### 2. Crear el archivo de variables de entorno

El proyecto necesita credenciales que **no se guardan en el repositorio** por seguridad. Hay que crearlas manualmente.

Crea un archivo llamado `.env` en la raíz del proyecto (en la misma carpeta donde está el `docker-compose.yml`). Tienes una plantilla en `.env.example`:

```
DB_USERNAME=cafe_user
DB_PASSWORD=tu_password_segura
MAIL_HOST=mailpit
MAIL_PORT=1025
MAIL_USERNAME=noreply@cafehuellas.local
MAIL_PASSWORD=
JWT_SECRET=tu_clave_secreta_jwt_minimo_32_caracteres
FRONTEND_URL=http://localhost:4200
BACKEND_URL=http://localhost:8087

```

| Variable | Descripción |
|---|---|
| `DB_USERNAME` | Usuario de la base de datos (cualquier nombre, **no usar `root`**) |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `MAIL_HOST` | Servidor SMTP. En local usar `mailpit` |
| `MAIL_PORT` | Puerto SMTP. En local usar `1025` |
| `MAIL_USERNAME` | Dirección remitente de los emails |
| `MAIL_PASSWORD` | Contraseña SMTP. En local dejar vacío |
| `JWT_SECRET` | Clave secreta para firmar los tokens JWT (mínimo 32 caracteres) |
| `FRONTEND_URL` | URL del frontend para construir los enlaces en los correos |
| `BACKEND_URL` | URL base del backend para construir las URLs públicas de los archivos subidos |

> ⚠️ El archivo `.env` está en `.gitignore` y **nunca se sube al repositorio** para proteger las credenciales.

### 3. Levantar el proyecto

**Primera vez** (descarga imágenes y compila el proyecto, tarda unos minutos):

```bash
docker compose up --build
```

**Siguientes veces** (arranca directamente sin recompilar, mucho más rápido):

```bash
docker compose up
```

> Usa `--build` de nuevo solo si has hecho cambios en el código Java o en el `pom.xml`.

Docker levantará tres contenedores:
- `cafe_con_huellas_db` → base de datos MySQL en el puerto 3307
- `cafe_con_huellas_backend` → servidor Spring Boot en el puerto 8087
- `cafe_con_huellas_mail` → Mailpit (capturador de emails) en el puerto 8025

Liquibase creará automáticamente todas las tablas de la base de datos al arrancar.

Cuando veas esta línea en los logs, el servidor está listo:

```
Started CafeConHuellasApplication in X seconds
```

La API estará disponible en: **http://localhost:8087**

### 4. Parar el proyecto

Para parar los contenedores conservando los datos de la base de datos:

```bash
docker compose down
```

Para parar y **borrar también los datos** (útil para empezar desde cero):

```bash
docker compose down -v
```

### 5. Ver los logs

Si algo falla al arrancar, puedes ver los logs detallados con:

```bash
docker compose logs backend
```

### ⚠️ Posibles problemas al arrancar

**El puerto 3307 ya está en uso**

Ocurre si tienes MySQL instalado localmente y arrancado. Solución: para el servicio MySQL local antes de levantar Docker.

```bash
# Windows (PowerShell como administrador)
Stop-Service -Name "MySQL*" -Force
```

**El puerto 8087 ya está en uso**

Otro proceso está usando ese puerto. Ciérralo o cambia el puerto en `docker-compose.yml`.

---

### 🔒 Nota sobre la configuración de Docker en producción

El archivo `docker-compose.yml` no expone el puerto de MySQL al exterior por seguridad. En su lugar, el archivo `docker-compose.override.yml` se encarga de exponer el puerto `3307` automáticamente en desarrollo para poder conectarse desde herramientas externas (IntelliJ, MySQL Workbench, etc.).

- **En desarrollo**: no hay que hacer nada especial. `docker compose up` aplica ambos archivos automáticamente.
- **En producción**: usar `docker compose -f docker-compose.yml up` para que MySQL solo sea accesible desde la red interna de Docker.

---

### 6. Ver los emails enviados (Mailpit)
En desarrollo, ningún email se envía realmente. Todos quedan capturados en **Mailpit**, una bandeja de entrada local.
Para verlos, abre en el navegador:
```
http://localhost:8025
```
Cada vez que la aplicación envíe un email (formulario de adopción, confirmación...) aparecerá aquí en lugar de llegar a ninguna bandeja real.

---

## 🐾 Datos de prueba

Al arrancar el proyecto por primera vez con Docker, Liquibase insertará
automáticamente datos de prueba en la base de datos.

### Credenciales de acceso

| Rol   | Email                    | Contraseña  |
|-------|--------------------------|-------------|
| ADMIN | admin@cafehuellas.com    | Admin1234!  |
| USER  | maria@example.com        | User1234!   |

### Datos incluidos
- 8 mascotas (5 perros y 3 gatos) con distintos estados de adopción
- 6 eventos (4 programados, 2 finalizados)

### Cómo obtener el token para probar la API

Los endpoints protegidos requieren un token JWT. Para obtenerlo con los usuarios de prueba:

1. Abre Swagger en `http://localhost:8087/swagger-ui/index.html`
2. Localiza el endpoint `POST /api/auth/login` y haz clic en **Try it out**
3. Introduce las credenciales en el body:
```json
{
  "email": "admin@cafehuellas.com",
  "password": "Admin1234!"
}
```
4. Pulsa **Execute** y copia el valor del campo `token` de la respuesta
5. Haz clic en el botón **Authorize 🔒** arriba a la derecha en Swagger
6. Pega el token directamente en el campo y pulsa **Authorize**

A partir de ese momento todos los endpoints protegidos funcionarán con ese usuario durante la sesión de Swagger.

> Repite el proceso con `maria@example.com` / `User1234!` para probar endpoints con rol USER.
---

## 🗃️ Gestión de la base de datos con Liquibase

El proyecto usa **Liquibase** para gestionar la estructura de la base de datos. En lugar de crear las tablas a mano, Liquibase las crea automáticamente al arrancar la aplicación ejecutando los changesets definidos en `src/main/resources/db/changelog/`.

### ¿Cuándo necesito hacer algo?

**Normalmente nada.** Al hacer `docker compose up` por primera vez, Liquibase crea todas las tablas solo.

**Solo si añades o modificas algo en las entidades** (nuevo campo, nueva tabla, etc.) sigue estos pasos:

1. Asegúrate de tener la base de datos local levantada con Docker:
```bash
docker compose up
```

2. Genera el changeset con los cambios detectados:
```bash
./mvnw liquibase:diff
```

3. Revisa el changeset generado en `src/main/resources/db/changelog/` y asegúrate de que es correcto
4. Añádelo al archivo `db.changelog-master.xml`
5. Recrea la base de datos para que Liquibase aplique los cambios:
```bash
docker compose down -v
docker compose up --build
```

> ⚠️ El flag `-v` borra todos los datos existentes. Úsalo solo en desarrollo, nunca en producción.
---

## 📚 Documentación de la API (Swagger)

Con el proyecto en marcha, accede a la documentación interactiva:

```
http://localhost:8087/swagger-ui/index.html
```

Desde ahí puedes explorar y probar todos los endpoints. Para los endpoints protegidos:

1. Haz login en `POST /api/auth/login`
2. Copia el token que devuelve
3. Pulsa el botón **Authorize 🔒** arriba a la derecha
4. Pega el token directamente en el campo y pulsa Authorize

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

### Endpoints para usuarios autenticados (requieren token)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/adoption-form/send` | Enviar formulario (USER: solo para sí mismo; ADMIN: para cualquier usuario) |
| `POST` | `/api/relationships/me` | Solicitar vínculo con una mascota (solo ACOGIDA, PASEO o VOLUNTARIADO) |

---

## 🗂️ Modelo de datos

### Entidades principales

| Entidad | Descripción |
|---|---|
| `User` | Usuarios del sistema (adoptantes y administradores). Tiene rol ADMIN o USER. |
| `Pet` | Mascotas del refugio con toda su información (raza, edad, peso, categoría...). |
| `Vaccine` | Catálogo de vacunas disponibles (datos maestros). |
| `Event` | Eventos organizados por el refugio (jornadas, mercadillos, charlas...). |
| `Donation` | Donaciones registradas en el sistema. |

### Entidades de relación

| Entidad | Descripción |
|---|---|
| `PetImage` | Fotos adicionales de una mascota. Relación **N:1** con `Pet`. |
| `PetVaccine` | Registro de vacunas aplicadas a una mascota con fecha. |
| `UserPetRelationship` | Vínculo formal entre un usuario y una mascota (adopción, acogida...). |
| `AdoptionFormToken` | Token único y temporal (48h) para acceder al formulario de adopción. |
| `AdoptionRequest` | Formulario rellenado por el usuario con sus datos. Se vincula con la `UserPetRelationship` generada al aprobar la solicitud. |
| `AdoptionDetail` | Detalles registrados por el admin tras formalizar la adopción. |

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
│   │   ├── scheduler/       # Tareas programadas automáticas
│   │   ├── security/        # JwtService, JwtAuthFilter
│   │   └── service/         # Lógica de negocio
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       └── db/changelog/    # Migraciones Liquibase
└── test/
    └── java/                # Tests unitarios e integración
```

---

## 🧪 Ejecutar los tests

```bash
./mvnw test
```

Los tests usan una base de datos **H2 en memoria**, por lo que no necesitan Docker ni variables de entorno. Se ejecutan de forma completamente aislada.

---

## 📚 Documentación Javadoc

La documentación técnica está generada con **Javadoc** y cubre todas las capas de la aplicación.

Para consultarla localmente abre el archivo `docs/javadoc/index.html` en tu navegador.

---

## 👩‍💻 Autoras

**Yolanda Cabrera Naranjo, Ana Cruces López y Andrea González Llamas** — Proyecto de fin de curso · Backend con Spring Boot