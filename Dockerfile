# ══════════════════════════════════════════════════════════════════
# DOCKERFILE - BACKEND (Spring Boot)
# Café con Huellas — API REST
# Construcción multi-stage para minimizar el tamaño de la imagen final
# ══════════════════════════════════════════════════════════════════

# ── Stage 1: BUILD ─────────────────────────────────────────────────
# Usamos la imagen oficial de Maven con JDK 17 (Eclipse Temurin)
# para compilar el proyecto. Esta imagen solo existe durante el build,
# no forma parte de la imagen final.
FROM maven:3.9-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar la caché de Docker:
# si las dependencias no cambian, Maven no las vuelve a descargar
COPY pom.xml .

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto y generamos el JAR, omitiendo los tests
# para acelerar el build en entornos CI/CD y Docker
RUN mvn clean package -DskipTests


# ── Stage 2: RUNTIME ───────────────────────────────────────────────
# Imagen final ligera: solo el JRE (Java Runtime Environment), sin Maven
# ni código fuente. Esto reduce el tamaño final considerablemente.
FROM eclipse-temurin:17-jre

# Directorio de trabajo en la imagen final
WORKDIR /app

# Copiamos únicamente el JAR generado en el stage anterior
# El wildcard *.jar permite que funcione independientemente del nombre exacto
COPY --from=build /app/target/*.jar app.jar

# Documentamos el puerto que expone la aplicación (8087)
# No abre el puerto en el host, solo sirve de documentación
EXPOSE 8087

# Comando de arranque: ejecuta el JAR con Java
ENTRYPOINT ["java", "-jar", "app.jar"]