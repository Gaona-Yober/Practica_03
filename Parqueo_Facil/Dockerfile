# Etapa de construcción con Java 17
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Copia el conector MySQL desde tu repositorio local
COPY src/main/webapp/WEB-INF/lib/mysql-connector-java-8.0.26.jar /app/lib/
RUN mvn clean package -DskipTests

# Etapa de despliegue con Open Liberty
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi

# Copiar configuración y aplicación
COPY src/main/liberty/config /config/
COPY --from=build /app/target/Parqueo_Facil.war /config/apps/
COPY --from=build /app/lib/mysql-connector-java-8.0.26.jar /opt/ol/wlp/usr/shared/resources/

# Configurar permisos
USER root
RUN chown -R 1001:0 /config /opt/ol/wlp/usr/shared/resources
USER 1001

# Puerto expuesto
EXPOSE 9080 9443