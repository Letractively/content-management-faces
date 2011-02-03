--
-- Name: content; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE content (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    content text,
    date_created timestamp,
    date_modified timestamp
);


--
-- Name: namespace; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE namespace (
    id bigint NOT NULL,
    name text NOT NULL,
    parent_id bigint
);


--
-- Name: style; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE style (
    id bigint NOT NULL,
    namespace_id bigint NOT NULL,
    name character varying(256) NOT NULL,
    style text
);


--
-- Name: style_to_content; Type: TABLE; Schema: public; Owner: Tablespace: 
--

CREATE TABLE style_to_content (
    id bigint NOT NULL,
    content_id bigint NOT NULL,
    style_id bigint NOT NULL
);

--
-- Name: id_gen; Type: TABLE; Schema: public; Owner: Tablespace:
--

CREATE TABLE id_gen (
    gen_name character varying(80) NOT NULL,
    gen_val integer
);


--
-- Name: content_pkey; Type: CONSTRAINT; Schema: public; Owner: Tablespace: 
--

ALTER TABLE ONLY content
    ADD CONSTRAINT content_pkey PRIMARY KEY (id);


--
-- Name: namespace_pkey1; Type: CONSTRAINT; Schema: public; Owner: Tablespace: 
--

ALTER TABLE ONLY namespace
    ADD CONSTRAINT namespace_pkey1 PRIMARY KEY (id);


--
-- Name: style_pkey; Type: CONSTRAINT; Schema: public; Owner: Tablespace: 
--

ALTER TABLE ONLY style
    ADD CONSTRAINT style_pkey PRIMARY KEY (id);


--
-- Name: style_to_content_pkey; Type: CONSTRAINT; Schema: public; Owner: Tablespace: 
--

ALTER TABLE ONLY style_to_content
    ADD CONSTRAINT style_to_content_pkey PRIMARY KEY (id);


--
-- Name: content_namespace_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: 
--

ALTER TABLE ONLY content
    ADD CONSTRAINT content_namespace_id_fkey FOREIGN KEY (namespace_id) REFERENCES namespace(id);


--
-- Name: namespace_parent_id_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: 
--

ALTER TABLE ONLY namespace
    ADD CONSTRAINT namespace_parent_id_fkey1 FOREIGN KEY (parent_id) REFERENCES namespace(id);


--
-- Name: style_namespace_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: 
--

ALTER TABLE ONLY style
    ADD CONSTRAINT style_namespace_id_fkey FOREIGN KEY (namespace_id) REFERENCES namespace(id);


--
-- Name: style_to_content_content_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: 
--

ALTER TABLE ONLY style_to_content
    ADD CONSTRAINT style_to_content_content_id_fkey FOREIGN KEY (content_id) REFERENCES content(id);


--
-- Name: style_to_content_style_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: 
--

ALTER TABLE ONLY style_to_content
    ADD CONSTRAINT style_to_content_style_id_fkey FOREIGN KEY (style_id) REFERENCES style(id);


--
-- Name: pk_id_gen; Type: CONSTRAINT; Schema: public; Owner: Tablespace:
--

ALTER TABLE ONLY id_gen
    ADD CONSTRAINT pk_id_gen PRIMARY KEY (gen_name);

INSERT INTO id_gen VALUES('style_id', 100);
INSERT INTO id_gen VALUES('content_id', 100);
INSERT INTO id_gen VALUES('namespace_id', 100);
INSERT INTO id_gen VALUES('style_to_content_id', 100);