create table if not exists movies
(
    id         bigint primary key,
    title      varchar(255) not null,
    runtime    int          not null,
    year       int          null,
    details    jsonb        not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);

create table if not exists people
(
    id         bigint primary key,
    name       varchar(255) not null,
    details    jsonb        not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);

create table if not exists movies_people
(
    person_id  bigint       not null,
    movie_id   bigint       not null,
    role       varchar(255) not null,
    created_at timestamp    not null,
    primary key (person_id, movie_id)
);