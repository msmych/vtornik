alter table vtornik.movies
    add column mentions     jsonb not null default '{}'::jsonb,
    add column release_date date  null;