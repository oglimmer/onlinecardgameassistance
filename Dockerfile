FROM maven:3.8-eclipse-temurin-8 AS build

# Build couchdb4j dependency
WORKDIR /build/couchdb4j
RUN git clone --single-branch --depth 1 https://github.com/mbreese/couchdb4j.git . && \
    sed -i '/import java.io.UnsupportedEncodingException/d' src/java/com/fourspaces/couchdb/Session.java && \
    sed -i 's/catch (UnsupportedEncodingException/catch (Exception/g' src/java/com/fourspaces/couchdb/Session.java && \
    mvn -DskipTests=true -q install

# Build bcg
WORKDIR /build/bcg
COPY pom.xml .
RUN mvn dependency:go-offline -q || true
COPY src src
RUN mvn -q package

# Runtime
FROM tomcat:9-jdk8-temurin

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/bcg/target/bcg.war /usr/local/tomcat/webapps/bcg.war

EXPOSE 8080
