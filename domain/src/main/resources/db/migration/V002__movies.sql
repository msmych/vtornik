create table if not exists movies
(
    id         bigint primary key,
    title      varchar(255) not null,
    year       int          null,
    details    jsonb        not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);
