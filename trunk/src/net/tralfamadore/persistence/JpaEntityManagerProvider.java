/*
 * Copyright (c) 2011 Bill Reh.
 *
 * This file is part of Content Management Faces.
 *
 * Content Management Faces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.tralfamadore.persistence;

import net.tralfamadore.cmf.JpaContentManager;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.config.ConfigFile;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 9:06 AM
 */
public class JpaEntityManagerProvider implements EntityManagerProvider {
    private EntityManager em;
    private EntityManagerFactory emFactory;


    @Override
    public void shutdown() {
        if(em != null)
            em.close();

        if(emFactory != null)
            emFactory.close();
    }

    @Override
    public EntityManager get() {
        if(em == null) {
            ConfigFile configFile = CmfContext.getInstance().getConfigFile();

            Map<String,String> properties = new HashMap<String,String>();

            for(Map.Entry<String,String> entry : configFile.getPersistenceProperties().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }

            emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            em = emFactory.createEntityManager();
            boolean embedded =
                    properties.get("javax.persistence.jdbc.driver").equals("org.apache.derby.jdbc.EmbeddedDriver");
            boolean mem = properties.get("javax.persistence.jdbc.url").matches("^jdbc:derby:mem.*");
            if(embedded && mem) {
                CmfContext.getInstance().setEmbeddedDbNeedsConfig(true);
                try {
                    em.createQuery("select n from namespace n").getResultList();
                } catch(Exception e) {
                    if(e.getCause().getClass().equals(SQLSyntaxErrorException.class)
                            && e.getMessage().contains("does not exist"))
                    {
                        em.getTransaction().begin();
                        for(String query : createDerbyTables)
                            em.createNativeQuery(query).executeUpdate();
                        em.getTransaction().commit();
                    }
                }
            }
        }

        return em;
    }

    public void createEmbeddedDb(Properties properties) {
        shutdown();
        emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        em = emFactory.createEntityManager();

        em.getTransaction().begin();
        try {
            for(String query : createDerbyTables)
                em.createNativeQuery(query).executeUpdate();
            em.getTransaction().commit();
        } catch(Exception e) {
            // most likely the tables are already there, probably the cmf-config.xml was overwritten
            em.getTransaction().rollback();
        }

        ((JpaContentManager)CmfContext.getInstance().getContentManager()).setEm(em);
        try {
            editWebXml(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editWebXml(Properties properties) throws Exception {
        String url = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/cmf-config.xml");
        File file = new File(url);
        String contents = readFileAsString(file);
        String newUrl = properties.getProperty("javax.persistence.jdbc.url");
        System.out.println(contents);
        contents = contents.replaceAll("jdbc:derby:mem;create=true", newUrl);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(contents);
        writeStringToFile(file, contents);
    }
    private static void writeStringToFile(File file, String string) throws java.io.IOException{
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
        } finally {
            try { if(outputStream != null)outputStream.close(); } catch(Exception ignore) {}
        }

    }

    private static String readFileAsString(File file) throws java.io.IOException{
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream f = null;
        try {
            f =  new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            f.read(buffer);
        } finally {
            if(f != null) {
                try { f.close(); } catch (IOException ignore) {}
            }
        }
        return new String(buffer);
    }


    String createDerbyTables[] = {
            "CREATE TABLE namespace (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    name varchar(32000) NOT NULL,\n" +
                    "    parent_id bigint,\n" +
                    "\t primary key(id)\n" +
                    ")\n",
            "CREATE TABLE content (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    namespace_id bigint NOT NULL,\n" +
                    "    name character varying(256) NOT NULL,\n" +
                    "    content varchar(32000),\n" +
                    "    date_created timestamp,\n" +
                    "    date_modified timestamp,\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (namespace_id) references namespace(id)\n" +
                    ")\n",
            "CREATE TABLE style (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    namespace_id bigint NOT NULL,\n" +
                    "    name character varying(256) NOT NULL,\n" +
                    "    style varchar(32000),\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (namespace_id) references namespace(id)\n" +
                    ")\n",
            "CREATE TABLE style_to_content (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    content_id bigint NOT NULL,\n" +
                    "    style_id bigint NOT NULL,\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (content_id) references content(id),\n" +
                    "\t foreign key (style_id) references style(id)\n" +
                    ")\n",
            "CREATE TABLE id_gen (\n" +
                    "    gen_name character varying(80) NOT NULL,\n" +
                    "    gen_val integer\n" +
                    ")\n",
            "INSERT INTO id_gen VALUES('style_id', 100)\n",
            "INSERT INTO id_gen VALUES('content_id', 100)\n",
            "INSERT INTO id_gen VALUES('namespace_id', 100)\n",
            "INSERT INTO id_gen VALUES('style_to_content_id', 100)"};
}
