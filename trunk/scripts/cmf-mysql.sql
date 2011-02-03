--
-- Name: namespace; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE namespace (
    id bigint NOT NULL,
    name text NOT NULL,
    parent_id bigint,
	primary key(id)
);


--
-- Name: content; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE content (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    content text,
    date_created datetime,
    date_modified datetime,
	primary key(id),
	foreign key (namespace_id) references namespace(id)
);


--
-- Name: style; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE style (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    style text,
	primary key(id),
	foreign key (namespace_id) references namespace(id)
);


--
-- Name: style_to_content; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE style_to_content (
    id bigint NOT NULL,
    content_id bigint NOT NULL,
    style_id bigint NOT NULL,
	primary key(id),
	foreign key (content_id) references content(id),
	foreign key (style_id) references style(id)
);

--
-- Name: id_gen; Type: TABLE; Schema: public; Owner: Tablespace:
--

CREATE TABLE id_gen (
    gen_name character varying(80) NOT NULL,
    gen_val integer
);

INSERT INTO id_gen VALUES('style_id', 100);
INSERT INTO id_gen VALUES('content_id', 100);
INSERT INTO id_gen VALUES('namespace_id', 100);
INSERT INTO id_gen VALUES('style_to_content_id', 100);
