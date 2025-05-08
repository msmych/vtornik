create table if not exists vtornik.users
(
    id         serial primary key,
    username   varchar(255) not null,
    details    jsonb        not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);

create unique index if not exists users_username_idx on vtornik.users (username);
create unique index if not exists users_details_github_id_idx on vtornik.users ((details -> 'github' -> 'id')) where details ? 'github';