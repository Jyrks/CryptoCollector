create table transaction (
        id serial primary key,
        price double precision,
        cost double precision,
        amount double precision,
        type text,
        timestamp timestamp without time zone
)