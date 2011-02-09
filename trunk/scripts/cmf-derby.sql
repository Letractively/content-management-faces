
CREATE TABLE namespace (
    id bigint NOT NULL primary key,
    name varchar(10000) NOT NULL,
    parent_id bigint
)



CREATE TABLE content (
    id bigint NOT NULL primary key,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    content varchar(10000),
    date_created datetime,
    date_modified datetime,
	foreign key (namespace_id) references namespace(id)
)



CREATE TABLE style (
    id bigint NOT NULL primary key,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    style varchar(10000),
	foreign key (namespace_id) references namespace(id)
)



CREATE TABLE style_to_content (
    id bigint NOT NULL primary key,
    content_id bigint NOT NULL,
    style_id bigint NOT NULL,
	foreign key (content_id) references content(id),
	foreign key (style_id) references style(id)
)


CREATE TABLE id_gen (
    gen_name character varying(80) NOT NULL,
    gen_val integer
)

INSERT INTO id_gen VALUES('style_id', 100)
INSERT INTO id_gen VALUES('content_id', 100)
INSERT INTO id_gen VALUES('namespace_id', 100)
INSERT INTO id_gen VALUES('style_to_content_id', 100)


CREATE TABLE users (
    id bigint NOT NULL primary key,
    username varchar(80) NOT NULL unique
)

CREATE TABLE groups (
    id bigint NOT NULL primary key,
    groupname varchar(80) NOT NULL unique
)

CREATE TABLE user_to_group (
    id bigint NOT NULL primary key,
    id user_id NOT NULL,
    id group_id NOT NULL,
	foreign key (user_id) references users(id),
	foreign key (group_id) references groups(id),
	constraint user_group_constraint unique(user_id, group_id)
)
