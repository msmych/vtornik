create table if not exists vtornik.tags
(
    user_id    int          not null,
    movie_id   bigint       not null,
    type       varchar(255) not null,
    value      jsonb        not null,
    created_at timestamp    not null,
    updated_at timestamp    not null,
    primary key (user_id, movie_id, type)
);

create index if not exists tags_user_id_movie_id_idx on vtornik.tags (user_id, movie_id);