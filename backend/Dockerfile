
# ---------- Stage 1: build the Spring Boot jar ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Use Aliyun Maven mirror to avoid slow / blocked Maven Central access
COPY maven-settings.xml /root/.m2/settings.xml

# Copy project sources
COPY pom.xml ./
COPY src ./src

# Build the jar. BuildKit cache mount keeps the local Maven repo across builds.
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -s /root/.m2/settings.xml -DskipTests package

# ---------- Stage 2: minimal runtime image ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Drop privileges
RUN groupadd --system app && useradd --system --gid app --home /app app

# Copy the produced jar with a stable name
COPY --from=build /workspace/target/washhelper-backend-*.jar /app/app.jar

# Writable directories
RUN mkdir -p /app/uploads && chown -R app:app /app
USER app

EXPOSE 8099

ENV JAVA_OPTS="" \
    SERVER_PORT=8099

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]