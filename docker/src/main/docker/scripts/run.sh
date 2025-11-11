#!/bin/sh
set -eu

# --- debug flag (unchanged) ---
if [ "${DEBUG:-false}" = "true" ]; then
  DEBUG_PARAM='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787'
else
  DEBUG_PARAM=''
fi

mask() { v="${1:-}"; [ -z "$v" ] && printf "" || [ ${#v} -le 4 ] && printf "**" || printf "%s**" "${v%??}"; }

echo "$(date +"%T"): ======= Mighty Messenger Variables ========"
echo "$(date +"%T"): PROFILE=${PROFILE:-}"
echo "$(date +"%T"): DB_URL=${DB_URL:-}"
echo "$(date +"%T"): DB_USER=${DB_USER:-}"
echo "$(date +"%T"): DB_PASSWORD=$(mask "${DB_PASSWORD:-}")"
echo "$(date +"%T"): JAR_LOCATION=${JAR_LOCATION:-/app/server.jar}"
# print BOTH styles so we see what's coming in
echo "$(date +"%T"): MSG_JMS_BROKER_URL=${MSG_JMS_BROKER_URL:-}"
echo "$(date +"%T"): MSG_JMS_USERNAME=${MSG_JMS_USERNAME:-}"
echo "$(date +"%T"): MSG_JMS_PASSWORD=$(mask "${MSG_JMS_PASSWORD:-}")"
echo "$(date +"%T"): BROKER_URL=${BROKER_URL:-}"
echo "$(date +"%T"): BROKER_USERNAME=${BROKER_USERNAME:-}"
echo "$(date +"%T"): BROKER_PASSWORD=$(mask "${BROKER_PASSWORD:-}")"
echo "$(date +"%T"): ==========================================="

# ---- FALLBACKS (accept both env styles) ----
BROKER_URL_FINAL="${MSG_JMS_BROKER_URL:-${BROKER_URL:-}}"
BROKER_USER_FINAL="${MSG_JMS_USERNAME:-${BROKER_USERNAME:-}}"
BROKER_PASS_FINAL="${MSG_JMS_PASSWORD:-${BROKER_PASSWORD:-}}"

# Option A: fail fast if any is empty (best for tests)
[ -z "${BROKER_URL_FINAL}" ] && { echo "ERROR: msg.jms.brokerUrl empty"; exit 1; }
[ -z "${BROKER_USER_FINAL}" ] && { echo "ERROR: msg.jms.username empty"; exit 1; }
[ -z "${BROKER_PASS_FINAL}" ] && { echo "ERROR: msg.jms.password empty"; exit 1; }

# Build args without injecting empty strings
JAVA_ARGS=""
JAVA_ARGS="$JAVA_ARGS -Dspring.datasource.url=${DB_URL:-}"
JAVA_ARGS="$JAVA_ARGS -Dspring.datasource.username=${DB_USER:-}"
JAVA_ARGS="$JAVA_ARGS -Dspring.datasource.password=${DB_PASSWORD:-}"
JAVA_ARGS="$JAVA_ARGS -Dspring.profiles.active=${PROFILE:-}"
JAVA_ARGS="$JAVA_ARGS -Ddebug=false"
JAVA_ARGS="$JAVA_ARGS -Dmsg.jms.brokerUrl=${BROKER_URL_FINAL}"
JAVA_ARGS="$JAVA_ARGS -Dmsg.jms.username=${BROKER_USER_FINAL}"
JAVA_ARGS="$JAVA_ARGS -Dmsg.jms.password=${BROKER_PASS_FINAL}"

# shellcheck disable=SC2086
exec java $DEBUG_PARAM ${JAVA_OPTS:-} $JAVA_ARGS -jar "${JAR_LOCATION:-/app/server.jar}"
