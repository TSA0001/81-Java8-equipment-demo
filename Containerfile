# syntax=docker/dockerfile:1.4
# Podman では HEALTHCHECK を有効にするため docker 形式でビルドすること:
#   BUILDAH_FORMAT=docker podman-compose up --build -d

# ---- build stage ----
FROM docker.io/library/eclipse-temurin:8-jdk AS build

ARG MAVEN_VERSION=3.9.9
ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
       | tar -xz -C /opt \
    && mv "/opt/apache-maven-${MAVEN_VERSION}" "${MAVEN_HOME}" \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests=false clean package

# ---- runtime stage ----
FROM docker.io/library/eclipse-temurin:8-jre

ENV CATALINA_HOME=/usr/local/tomcat \
    PATH=/usr/local/tomcat/bin:$PATH \
    TZ=Asia/Tokyo \
    JAVA_OPTS="-Duser.timezone=Asia/Tokyo" \
    DB_PATH=/data/h2/equipment

ARG TOMCAT_VERSION=9.0.98

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
       | tar -xz -C /usr/local \
    && mv "/usr/local/apache-tomcat-${TOMCAT_VERSION}" "${CATALINA_HOME}" \
    && rm -rf "${CATALINA_HOME}/webapps/*" \
    && mkdir -p /data/h2 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/target/equipment-management.war \
     "${CATALINA_HOME}/webapps/equipment-management.war"

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=5 \
  CMD curl -fsS "http://127.0.0.1:8080/equipment-management/health" || exit 1

CMD ["catalina.sh", "run"]
