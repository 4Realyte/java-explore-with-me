CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254),
    name  VARCHAR(250),
    CONSTRAINT UQ_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50),
    CONSTRAINT UQ_NAME UNIQUE (name)
);
CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000),
    category_id        BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    confirmed_requests INTEGER,
    description        VARCHAR(7000),
    event_date         timestamp,
    paid               boolean,
    participant_limit  INTEGER,
    request_moderation boolean,
    title              VARCHAR(120),
    created_on         timestamp,
    published_on       timestamp,
    initiator_id       BIGINT references users (id),
    state              VARCHAR(10),
    lat                real,
    lon                real
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      timestamp,
    event_id     BIGINT references events (id) ON DELETE CASCADE,
    requester_id BIGINT references users (id) ON DELETE CASCADE,
    status       VARCHAR(25),
    CONSTRAINT UQ_REQUEST UNIQUE (event_id, requester_id)
);