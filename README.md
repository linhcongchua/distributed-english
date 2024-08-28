| Service                    | Health-check url             |
|----------------------------|------------------------------|
| forum-service              | http://localhost:8085/health |
| notification-service       | http://localhost:8086/health |
| payment-service            | http://localhost:8087/health |
| saga-orchestration-service | http://localhost:8088/health |


## Architecture

> This project is demo project for what I have learned through those time as a copy/paster

 With an idea `making a forum for English learner or any languages`, that forum can help learner post a question in the internet with a small `reward`, and take advantage of million learner out there. Hope it can make a journey to English or any other languages much easier.

 
#### 1. Overview structure

![overview-project-structure](./z-docs/microservice_in_actions-[distributed-english]overview-project-structure.drawio.png)

There are many service that single single service have their own database. Services communicate through `Kafka` to produce command or event. With query request, it is suitable for using `http` request directly to service

- Gateway-sevice: 
- Saga-orchestration-service:
- Notification-service
- Payment-service:
- Forum-service:
- Media-service
- Fraud-detection-service:
- Account-service:

#### 2. Pattern

##### A. CQRS

> As our `forum-service` will have more read request than a write. So it can using CQRS for reduce the latency in read request.
> And more the query side can also listen to other event that related to this domain in other service and combine as a flat data. 

![sqrs-forum](./z-docs/microservice_in_actions-[distributed-english]forum-cqrs.drawio.png)

##### B. Outbox event router

> Consistent is important feature in microservice project. Outbox pattern is shining in here and have take a advantage of transaction in relational database (`postgres`) and Debezium as a Capture Data Change to push message to `Kafka`.


![outbox-example](./core-outbox/outbox-overview.png)

> To apply for almost every service that want produce event as a single transaction with logic.

##### C. Event Sourcing

> Event Sourcing is good choice for observing the change through timeline of a domain. And with this `distributed-english`, its purpose is take advantage of million languages learner out there. And reward take place as a interested feature that be consider as another way to make money for those one who have answer the question.
> It is about `Money` - `Payment-service`.

| aggregate_type | event_type           | data    | metadata | version | timestamp               |
|----------------|----------------------|---------|----------|---------|-------------------------|
| emoney         | ACCOUNT_INITIALIZED  | 0.0     |          | 1       | 2024‑08‑25 17:45:30.005 |
| emoney         | BALANCE_DEPOSITED    | 25000.0 |          | 2       | 2024‑08‑26 20:45:30.005 |
| emoney         | BALANCE_WITHDREW     | 10000.0 |          | 3       | 2024‑08‑27 08:26:28.005 |
| emoney         | EARNED_REWARD        | 80000.0 |          | 4       | 2024‑08‑28 17:45:30.005 |
| emoney         | OFFERED_REWARD       | 35000.0 |          | 5       | 2024‑08‑28 18:45:30.005 |

=> `current_emoney = 0.0 + 25000.0 - 10000.0 + 80000.0 - 35000.0 = 60000.0`


## Opentelemetry

- We are using Java agent to reduce config work. Reference: [opentelemetry-javaagent](https://github.com/open-telemetry/opentelemetry-java-instrumentation)
- And Jaeger as center observation.

## TODO
- [ ] Move run -> docker & k8s
- [ ] Config ELK
- [ ] CQRS query handler code base.