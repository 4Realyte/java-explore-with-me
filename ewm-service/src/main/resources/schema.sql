CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254),
    name  VARCHAR(250),
    CONSTRAINT UQ_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS category
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(50),
    CONSTRAINT UQ_NAME UNIQUE (name)
);