FROM eclipse-temurin:17-jre-jammy

# Where the app jar will live *inside the container*
ENV JAR_LOCATION=/data/messenger/server.jar
WORKDIR /data/messenger

# (Optional) curl for HEALTHCHECK
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Non-root user + writable dirs
RUN useradd -r -u 10001 -g root appuser \
    && mkdir -p /runtime/logs /scripts \
    && chown -R appuser:root /runtime /scripts /data \
    && chmod -R g+rwX /runtime /scripts /data

# Copy the built JAR from the server module and rename it to server.jar
# (uses wildcard on the *source* to match versioned file)
COPY server/target/server-*.jar ${JAR_LOCATION}

# Copy startup scripts
COPY docker/src/main/docker/scripts /scripts
RUN chmod +x /scripts/*.sh

USER appuser

HEALTHCHECK --interval=3s --retries=5 CMD curl -f http://localhost:8080/api/health || exit 1

EXPOSE 8080
ENTRYPOINT ["/bin/sh","/scripts/run.sh"]
