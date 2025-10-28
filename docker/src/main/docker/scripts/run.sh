#!/bin/sh
set -eu

# Enable remote debugging if DEBUG=true
if [ "${DEBUG:-false}" = "true" ]; then
  # Java 9+ / 17: use JDWP agent (replaces -Xdebug/-Xrunjdwp)
  DEBUG_PARAM='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787'
else
  DEBUG_PARAM=''
fi

# Helper to mask sensitive values in logs
mask() {
  v="${1:-}"
  if [ -z "$v" ]; then
    printf ""
  elif [ ${#v} -le 4 ]; then
    printf "**"
  else
    printf "%s**" "${v%??}"
  fi
}

echo "$(date +"%T"): ======= Mighty Messenger Variables ========"
echo "$(date +"%T"): PROFILE=${PROFILE:-}"
echo "$(date +"%T"): DB_URL=${DB_URL:-}"
echo "$(date +"%T"): DB_USER=${DB_USER:-}"
echo "$(date +"%T"): DB_PASSWORD=$(mask "${DB_PASSWORD:-}")"
echo "$(date +"%T"): JAR_LOCATION=${JAR_LOCATION:-/app/server.jar}"
echo "$(date +"%T"): BROKER_URL=${BROKER_URL:-}"
echo "$(date +"%T"): BROKER_USERNAME=${BROKER_USERNAME:-}"
echo "$(date +"%T"): BROKER_PASSWORD=$(mask "${BROKER_PASSWORD:-}")"
echo "$(date +"%T"): ==========================================="

# Prefer JAVA_TOOL_OPTIONS for JVM flags (read automatically by the JVM)
# If you still use JAVA_OPTS, we keep honoring it here.
# shellcheck disable=SC2086
exec java $DEBUG_PARAM ${JAVA_OPTS:-} \
  -Dspring.datasource.url="${DB_URL:-}" \
  -Dspring.datasource.username="${DB_USER:-}" \
  -Dspring.datasource.password="${DB_PASSWORD:-}" \
  -Dspring.profiles.active="${PROFILE:-}" \
  -Ddebug=false \
  -Dmsg.jms.brokerUrl="${BROKER_URL:-}" \
  -Dmsg.jms.username="${BROKER_USERNAME:-}" \
  -Dmsg.jms.password="${BROKER_PASSWORD:-}" \
  -jar "${JAR_LOCATION:-/app/server.jar}"
