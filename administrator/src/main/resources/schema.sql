CREATE TABLE IF NOT EXISTS public.user_relation
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    employee character varying(100) NOT NULL,
    supervisor character varying(100) NOT NULL,
    version integer NOT NULL,
    company_id bigint NOT NULL,
    is_deleted boolean NOT NULL,
    created_at date NOT NULL,
    CONSTRAINT user_relation_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_relation_employee
    ON user_relation(employee);

CREATE INDEX IF NOT EXISTS idx_relation_company
    ON user_relation(company_id);

CREATE TABLE IF NOT EXISTS public.roles
(
    id   serial not null
        constraint roles_pkey
            primary key,
    name varchar(20)
);

CREATE TABLE IF NOT EXISTS public.users
(
    id       bigserial not null
        constraint users_pkey
            primary key,
    email    varchar(50)
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    password varchar(120),
    username varchar(20)
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique
);


CREATE TABLE IF NOT EXISTS public.user_roles
(
    user_id bigint  not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references users,
    role_id integer not null
        constraint fkh8ciramu9cc9q3qcqiv4ue8a6
            references roles
        constraint fkrhfovtciq1l558cw6udg0h0d3
            references role,
    constraint user_roles_pkey
        primary key (user_id, role_id)
);
