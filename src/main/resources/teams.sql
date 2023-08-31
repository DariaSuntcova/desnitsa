CREATE TABLE members
(
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    first_name  VARCHAR,
    last_name   VARCHAR,
    speciality   VARCHAR,
    profession  VARCHAR,
    description VARCHAR,
    image_url   VARCHAR
)