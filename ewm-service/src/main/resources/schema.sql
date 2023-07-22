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
CREATE TABLE IF NOT EXISTS locations
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat           float(4),
    lon           float(4),
    rad           float(4),
    location_name varchar(200),
    state varchar(10),
    CONSTRAINT UQ_LOCATION UNIQUE (lat, lon, rad)
);
CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000),
    category_id        BIGINT REFERENCES categories (id) ON DELETE RESTRICT,
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
    location_id        BIGINT REFERENCES locations (id) ON DELETE RESTRICT
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
CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned boolean,
    title  varchar(50)
);
CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT CO_PRIMARY PRIMARY KEY (compilation_id, event_id)
);
CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
    declare
        dist      float = 0;
        rad_lat1  float;
        rad_lat2  float;
        theta     float;
        rad_theta float;
    BEGIN
        IF lat1 = lat2 AND lon1 = lon2
        THEN
            RETURN dist;
        ELSE
            -- переводим градусы широты в радианы
            rad_lat1 = pi() * lat1 / 180;
            -- переводим градусы долготы в радианы
            rad_lat2 = pi() * lat2 / 180;
            -- находим разность долгот
            theta = lon1 - lon2;
            -- переводим градусы в радианы
            rad_theta = pi() * theta / 180;
            -- находим длину ортодромии
            dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

            IF dist > 1
            THEN
                dist = 1;
            END IF;

            dist = acos(dist);
            -- переводим радианы в градусы
            dist = dist * 180 / pi();
            -- переводим градусы в километры
            dist = dist * 60 * 1.8524;

            RETURN round(dist::numeric, 4);
        END IF;
    END;
'
    LANGUAGE PLPGSQL;