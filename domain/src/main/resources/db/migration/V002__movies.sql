create table if not exists vtornik.movies
(
    id             bigint primary key,
    title          varchar(1023) not null,
    runtime        int           not null,
    overview       text          not null,
    release_date   date          null,
    original_title varchar(1023) null,
    tmdb           jsonb         null,
    created_at     timestamp     not null,
    updated_at     timestamp     not null
);

create table if not exists vtornik.people
(
    id         bigint primary key,
    name       varchar(255) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);

create table if not exists vtornik.movies_people
(
    person_id  bigint       not null,
    movie_id   bigint       not null,
    role       varchar(255) not null,
    created_at timestamp    not null,
    primary key (person_id, movie_id)
);