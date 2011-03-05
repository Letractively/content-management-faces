-- Primary key table
CREATE TABLE id_gen (
    gen_name character varying(80) NOT NULL,
    gen_val integer
);
ALTER TABLE id_gen ADD CONSTRAINT pk_id_gen PRIMARY KEY (gen_name);

-- Namespace
CREATE TABLE namespace (
    id bigint NOT NULL,
    name text NOT NULL unique,
    parent_id bigint
);
ALTER TABLE namespace ADD CONSTRAINT namespace_pkey1 PRIMARY KEY (id);
ALTER TABLE namespace ADD CONSTRAINT namespace_parent_id_fkey1 FOREIGN KEY (parent_id) REFERENCES namespace(id);

-- Content
CREATE TABLE content (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL unique,
    content text,
    date_created timestamp without time zone,
    date_modified timestamp without time zone
);
ALTER TABLE content ADD CONSTRAINT content_pkey PRIMARY KEY (id);
ALTER TABLE content ADD CONSTRAINT content_namespace_id_fkey FOREIGN KEY (namespace_id) REFERENCES namespace(id);

-- Style
CREATE TABLE style (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    style text
);
ALTER TABLE style ADD CONSTRAINT style_pkey PRIMARY KEY (id);
ALTER TABLE style ADD CONSTRAINT style_namespace_id_fkey FOREIGN KEY (namespace_id) REFERENCES namespace(id);

-- Style to Content
CREATE TABLE style_to_content (
    content_id bigint NOT NULL,
    style_id bigint NOT NULL
);
ALTER TABLE style_to_content ADD CONSTRAINT style_to_content_content_id_fkey FOREIGN KEY (content_id) REFERENCES content(id);
ALTER TABLE style_to_content ADD CONSTRAINT style_to_content_style_id_fkey FOREIGN KEY (style_id) REFERENCES style(id);

-- Users
CREATE TABLE users (
    id bigint NOT NULL,
    username character varying(80) NOT NULL,
    password character varying(360)
);
ALTER TABLE users ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE users ADD CONSTRAINT users_username_key UNIQUE (username);

-- Groups
CREATE TABLE groups (
    id bigint NOT NULL,
    groupname character varying(80) NOT NULL
);
ALTER TABLE groups ADD CONSTRAINT groups_groupname_key UNIQUE (groupname);
ALTER TABLE groups ADD CONSTRAINT groups_pkey PRIMARY KEY (id);

-- User to Group
CREATE TABLE user_to_group (
    user_id bigint NOT NULL,
    group_id bigint NOT NULL
);
ALTER TABLE user_to_group ADD CONSTRAINT user_group_constraint UNIQUE (user_id, group_id);
ALTER TABLE user_to_group ADD CONSTRAINT user_to_group_group_id_fkey FOREIGN KEY (group_id) REFERENCES groups(id);
ALTER TABLE user_to_group ADD CONSTRAINT user_to_group_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);

-- Group Permissions
CREATE TABLE group_permissions (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    can_view character(1) DEFAULT 'F'::bpchar NOT NULL,
    can_edit character(1) DEFAULT 'F'::bpchar NOT NULL,
    can_delete character(1) DEFAULT 'F'::bpchar NOT NULL,
    can_admin character(1) DEFAULT 'F'::bpchar NOT NULL
);
ALTER TABLE group_permissions ADD CONSTRAINT group_permissions_pkey PRIMARY KEY (id);
ALTER TABLE group_permissions ADD CONSTRAINT group_permissions_group_id_fkey FOREIGN KEY (group_id) REFERENCES groups(id);

-- Group Permissions to Content
CREATE TABLE group_permissions_to_content (
    group_permissions_id bigint NOT NULL,
    content_id bigint
);
ALTER TABLE group_permissions_to_content ADD CONSTRAINT group_permissions_to_content_content_id_fkey FOREIGN KEY (content_id) REFERENCES content(id);
ALTER TABLE group_permissions_to_content ADD CONSTRAINT group_permissions_to_content_group_permissions_id_fkey FOREIGN KEY (group_permissions_id) REFERENCES group_permissions(id);

-- Group Permissions to Namespace
CREATE TABLE group_permissions_to_namespace (
    group_permissions_id bigint NOT NULL,
    namespace_id bigint
);
ALTER TABLE group_permissions_to_namespace ADD CONSTRAINT group_permissions_to_namespace_group_permissions_id_fkey FOREIGN KEY (group_permissions_id) REFERENCES group_permissions(id);
ALTER TABLE group_permissions_to_namespace ADD CONSTRAINT group_permissions_to_namespace_namespace_id_fkey FOREIGN KEY (namespace_id) REFERENCES namespace(id);

-- Group Permissions to Style
CREATE TABLE group_permissions_to_style (
    group_permissions_id bigint NOT NULL,
    style_id bigint
);
ALTER TABLE group_permissions_to_style ADD CONSTRAINT group_permissions_to_style_group_permissions_id_fkey FOREIGN KEY (group_permissions_id) REFERENCES group_permissions(id);
ALTER TABLE group_permissions_to_style ADD CONSTRAINT group_permissions_to_style_style_id_fkey FOREIGN KEY (style_id) REFERENCES style(id);


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
