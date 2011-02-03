
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
