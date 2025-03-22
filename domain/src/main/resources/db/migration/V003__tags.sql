create table if not exists tags
(
    user_id    int          not null,
    movie_id   bigint       not null,
    tag        varchar(255) not null,
    created_at timestamp    not null,
    primary key (user_id, movie_id, tag)
);

create index if not exists tags_user_id_movie_id_idx on tags (user_id, movie_id);