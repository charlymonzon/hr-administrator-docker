INSERT INTO public.user_relation(employee, supervisor, version, company_id, created_at, is_deleted) VALUES ('Nick', 'Sophie', 1, 1, current_timestamp, false);
INSERT INTO public.user_relation(employee, supervisor, version, company_id, created_at, is_deleted) VALUES ('Pete', 'Nick', 1, 1, current_timestamp, false);
INSERT INTO public.user_relation(employee, supervisor, version, company_id, created_at, is_deleted) VALUES ('Sophie', 'Jonas', 1, 1, current_timestamp, false);
INSERT INTO public.user_relation(employee, supervisor, version, company_id, created_at, is_deleted) VALUES ('Barbara', 'Nick', 1, 1, current_timestamp, false);

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

INSERT INTO public.users(email, password, username)	VALUES ('test@hr-administrator.com', '$2a$10$NKrl8EY2MxuMlr5pO4Mou.M0DCj6T7zCTpvG/Xu4GylfecI5fmBjm', 'test');

INSERT INTO public.user_roles( user_id, role_id) VALUES (1, 3);
