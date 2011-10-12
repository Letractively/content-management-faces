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
import net.tralfamadore.persistence.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * User: billreh
 * Date: 2/1/11
 * Time: 1:15 PM
 */
public class JpaContentManager implements ContentManager {
    private final EntityManagerProvider entityManagerProvider = CmfContext.getInstance().getEntityManagerProvider();
    private EntityManager em = entityManagerProvider.get();

    public List<Namespace> makeNamespaceNodes(NamespaceEntity namespaceEntity) {
        List<Namespace> namespaces = new Vector<Namespace>();
        String[] nodeNames = namespaceEntity.getName().split("\\.");
        Namespace parent = null;
        String name = namespaceEntity.getName();

        for(String nodeName : nodeNames) {
            Namespace ns = new Namespace();
            ns.setNodeName(nodeName);
            ns.setParent(parent);
            namespaceEntity = (NamespaceEntity) em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, name).getSingleResult();
            ns.setGroupPermissionsList(makeGroupPermissions(namespaceEntity.getGroupPermissions()));
            if(!namespaces.contains(ns))
                namespaces.add(ns);
            parent = ns;
            name = name.replaceAll("\\." + nodeName + "$", "");
        }

        return namespaces;
    }

    public static Content makeContent(ContentEntity contentEntity) {
        Content content = new Content();
        content.setContent(contentEntity.getContent());
        content.setName(contentEntity.getName());
        content.setNamespace(Namespace.createFromString(contentEntity.getNamespace().getName()));
        content.setDateCreated(contentEntity.getDateCreated());
        content.setDateModified(contentEntity.getDateModified());
        content.getGroupPermissionsList().addAll(makeGroupPermissions(contentEntity.getGroupPermissions()));
        content.getStyles().addAll(makeStyles(contentEntity.getStyles()));

        return content;
    }

    public static Style makeStyle(StyleEntity styleEntity) {
        Style style = new Style();

        style.setName(styleEntity.getName());
        style.setNamespace(Namespace.createFromString(styleEntity.getNamespace().getName()));
        style.setStyle(styleEntity.getStyle());
        style.getGroupPermissionsList().addAll(makeGroupPermissions(styleEntity.getGroupPermissions()));

        return style;
    }

    public static List<Style> makeStyles(Set<StyleEntity> styleEntities){
        List<Style> styles = new Vector<Style>();

        if(styleEntities == null)
            return styles;
        for(StyleEntity styleEntity : styleEntities) {
            styles.add(makeStyle(styleEntity));
        }

        return styles;
    }

    public static List<GroupPermissions> makeGroupPermissions(Set<GroupPermissionsEntity> groupPermissionsEntities) {
        List<GroupPermissions> groupPermissionsList = new Vector<GroupPermissions>();
        if(groupPermissionsEntities == null)
            return groupPermissionsList;

        for(GroupPermissionsEntity groupPermissionsEntity : groupPermissionsEntities) {
            GroupPermissions groupPermissions = new GroupPermissions();
            groupPermissions.setCanView(groupPermissionsEntity.canView());
            groupPermissions.setCanEdit(groupPermissionsEntity.canEdit());
            groupPermissions.setCanDelete(groupPermissionsEntity.canDelete());
            groupPermissions.setCanAdmin(groupPermissionsEntity.canAdmin());
            groupPermissions.setGroup(groupPermissionsEntity.getGroup().getGroupname());
            groupPermissionsList.add(groupPermissions);
        }

        return groupPermissionsList;
    }

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
            for(Namespace namespace : makeNamespaceNodes(namespaceEntity)) {
                if(!namespaces.contains(namespace))
                    namespaces.add(namespace);
            }
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
            for(Namespace ns : makeNamespaceNodes(namespaceEntity)) {
                if(!namespaces.contains(ns))
                    namespaces.add(ns);
            }
        }

        return namespaces;
    }

    @Override
    public void saveNamespace(Namespace namespace) {
        NamespaceEntity namespaceEntity = new NamespaceEntity();
        try {
            Object result = em.createQuery("select n from namespace n where n.name = ?1").
                    setParameter(1, namespace.getFullName()).getSingleResult();
            if(result != null)
                namespaceEntity = (NamespaceEntity) result;
        } catch(NoResultException e) {
            // then save the new one
        }

        String[] nodeNames = namespace.getFullName().split("\\.");
        String parentName = namespace.getFullName().replaceAll("\\." + nodeNames[nodeNames.length - 1] + "$", "");

        Query q = em.createQuery("select n.id from namespace n where n.name = ?1");
        q.setParameter(1, parentName);
        Long parentId;
        try {
            parentId = (Long) q.getSingleResult();
        } catch(NoResultException e) {
            if(parentName.contains("."))
                throw new RuntimeException("Can't find parent namespace " + parentName);
            parentId = null; // root node
        }

        namespaceEntity.setName(namespace.getFullName());
        if(parentId != null)
            namespaceEntity.setParentId(parentId);
        makeGroupPermissionsEntity(namespaceEntity, namespace.getGroupPermissionsList());

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(namespaceEntity);
        t.commit();
    }


    private void makeGroupPermissionsEntity(NamespaceEntity namespaceEntity,
                                            List<GroupPermissions> groupPermissionsList)
    {
        if(groupPermissionsList.isEmpty()) {
            if(namespaceEntity.getGroupPermissions() != null)
                namespaceEntity.getGroupPermissions().clear();
            return;
        }

        if(namespaceEntity.getGroupPermissions() == null)
            namespaceEntity.setGroupPermissions(new HashSet<GroupPermissionsEntity>());

        clearOldGroupPermissions(namespaceEntity.getGroupPermissions(), groupPermissionsList);
        addNewGroupPermissions(namespaceEntity.getGroupPermissions(), groupPermissionsList);
    }

    private void makeGroupPermissionsEntity(ContentEntity contentEntity, List<GroupPermissions> groupPermissionsList) {
        if(groupPermissionsList.isEmpty()) {
            if(contentEntity.getGroupPermissions() != null)
                contentEntity.getGroupPermissions().clear();
            return;
        }

        if(contentEntity.getGroupPermissions() == null)
            contentEntity.setGroupPermissions(new HashSet<GroupPermissionsEntity>());
        clearOldGroupPermissions(contentEntity.getGroupPermissions(), groupPermissionsList);
        addNewGroupPermissions(contentEntity.getGroupPermissions(), groupPermissionsList);
    }

    private void clearOldGroupPermissions(Set<GroupPermissionsEntity> groupPermissionsEntities,
                                          List<GroupPermissions> groupPermissionsList)
    {
        if(groupPermissionsEntities == null)
            return;
        for(Iterator<GroupPermissionsEntity> it = groupPermissionsEntities.iterator(); it.hasNext(); ) {
            boolean found = false;
            GroupPermissionsEntity groupPermissionsEntity = it.next();
            String name = groupPermissionsEntity.getGroup().getGroupname();

            for(GroupPermissions groupPermissions : groupPermissionsList) {
                if(name.equals(groupPermissions.getGroup())) {
                    found = true;
                    break;
                }
            }

            if(!found)
                it.remove();

        }
    }

    private void addNewGroupPermissions(Set<GroupPermissionsEntity> groupPermissionsEntities,
                                        List<GroupPermissions> groupPermissionsList) {
        for(GroupPermissions groupPermissions : groupPermissionsList) {
            boolean found = false;
            String name = groupPermissions.getGroup();

            if(groupPermissionsEntities == null)
                groupPermissionsEntities = new HashSet<GroupPermissionsEntity>();

            for(GroupPermissionsEntity groupPermissionsEntity : groupPermissionsEntities) {
                if(name.equals(groupPermissionsEntity.getGroup().getGroupname())) {
                    found = true;
                    groupPermissionsEntity.setCanAdmin(groupPermissions.isCanAdmin());
                    groupPermissionsEntity.setCanDelete(groupPermissions.isCanDelete());
                    groupPermissionsEntity.setCanEdit(groupPermissions.isCanEdit());
                    groupPermissionsEntity.setCanView(groupPermissions.isCanView());
                    break;
                }
            }

            if(!found) {
                GroupPermissionsEntity gpe = new GroupPermissionsEntity();
                GroupEntity group = (GroupEntity) em.createQuery("select g from groups g where g.groupname = ?1")
                        .setParameter(1, name).getSingleResult();
                gpe.setGroup(group);
                gpe.setCanAdmin(groupPermissions.isCanAdmin());
                gpe.setCanView(groupPermissions.isCanView());
                gpe.setCanEdit(groupPermissions.isCanEdit());
                gpe.setCanDelete(groupPermissions.isCanDelete());
                groupPermissionsEntities.add(gpe);
            }
        }
    }

    private void makeGroupPermissionsEntity(StyleEntity styleEntity, List<GroupPermissions> groupPermissionsList) {
        if(groupPermissionsList.isEmpty()) {
            if(styleEntity.getGroupPermissions() != null)
                styleEntity.getGroupPermissions().clear();
            return;
        }

        clearOldGroupPermissions(styleEntity.getGroupPermissions(), groupPermissionsList);
        addNewGroupPermissions(styleEntity.getGroupPermissions(), groupPermissionsList);
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
            contentList.add(makeContent(contentEntity));
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
            contentList.add(makeContent(contentEntity));
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

        return makeContent(contentEntity);
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
            contentEntity.setStyles(new HashSet<StyleEntity>());
            contentEntity.getStyles().addAll(makeStyleEntities(content.getStyles()));
        } else {
            contentEntity = (ContentEntity) result;
            contentEntity.setContent(content.getContent());
            contentEntity.setDateModified(new Date());
            if(contentEntity.getStyles() == null)
                contentEntity.setStyles(new HashSet<StyleEntity>());
            for(Style style : content.getStyles()) {
                boolean found = false;
                for(StyleEntity styleEntity : contentEntity.getStyles()) {
                    if(style.getName().equals(styleEntity.getName())
                            && style.getNamespace().getFullName().equals(styleEntity.getNamespace().getName()))
                    {
                        styleEntity.setStyle(style.getStyle());
                        found = true;
                    }
                }
                if(!found) {
                    StyleEntity styleEntity = makeStyleEntity(style);
                    contentEntity.getStyles().add(styleEntity);
                }
            }
            Iterator<StyleEntity> it = contentEntity.getStyles().iterator();
            while(it.hasNext()) {
                StyleEntity styleEntity = it.next();
                boolean found = false;
                for(Style style : content.getStyles()) {
                    if(style.getName().equals(styleEntity.getName())
                            && style.getNamespace().getFullName().equals(styleEntity.getNamespace().getName()))
                    {
                        found = true;
                    }
                }
                if(!found)
                    it.remove();
            }
        }

        makeGroupPermissionsEntity(contentEntity, content.getGroupPermissionsList());

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.persist(contentEntity);
        t.commit();
    }

    private List<StyleEntity> makeStyleEntities(List<Style> styles) {
        List<StyleEntity> styleEntities = new Vector<StyleEntity>();

        for(Style style : styles)
            styleEntities.add(makeStyleEntity(style));

        return styleEntities;
    }


    private StyleEntity makeStyleEntity(Style style) {
        StyleEntity styleEntity = new StyleEntity();

        Object result = em.createQuery("select s from style s where s.namespace.name = ?1 and s.name = ?2 ").
                setParameter(1, style.getNamespace().getFullName()).setParameter(2, style.getName()).getSingleResult();
        if(result != null)
            styleEntity = (StyleEntity) result;

        styleEntity.setName(style.getName());
        styleEntity.setStyle(style.getStyle());
        makeGroupPermissionsEntity(styleEntity, style.getGroupPermissionsList());

        return styleEntity;
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

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.remove(contentEntity);
        t.commit();
    }

    @Override
    public List<Script> loadAllScripts() {
        return null;
    }

    @Override
    public List<Script> loadScript(Namespace namespace) {
        return null;
    }

    @Override
    public Script loadScript(Namespace namespace, String name) {
        return null;
    }

    @Override
    public void saveScript(Script script) {
    }

    @Override
    public void deleteScript(Script script) {
    }

    @Override
    public List<Style> loadAllStyles() {
        List<Style> styleList = new Vector<Style>();
        List styleEntities = em.createQuery("select c from style c").getResultList();

        for(Object o : styleEntities) {
            StyleEntity styleEntity = (StyleEntity) o;
            styleList.add(makeStyle(styleEntity));
        }

        return styleList;
    }

    @Override
    public List<Style> loadStyle(Namespace namespace) {
        List<Style> styleList = new Vector<Style>();

        List styleEntities = em.createQuery("select c from style c where c.namespace.name = ?1").
                setParameter(1, namespace.getFullName()).getResultList();

        for(Object o : styleEntities) {
            StyleEntity styleEntity = (StyleEntity) o;
            styleList.add(makeStyle(styleEntity));
        }

        return styleList;
    }

    @Override
    public Style loadStyle(Namespace namespace, String name) {
        Object result;

        try {
            result = em.createQuery("select c from style c where c.namespace.name = ?1 and c.name = ?2").
                    setParameter(1, namespace.getFullName()).setParameter(2, name).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

        StyleEntity styleEntity = (StyleEntity) result;

        return makeStyle(styleEntity);
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
            result = em.createQuery("select c from style c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            result = null;
        }

        StyleEntity styleEntity;
        if(result == null) {
            styleEntity = new StyleEntity();
            styleEntity.setName(style.getName());
            styleEntity.setStyle(style.getStyle());
            styleEntity.setNamespace(ne);
        } else {
            styleEntity = (StyleEntity) result;
            styleEntity.setStyle(style.getStyle());
        }

        makeGroupPermissionsEntity(styleEntity, style.getGroupPermissionsList());

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
            result = em.createQuery("select c from style c where c.namespace.id = ?1 and c.name = ?2").
                    setParameter(1, ne.getId()).setParameter(2, style.getName()).getSingleResult();
        } catch(NoResultException e) {
            return;
        }

        EntityTransaction t = em.getTransaction();
        t.begin();
        em.remove(result);
        t.commit();
    }

    @Override
    public void saveGroup(GroupEntity group) {
        em.getTransaction().begin();
        em.persist(group);
        em.getTransaction().commit();
    }

    @Override
    public List<Namespace> loadChildNamespaces(Namespace namespace) {
        List<Namespace> namespaces = new Vector<Namespace>();
        Query query = em.createQuery(
                "select n from namespace n where n.parentId in (select nn.id from namespace nn where nn.name = ?1)");
        query.setParameter(1, namespace.getFullName());
        List results = query.getResultList();
        if(results == null || results.isEmpty())
            return namespaces;

        for(Object result : results) {
            NamespaceEntity namespaceEntity = (NamespaceEntity) result;
            Namespace ns = Namespace.createFromString(namespaceEntity.getName());
            ns.setGroupPermissionsList(makeGroupPermissions(namespaceEntity.getGroupPermissions()));
            namespaces.add(ns);
        }
        return namespaces;
    }

    @SuppressWarnings({"unchecked"})
    public List<String> getAllGroups() {
        return em.createQuery("select g.groupname from groups g").getResultList();
    }
}
