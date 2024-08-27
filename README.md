| Service                    | Health-check url             |
|----------------------------|------------------------------|
| forum-service              | http://localhost:8085/health |
| notification-service       | http://localhost:8086/health |
| payment-service            | http://localhost:8087/health |
| saga-orchestration-service | http://localhost:8088/health |

## Opentelemetry

- We are using Java agent to reduce config work. Reference: [opentelemetry-javaagent](https://github.com/open-telemetry/opentelemetry-java-instrumentation)
- And Jaeger as center observation.

## TODO
Move run -> docker & k8s