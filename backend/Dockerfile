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

ARG USER_ID

RUN chown ${USER_ID} /usr/local/tomcat/webapps

USER ${USER_ID}

COPY --from=build-backend /src/target/diwi.war /usr/local/tomcat/webapps/ROOT.war
COPY ./tomcat-config/logging.properties /usr/local/tomcat/conf/logging.properties
