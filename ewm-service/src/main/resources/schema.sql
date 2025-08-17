--DROP TABLE IF EXISTS categories, users, events, compilations, compilations_events, requests, locations, comments;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    lat BIGINT NOT NULL,
    lon BIGINT NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category BIGINT NOT NULL,
    confirmed_requests BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator BIGINT NOT NULL,
    location BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(10) NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_events_category FOREIGN KEY (category) REFERENCES categories(id),
    CONSTRAINT fk_events_initiator FOREIGN KEY (initiator) REFERENCES users(id),
    CONSTRAINT fk_events_location FOREIGN KEY (location) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id),
    CONSTRAINT uq_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    id_compilation BIGINT NOT NULL,
    id_event BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (id_compilation, id_event),
    CONSTRAINT fk_compilations_events_compilation FOREIGN KEY (id_compilation) REFERENCES compilations(id),
    CONSTRAINT fk_compilations_events_event FOREIGN KEY (id_event) REFERENCES events(id),
    CONSTRAINT uq_compilations_events UNIQUE (id_compilation, id_event)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester BIGINT NOT NULL,
    event BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT uq_requests UNIQUE (requester, event)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text VARCHAR(7000) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    visible BOOLEAN NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_event FOREIGN KEY (event_id) REFERENCES events(id)
);