# Étape 1 : Build du projet avec Maven
FROM eclipse-temurin:17-jdk AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:resolve

COPY src ./src
COPY WebContent ./WebContent

RUN mvn clean package -DskipTests

# Étape 2 : Déployer dans Tomcat
FROM tomcat:9-jdk17

WORKDIR /usr/local/tomcat/webapps/

# Copier le .war généré et le renommer ROOT.war pour qu'il soit accessible à /
COPY --from=build /app/target/*.war ./ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]