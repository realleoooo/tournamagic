FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first for better layer caching of dependencies
COPY pom.xml .

# Resolve dependencies with retries to tolerate transient Maven Central transfer issues
RUN set -eux; \
    for attempt in 1 2 3; do \
      rm -rf /root/.m2/repository/net/bytebuddy || true; \
      mvn -B -U -DskipTests -Dmaven.wagon.http.retryHandler.count=5 dependency:go-offline && break; \
      if [ "$attempt" -eq 3 ]; then exit 1; fi; \
      echo "Maven dependency resolution failed (attempt $attempt). Retrying..."; \
      sleep 3; \
    done

COPY src ./src

# Package with retries as well (covers transient artifact re-download failures)
RUN set -eux; \
    for attempt in 1 2 3; do \
      rm -rf /root/.m2/repository/net/bytebuddy || true; \
      mvn -B -U -DskipTests -Dmaven.wagon.http.retryHandler.count=5 package && break; \
      if [ "$attempt" -eq 3 ]; then exit 1; fi; \
      echo "Maven package failed (attempt $attempt). Retrying..."; \
      sleep 3; \
    done

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
