CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS post
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT,
    detail TEXT,
    user_id UUID,
    is_deleted boolean DEFAULT false
);
CREATE INDEX IF NOT EXISTS post_user_id_is_deleted_idx ON post USING btree (user_id, is_deleted);

CREATE TABLE IF NOT EXISTS event_publish
(
    id       UUID                     DEFAULT uuid_generate_v4(),
    aggregate_id   VARCHAR(250),
    aggregate_type VARCHAR(250),
    type     VARCHAR(250),
    timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    payload jsonb
);