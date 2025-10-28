FROM eclipse-temurin:17-jre-jammy

ENV JAR_LOCATION=/data/messenger/server.jar
WORKDIR /data/messenger

# Install curl for the HEALTHCHECK
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Non-root user + writable runtime dir
RUN useradd -r -u 10001 -g root appuser \
    && mkdir -p /runtime/logs /scripts \
    && chown -R appuser:root /runtime /scripts /data \
    && chmod -R g+rwX /runtime /scripts /data

# App files
COPY server.jar ${JAR_LOCATION}
COPY scripts /scripts
RUN chmod -R a+x /scripts/*.sh

USER appuser

HEALTHCHECK --interval=3s --retries=5 CMD curl -f http://localhost:8080/api/health || exit 1

EXPOSE 8080
ENTRYPOINT ["/bin/sh","/scripts/run.sh"]
