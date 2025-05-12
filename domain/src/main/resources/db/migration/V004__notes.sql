create table if not exists vtornik.notes
(
    user_id    int       not null,
    movie_id   bigint    not null,
    note       text      not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (user_id, movie_id)
);