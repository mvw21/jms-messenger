# Ubuntu with Artemis Server image

## Use official Apache Artemis image
#FROM apache/activemq-artemis:latest
#
## Copy your broker configuration (adjust path if needed)
#COPY assets/broker.xml /var/lib/artemis/etc/broker.xml
#
## If you have custom scripts, copy them in
#COPY scripts/start-container.sh /opt/start-container.sh
#RUN chmod +x /opt/start-container.sh
#
## Set default command to your startup script (or keep the default one)
#CMD ["/opt/start-container.sh"]

FROM apache/activemq-artemis:latest

COPY assets/broker.xml /var/lib/artemis/etc/broker.xml

