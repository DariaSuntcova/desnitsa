CREATE TABLE about
(
    id               BIGSERIAL PRIMARY KEY NOT NULL,
    constant         varchar,
    description      text,
    first_image_url  varchar,
    second_image_url varchar
);

CREATE TABLE news
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    title              varchar(255),
    description        text,
    news_date          timestamp             NOT NULL,
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    video_link         varchar,
    main_image_url     varchar,
    image_url_list     varchar
);

CREATE TABLE tours
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    title              varchar(255),
    description        text,
    tour_date          timestamp             NOT NULL,
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    main_image_url     varchar,
    image_url_list     varchar
);

CREATE TABLE partners
(
    id        BIGSERIAL PRIMARY KEY NOT NULL,
    title     varchar(50)           NOT NULL,
    image_url varchar,
    link      varchar
);

CREATE TABLE contacts
(
    id           BIGSERIAL PRIMARY KEY NOT NULL,
    phone_number varchar,
    address      varchar,
    email        varchar,
    telegram     varchar,
    vk_link      varchar,
    youtube_link varchar,
    rutube_link  varchar
);

CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY NOT NULL,
    username     varchar UNIQUE        NOT NULL,
    password     varchar,
    full_name    varchar,
    email        varchar,
    phone_number varchar,
    roles        varchar
);

CREATE TABLE role
(
    id      SERIAL PRIMARY KEY NOT NULL,
    user_id bigint,
    name    varchar(20)
);

CREATE TABLE images
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    image_name varchar(25),
    type       varchar(10),
    size       bigint,
    content    bytea
);

CREATE TABLE image_urls
(
    id        BIGSERIAL PRIMARY KEY NOT NULL,
    tour_id   bigint,
    news_id   bigint,
    image_url varchar
);

CREATE TABLE phone_number
(
    id           BIGSERIAL PRIMARY KEY NOT NULL,
    contacts_id  bigint,
    phone_number varchar(50)
)
