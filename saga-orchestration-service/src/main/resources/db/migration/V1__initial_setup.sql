CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS snapshots CASCADE;


CREATE TABLE IF NOT EXISTS event_publish
(
    id              UUID            DEFAULT uuid_generate_v4(),
    aggregate_id    VARCHAR(250),
    aggregate_type  VARCHAR(250),
    type            VARCHAR(250),
    timestamp       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    payload jsonb
);