# Étape de build
ARG TARGETARCH
FROM eclipse-temurin:17-jdk-jammy AS build

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y maven \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:resolve

COPY src ./src
COPY WebContent ./WebContent

RUN mvn -U clean package -DskipTests

# Étape finale avec Tomcat
ARG TARGETARCH
FROM tomcat:9.0-jdk11-temurin-jammy

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]