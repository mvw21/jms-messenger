# ---- build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . /workspace
RUN mvn -q -pl jms-client -am clean package -DskipTests

# ---- run ----
FROM eclipse-temurin:17-jre
ENV APP_HOME=/app
WORKDIR $APP_HOME

# copy shaded jar
COPY --from=build /workspace/jms-client/target/*-shaded.jar /app/app.jar

# defaults (override via compose)
ENV BROKER_URL=tcp://msg-broker:61616 \
    BROKER_USERNAME=admin \
    BROKER_PASSWORD=admin \
    QUEUE=queue.test \
    PAYLOAD="hello from jms-client" \
    DEBUG=false

EXPOSE 5005
SHELL ["/bin/sh","-lc"]
ENTRYPOINT sh -lc '\
  if [ "$DEBUG" = "true" ]; then \
    export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"; \
    echo "[jms-client] Debug ON (5005, suspend=y)"; \
  fi; \
  url="${BROKER_URL#*://}"; \
  host="${url%%:*}"; \
  port="${BROKER_URL##*:}"; \
  echo "Waiting for broker ${host}:${port}..."; \
  for i in $(seq 1 20); do \
    (echo > /dev/tcp/${host}/${port}) >/dev/null 2>&1 && break || sleep 2; \
  done; \
  echo "Starting jms-client"; \
  exec java $JAVA_TOOL_OPTIONS -jar /app/app.jar'
