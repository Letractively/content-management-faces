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

package net.tralfamadore.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 9:09 AM
 */
public class ConfigFile {
    private String contentManager;
    private String entityManagerProvider;
    private Map<String,String> persistenceProperties = new HashMap<String, String>();
    private String customLoginUrl;

    public ConfigFile() {
        this(true);
    }

    public ConfigFile(boolean parse) {
        if(parse)
            parseXml();
    }

    public String getContentManager() {
        return contentManager;
    }

    public void setContentManager(String contentManager) {
        this.contentManager = contentManager;
    }

    public String getEntityManagerProvider() {
        return entityManagerProvider;
    }

    public void setEntityManagerProvider(String entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    public Map<String, String> getPersistenceProperties() {
        return persistenceProperties;
    }

    public void setPersistenceProperties(Map<String, String> persistenceProperties) {
        this.persistenceProperties = persistenceProperties;
    }

    public void parseXml() {
        InputStream stream = null;
        try {
            stream = FacesContext.getCurrentInstance().getExternalContext().
                    getResourceAsStream("/WEB-INF/cmf-config.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(stream);
            Element root = document.getDocumentElement();

            NodeList nodes = root.getElementsByTagName("content-manager");
            for(int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if(n.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                contentManager = nodes.item(i).getTextContent();
            }

            nodes = root.getElementsByTagName("entity-manager-provider");
            for(int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if(n.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                entityManagerProvider = nodes.item(i).getTextContent();
            }

            nodes = root.getElementsByTagName("persistence-properties");
            for(int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if(n.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                parsePersistenceProperties(nodes.item(i));
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            try {
                if(stream != null)
                    stream.close();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void parsePersistenceProperties(Node persistencePropertiesNode) {
        NodeList nodes = persistencePropertiesNode.getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            if(!node.getNodeName().equalsIgnoreCase("persistence-property"))
                continue;

            Map.Entry<String,String> entry = parsePersistenceProperty(node);
            persistenceProperties.put(entry.getKey(), entry.getValue());
        }
    }

    private Map.Entry<String,String> parsePersistenceProperty(Node persistencePropertyNode) {
        Node n = persistencePropertyNode.getFirstChild().getNextSibling();
        String s = n.getFirstChild().getTextContent();
        String v = n.getNextSibling().getNextSibling().getFirstChild().getTextContent();
        return new AbstractMap.SimpleEntry<String, String>(s, v);
    }

    public String getCustomLoginUrl() {
        return customLoginUrl;
    }
}
