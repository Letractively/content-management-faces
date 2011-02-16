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

package net.tralfamadore.cmf;

import net.tralfamadore.config.CmfContext;
import net.tralfamadore.persistence.EntityManagerProvider;
import net.tralfamadore.persistence.entity.ContentEntity;
import net.tralfamadore.persistence.entity.NamespaceEntity;
import net.tralfamadore.persistence.entity.StyleEntity;
import net.tralfamadore.persistence.entity.StyleToContentEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 2/1/11
 * Time: 1:15 PM
 */
public class JpaContentManager implements ContentManager {
    private final EntityManagerProvider entityManagerProvider = CmfContext.getInstance().getEntityManagerProvider();
    private EntityManager em = entityManagerProvider.get();

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Namespace> loadAllNamespaces() {
        List<Namespace> namespaces = new Vector<Namespace>();
        List results = em.createQuery("select n from namespace n").getResultList();
        for(Object result : results) {
            NamespaceEntity namespaceEntity = (NamespaceEntity) result;
            String[] nodeNames = namespaceEntity.getName().split("\\.");
            makeNamespaceNodes(namespaces, nodeNames);
        }

        return namespaces;
    }

    @Override
    public List<Namespace> loadNamespace(Namespace namespace) {
        List<Namespace> namespaces = new Vector<Namespace>();
        Query query = em.createQuery("select n from namespace n where n.name = ?1 or n.name like ?2");
        query.setParameter(1, namespace.getFullName());
        query.setParameter(2, namespace.getFullName() + ".%");
        List results = query.getResultList();

        for(Object result : results) {
            NamespaceEntity namespaceEntity = (NamespaceEntity) result;
            String[] nodeNames = namespaceEntity.getName().split("\\.");
            makeNamespaceNodes(namespaces, nodeNames);
        }

        return namespaces;
    }

    private void makeNamespaceNodes(List<Namespace> namespaces, String[] nodeNames) {
        Namespace parent = null;
        for(String nodeName : nodeNames) {
            Namespace ns = new Namespace();
            ns.setNodeName(nodeName);
            ns.setParent(parent);
            if(!namespaces.contains(ns))
                namespaces.add(ns);
            parent = ns;
        }
    }

    @Override
    public void saveNamespace(Namespace namespace) {
        try {
            Object result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, namespace.getFullName()).getSingleResult();
            if(result != null)
                return;
        } catch(NoResultException e) {
            // then save it
        }

        String[] nodeNames = namespace.getFullName().split("\\.");
        String parentName = namespace.getFullName().replaceAll("\\." + nodeNames[nodeNames.length - 1] + "$", "");

        Query q = em.createQuery("select n.id from namespace n where n.name = ?1");
        q.setParameter(1, parentName);
        Long parentId;
        try {
            parentId = (Long) q.getSingleResult();
        } catch(NoResultException e) {
            parentId = null; // root node
        }

        NamespaceEntity namespaceEntity = new NamespaceEntity();
        namespaceEntity.setName(namespace.getFullName());
        if(parentId != null)
            namespaceEntity.setParentId(parentId);

        q = em.createQuery("select n from namespace n where n.name = ?1");
        q.setParameter(1, namespaceEntity.getName());
        if(q.getResultList().size() > 0)
            return;

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(namespaceEntity);
        t.commit();
    }

    @Override
    public void deleteNamespace(Namespace namespace) {
        Query query = em.createQuery("select n from namespace n where n.name = ?1");
        query.setParameter(1, namespace.getFullName());
        Object ns;
        try {
            ns = query.getSingleResult();
        } catch(NoResultException e) {
            return;
        }

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.remove(ns);
        t.commit();
    }

    @Override
    public List<Content> loadAllContent() {
        List<Content> contentList = new Vector<Content>();
        List contentEntities = em.createQuery("select c from content c").getResultList();
        for(Object o : contentEntities) {
            ContentEntity contentEntity = (ContentEntity) o;
            NamespaceEntity n = contentEntity.getNamespace();

            Content content = new Content();
            content.setContent(contentEntity.getContent());
            content.setName(contentEntity.getName());
            content.setNamespace(Namespace.createFromString(n.getName()));
            content.setDateCreated(contentEntity.getDateCreated());
            content.setDateModified(contentEntity.getDateModified());
            contentList.add(content);
        }

        return contentList;
    }

    @Override
    public List<Content> loadContent(Namespace namespace) {
        List<Content> contentList = new Vector<Content>();

        List contentEntities =  em.createQuery("select c from content c where c.namespace.name = ?1").
                setParameter(1, namespace.getFullName()).getResultList();

        for(Object o : contentEntities) {
            ContentEntity contentEntity = (ContentEntity) o;
            NamespaceEntity n = contentEntity.getNamespace();

            Content content = new Content();
            content.setContent(contentEntity.getContent());
            content.setName(contentEntity.getName());
            content.setNamespace(Namespace.createFromString(n.getName()));
            content.setDateCreated(contentEntity.getDateCreated());
            content.setDateModified(contentEntity.getDateModified());
            contentList.add(content);
        }

        return contentList;
    }

    @Override
    public Content loadContent(Namespace namespace, String name) {
        Object result;
        try {
            result = em.createQuery("select c from content c where c.namespace.name = ?1 and c.name = ?2").
                    setParameter(1, namespace.getFullName()).setParameter(2, name).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

        ContentEntity contentEntity = (ContentEntity) result;
        NamespaceEntity n = contentEntity.getNamespace();

        Content content = new Content();
        content.setContent(contentEntity.getContent());
        content.setName(contentEntity.getName());
        content.setNamespace(Namespace.createFromString(n.getName()));
        content.setDateCreated(contentEntity.getDateCreated());
        content.setDateModified(contentEntity.getDateModified());

        return content;
    }

    @Override
    public void saveContent(Content content) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, content.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't save content with non-existent namespace: "
                    + content.getNamespace().getFullName());
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from content c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, content.getName()).getSingleResult();
        } catch(NoResultException e) {
            result = null;
        }

        ContentEntity contentEntity;
        if(result == null) {
            contentEntity = new ContentEntity();
            contentEntity.setName(content.getName());
            contentEntity.setContent(content.getContent());
            contentEntity.setDateCreated(new Date());
            contentEntity.setDateModified(new Date());
            contentEntity.setNamespace(ne);
        } else {
            contentEntity = (ContentEntity) result;
            contentEntity.setContent(content.getContent());
            contentEntity.setDateModified(new Date());
        }

// TODO:       setContentStyles()...

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(contentEntity);
        t.commit();
    }

    @Override
    public void deleteContent(Content content) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, content.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            return;
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from content c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, content.getName()).getSingleResult();
        } catch(NoResultException e) {
            return;
        }
        ContentEntity contentEntity = (ContentEntity) result;
        List results = em.createQuery(
                "select c, n from style c, style_to_content s, namespace n " +
                        " where s.contentId = ?1 and c.id = s.styleId and n.id = c.namespaceId").
                setParameter(1, contentEntity.getId()).getResultList();
        for(Object r : results) {
            Object o[] = (Object[]) r;
            StyleEntity styleEntity = (StyleEntity) o[0];
            ne = (NamespaceEntity) o[1];
            Style style = new Style();
            style.setStyle(styleEntity.getStyle());
            style.setNamespace(Namespace.createFromString(ne.getName()));
// TODO:            perhaps we can do this through a cascade...
            style.setName(styleEntity.getName());
            disassociateWithContent(content, style);
        }

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.remove(contentEntity);
        t.commit();
    }

    @Override
    public List<Script> loadAllScripts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Script> loadScript(Namespace namespace) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Script loadScript(Namespace namespace, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveScript(Script script) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteScript(Script script) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Style> loadAllStyles() {
        List<Style> styleList = new Vector<Style>();
        List styleEntities = em.createQuery("select c from style c").getResultList();
        for(Object o : styleEntities) {
            StyleEntity styleEntity = (StyleEntity) o;
            Query q = em.createQuery("select n from namespace n where n.id = ?1");
            q.setParameter(1, styleEntity.getNamespaceId());
            NamespaceEntity n = (NamespaceEntity) q.getSingleResult();

            Style style = new Style();
            style.setStyle(styleEntity.getStyle());
            style.setName(styleEntity.getName());
            style.setNamespace(Namespace.createFromString(n.getName()));
            styleList.add(style);
        }

        return styleList;
    }

    @Override
    public List<Style> loadStyle(Namespace namespace) {
        List<Style> styleList = new Vector<Style>();
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, namespace.getFullName()).getSingleResult();
        } catch(NoResultException e) {
            return styleList;
        }

        NamespaceEntity ne = (NamespaceEntity) result;
        List styleEntities = em.createQuery("select c from style c where c.namespaceId = ?1").
                setParameter(1, ne.getId()).getResultList();

        for(Object o : styleEntities) {
            StyleEntity styleEntity = (StyleEntity) o;
            Query q = em.createQuery("select n from namespace n where n.id = ?1");
            q.setParameter(1, styleEntity.getNamespaceId());
            NamespaceEntity n = (NamespaceEntity) q.getSingleResult();

            Style style = new Style();
            style.setStyle(styleEntity.getStyle());
            style.setName(styleEntity.getName());
            style.setNamespace(Namespace.createFromString(n.getName()));
            styleList.add(style);
        }

        return styleList;
    }

    @Override
    public Style loadStyle(Namespace namespace, String name) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, namespace.getFullName()).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from style c where c.namespaceId = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, name).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

        StyleEntity styleEntity = (StyleEntity) result;

        Style style = new Style();
        style.setStyle(styleEntity.getStyle());
        style.setName(styleEntity.getName());
        style.setNamespace(Namespace.createFromString(ne.getName()));

        return style;
    }

    @Override
    public void saveStyle(Style style) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, style.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't save style with non-existent namespace: "
                    + style.getNamespace().getFullName());
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from style c where c.namespaceId = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            result = null;
        }

        StyleEntity styleEntity;
        if(result == null) {
            styleEntity = new StyleEntity();
            styleEntity.setName(style.getName());
            styleEntity.setStyle(style.getStyle());
            styleEntity.setNamespaceId(ne.getId());
        } else {
            styleEntity = (StyleEntity) result;
            styleEntity.setStyle(style.getStyle());
        }

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(styleEntity);
        t.commit();
    }

    @Override
    public void deleteStyle(Style style) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, style.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            return;
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from style c where c.namespaceId = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            return;
        }
        StyleEntity styleEntity = (StyleEntity) result;
        List results = em.createQuery(
                "select c from content c, style_to_content s where s.styleId = ?1 and c.id = s.contentId").
                setParameter(1, styleEntity.getId()).getResultList();
        for(Object r : results) {
            ContentEntity contentEntity = (ContentEntity) r;
            Content content = new Content();
            content.setContent(contentEntity.getContent());
            content.setNamespace(Namespace.createFromString(ne.getName()));
            content.setDateCreated(contentEntity.getDateCreated());
            content.setDateModified(contentEntity.getDateModified());
            content.setName(contentEntity.getName());
            disassociateWithContent(content, style);
        }

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.remove(result);
        t.commit();
    }

    @Override
    public void associateWithContent(Content content, Script script) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Script> loadScriptsForContent(Content content) {
        return new Vector<Script>();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disassociateWithContent(Content content, Script script) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void associateWithContent(Content content, Style style) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, style.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Invalid namespace for style: " + content.getNamespace().getFullName());
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from style c where c.namespaceId = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't locate style: " + style.getName());
        }
        StyleEntity styleEntity = (StyleEntity) result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, content.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Invalid namespace for content: " + content.getNamespace().getFullName());
        }
        ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from content c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, content.getName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't locate content: " + content.getName());
        }
        ContentEntity contentEntity = (ContentEntity) result;


        Query q = em.createQuery("select s from style_to_content s where s.contentId = ?1 and s.styleId = ?2");
        q.setParameter(1, contentEntity.getId());
        q.setParameter(2, styleEntity.getId());
        try {
            result = q.getSingleResult();
            if(result != null)
                return; // already associated
        } catch(NoResultException e) {
            // ok, time to associate;
        }
        StyleToContentEntity styleToContentEntity = new StyleToContentEntity();
        styleToContentEntity.setContentId(contentEntity.getId());
        styleToContentEntity.setStyleId(styleEntity.getId());
        em.getTransaction().begin();
        em.persist(styleToContentEntity);
        em.getTransaction().commit();
    }

    @Override
    public List<Style> loadStylesForContent(Content content) {
        List<Style> styles = new Vector<Style>();
        if(content == null)
            return styles;
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, content.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Invalid namespace for content: " + content.getNamespace().getFullName());
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from content c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, content.getName()).getSingleResult();
        } catch(NoResultException e) {
            return styles;
        }
        ContentEntity contentEntity = (ContentEntity) result;

        Query query = em.createQuery("select s.styleId from style_to_content s where s.contentId = ?1");
        query.setParameter(1, contentEntity.getId());
        List results = query.getResultList();
        if(results.isEmpty())
            return styles;

        query = em.createQuery("select s from style s where s.id in ?1");
        query.setParameter(1, results);
        results = query.getResultList();

        for(Object o : results) {
            StyleEntity styleEntity = (StyleEntity) o;
            Style style = new Style();
            query = em.createQuery("select n from namespace n where n.id = ?1");
            query.setParameter(1, styleEntity.getNamespaceId());
            ne = (NamespaceEntity) query.getSingleResult();
            style.setNamespace(Namespace.createFromString(ne.getName()));
            style.setStyle(styleEntity.getStyle());
            style.setName(styleEntity.getName());
            styles.add(style);
        }

        return styles;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disassociateWithContent(Content content, Style style) {
        Object result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, style.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Invalid namespace for style: " + style.getNamespace().getFullName());
        }
        NamespaceEntity ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from style c where c.namespaceId = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't locate style: " + style.getName());
        }
        StyleEntity styleEntity = (StyleEntity) result;
        try {
            result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, content.getNamespace().getFullName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Invalid namespace for content: " + content.getNamespace().getFullName());
        }
        ne = (NamespaceEntity) result;
        try {
            result = em.createQuery("select c from content c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, content.getName()).getSingleResult();
        } catch(NoResultException e) {
            throw new RuntimeException("Can't locate content: " + content.getName());
        }
        ContentEntity contentEntity = (ContentEntity) result;


        Query q = em.createQuery("select s from style_to_content s where s.contentId = ?1 and s.styleId = ?2");
        q.setParameter(1, contentEntity.getId());
        q.setParameter(2, styleEntity.getId());
        try {
            result = q.getSingleResult();
        } catch(NoResultException e) {
            return;
        }
        em.getTransaction().begin();
        em.remove(result);
        em.getTransaction().commit();
    }
}
