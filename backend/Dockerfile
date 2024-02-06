FROM git.phinion.com/phinion/dependency_proxy/containers/maven:3.9-eclipse-temurin-17 AS build-backend

WORKDIR /src

# To resolve dependencies (minimizes the amounts of downloads needed when the dependencies don't change)
COPY pom.xml checkstyle.xml /src/
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -Dmaven.main.skip -DskipTests -Dcheckstyle.skip && rm -r target

COPY src ./src/

RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests

FROM tomcat:10-jdk17-temurin

COPY --from=build-backend /src/target/vng.war /var/lib/tomcat10/webapps/ROOT.war
COPY ./tomcat-config/logging.properties /usr/local/tomcat/conf/logging.properties
