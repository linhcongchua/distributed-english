FROM openjdk:21-jdk
LABEL authors="linhdv"

ADD build/libs/gateway-service-0.0.1-SNAPSHOT.jar /gateway-service.jar
ADD build/agent/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

ENTRYPOINT java -javaagent:opentelemetry-javaagent.jar \
                -Dotel.traces.exporter=logging \
                -Dotel.metrics.exporter=logging \
                -Dotel.logs.exporter=logging \
                -jar /gateway-service.jar