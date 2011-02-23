CREATE TABLE namespace (
    id bigint NOT NULL primary key,
    name varchar(10000) NOT NULL UNIQUE,
    parent_id bigint,
    foreign key (parent_id) references namespace(id)
);

CREATE TABLE content (
    id bigint NOT NULL primary key,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    content varchar(10000),
    date_created timestamp,
    date_modified timestamp,
	foreign key (namespace_id) references namespace(id)
);

CREATE TABLE style (
    id bigint NOT NULL primary key,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    style varchar(10000),
	foreign key (namespace_id) references namespace(id)
);

CREATE TABLE style_to_content (
    content_id bigint NOT NULL,
    style_id bigint NOT NULL,
	foreign key (content_id) references content(id),
	foreign key (style_id) references style(id)
);

CREATE TABLE id_gen (
    gen_name character varying(80) NOT NULL,
    gen_val integer
);

CREATE TABLE users (
    id bigint NOT NULL primary key,
    username varchar(80) NOT NULL unique,
    password varchar(360)
);

CREATE TABLE groups (
    id bigint NOT NULL primary key,
    groupname varchar(80) NOT NULL unique
);

CREATE TABLE user_to_group (
    user_id bigint NOT NULL,
    group_id bigint NOT NULL,
	foreign key (user_id) references users(id),
	foreign key (group_id) references groups(id),
	constraint user_group_constraint unique(user_id, group_id)
);

CREATE TABLE group_permissions (
    id bigint NOT NULL primary key,
    group_id bigint NOT NULL,
    can_view char(1) NOT NULL DEFAULT 'F',
    can_edit char(1) NOT NULL DEFAULT 'F',
    can_delete char(1) NOT NULL DEFAULT 'F',
    can_admin char(1) NOT NULL DEFAULT 'F',
	foreign key (group_id) references groups(id)
);

CREATE TABLE group_permissions_to_content (
    group_permissions_id bigint NOT NULL references group_permissions(id),
    content_id bigint not null references content(id)
);

CREATE TABLE group_permissions_to_namespace (
    group_permissions_id bigint NOT NULL references group_permissions(id),
    namespace_id bigint not null references namespace(id)
);

-- Group Permissions to Style
CREATE TABLE group_permissions_to_style (
    group_permissions_id bigint NOT NULL references group_permissions(id),
    style_id bigint not null references style(id)
);

-- Default Users
INSERT INTO users VALUES(1, 'cmfAdmin', 'cmfAdmin');
-- Default Groups
INSERT INTO groups VALUES(1, 'cmfAdmin');
INSERT INTO groups VALUES(2, 'users');
-- Primary Key Names
INSERT INTO id_gen VALUES('style_id', 100);
INSERT INTO id_gen VALUES('content_id', 100);
INSERT INTO id_gen VALUES('namespace_id', 100);
INSERT INTO id_gen VALUES('group_permissions_id', 100);
INSERT INTO id_gen VALUES('groups_id', 100);
INSERT INTO id_gen VALUES('users_id', 100);
