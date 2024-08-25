Config connector debezium:

```shell
curl --location 'http://localhost:8083/connectors' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
   "name": "forum-post-connector",
   "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "xxx.xxx.xxx.xxx",
        "database.port": "5445",
        "database.user": "postgres",
        "database.password": "admin",
        "database.dbname": "forum-service",
        "table.include.list": "public.event_publish",
        "topic.prefix": "holy",
        "transforms" : "outbox",
        "value.converter.schemas.enable": "false",
        "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        "transforms.outbox.table.expand.json.payload": "true",
        "transforms.outbox.type" : "io.debezium.transforms.outbox.EventRouter",
        "transforms.outbox.table.field.event.key": "aggregate_id",
        "transforms.outbox.route.by.field": "aggregate_type",
        "transforms.outbox.route.topic.replacement": "${routedByValue}",
        "transforms.outbox.table.fields.additional.placement": "type:header:SAGA_HEADER"
    }
}'
```