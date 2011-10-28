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

package net.tralfamadore.admin;

import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.component.Editor;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import mockit.*;
import net.integration.GuiceTestRunner;
import net.integration.MockFacesContext;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.persistence.JpaEntityManagerProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import java.io.File;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * User: billreh
 * Date: 10/23/11
 * Time: 12:33 AM
 */
@RunWith(AdminControllerTest.GuiceAdminIntegration.class)
public class AdminControllerTest {

    @Inject
    AdminController adminController;

    @Mocked
    FacesContext facesContext;
    @Mocked
    PartialViewContext partialViewContext;
    @Mocked
    TheTree theTree;
    @Mocked
    ContentHolder contentHolder;
    @Mocked
    PageContent pageContent;
    @Mocked
    ContentManager contentManager;
    @Mocked @Cascading
    ActionEvent e;
    @Mocked DataTable groupsDataTable;


    @Test
    public void testLoadNamespace() throws Exception {
        final TreeNode newContent = new DefaultTreeNode();
        newContent.setSelected(false);
        final TreeNode selectedNode = new DefaultTreeNode();
        selectedNode.setSelected(true);
        final List<Content> contentList = new ArrayList<Content>();
        contentList.add(new Content());
        new NonStrictExpectations() {{
            facesContext.getPartialViewContext(); result = partialViewContext;
            partialViewContext.isAjaxRequest(); result = false;
            theTree.getContentHolder(); result = contentHolder;
            contentHolder.find(new ContentKey(null, "namespace", "namespace")); result = newContent;
            theTree.getSelectedNode(); result = selectedNode;
            pageContent.getNamespace(); result = Namespace.createFromString("net.tralfamadore");
            pageContent.getNamespaceContents(); result = new Vector<BaseContent>();
            theTree.getContentManager(); result = contentManager;
            contentManager.loadContent((Namespace)any); result = contentList;
        }};
        pageContent.setNamespaceContents(new ArrayList<BaseContent>());
        adminController.setIncomingNamespace("namespace");
        adminController.pageContent = new PageContent();
        adminController.pageContent.setTheContent(Namespace.createFromString("net.tralfamadore"));
        adminController.loadNamespace();

        assertTrue(newContent.isSelected());
        assertFalse(selectedNode.isSelected());

        assertEquals(1, pageContent.getNamespaceContents().size());
    }

    @Test
    public void testLoadTopLevelNamespace() throws Exception {
        final TreeNode newContent = new DefaultTreeNode();
        newContent.setSelected(false);
        final TreeNode selectedNode = new DefaultTreeNode();
        selectedNode.setSelected(true);
        final List<Content> contentList = new ArrayList<Content>();
        contentList.add(new Content());
        new NonStrictExpectations() {{
            facesContext.getPartialViewContext(); result = partialViewContext;
            partialViewContext.isAjaxRequest(); result = false;
            theTree.getContentHolder(); result = contentHolder;
            contentHolder.find(new ContentKey(null, "namespace", "namespace")); result = newContent;
            theTree.getSelectedNode(); result = selectedNode;
            pageContent.getNamespace(); result = Namespace.createFromString("net");
            pageContent.getNamespaceContents(); result = new Vector<BaseContent>();
            theTree.getContentManager(); result = contentManager;
            contentManager.loadContent((Namespace) any); result = contentList;
            contentManager.loadChildNamespaces((Namespace) any); result = Collections.emptyList();
        }};
        pageContent.setNamespaceContents(new ArrayList<BaseContent>());
        adminController.setIncomingNamespace("namespace");
        adminController.pageContent = new PageContent();
        adminController.pageContent.setTheContent(Namespace.createFromString("net"));
        adminController.loadNamespace();

        assertTrue(newContent.isSelected());
        assertFalse(selectedNode.isSelected());

        assertEquals(2, pageContent.getNamespaceContents().size());
    }

    @Test
    public void testLoadContent() throws Exception {
        final TreeNode newContent = new DefaultTreeNode();
        newContent.setSelected(false);
        final TreeNode selectedNode = new DefaultTreeNode();
        selectedNode.setSelected(true);
        new NonStrictExpectations() {{
            facesContext.getPartialViewContext(); result = partialViewContext;
            partialViewContext.isAjaxRequest(); result = false;
            theTree.getContentHolder(); result = contentHolder;
            contentHolder.find(new ContentKey("content", "namespace", "content")); result = newContent;
            theTree.getSelectedNode(); result = selectedNode;
        }};
        adminController.setIncomingContentName("content");
        adminController.setIncomingNamespace("namespace");
        adminController.loadContent();
        assertTrue(newContent.isSelected());
        assertFalse(selectedNode.isSelected());
    }

    @Test
    public void testLoadNonExistentContent() throws Exception {
        new NonStrictExpectations() {{
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getPartialViewContext(); result = partialViewContext;
            partialViewContext.isAjaxRequest(); result = false;
            theTree.getContentHolder(); result = contentHolder;
        }};

        adminController.setIncomingContentName("content");
        adminController.setIncomingNamespace("namespace");
        adminController.loadContent();

        new Verifications() {{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, anyString, null));
                times = 1;
        }};
    }

    @Test
    public void testLoadStyle() throws Exception {
        final TreeNode newContent = new DefaultTreeNode();
        newContent.setSelected(false);
        final TreeNode selectedNode = new DefaultTreeNode();
        selectedNode.setSelected(true);
        new NonStrictExpectations() {{
            facesContext.getPartialViewContext(); result = partialViewContext;
            partialViewContext.isAjaxRequest(); result = false;
            theTree.getContentHolder(); result = contentHolder;
            contentHolder.find(new ContentKey("style", "namespace", "style")); result = newContent;
            theTree.getSelectedNode(); result = selectedNode;
        }};
        adminController.setIncomingContentName("style");
        adminController.setIncomingNamespace("namespace");
        adminController.loadStyle();
        assertTrue(newContent.isSelected());
        assertFalse(selectedNode.isSelected());
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testAddNewTopLevelNamespace() throws Exception {
        new Expectations() {
            Namespace namespaceToAdd;
            {
                pageContent.setNamespaceToAdd((Namespace)any); times = 1;
                pageContent.getNamespaceToAdd(); result = namespaceToAdd; times = 1;
                namespaceToAdd.setGroupPermissionsList((List<GroupPermissions>) any); times = 1;
                pageContent.setAddingNamespace(true); times = 1;
                pageContent.setAddingTopLevelNamespace(true); times = 1;
            }
        };

        adminController.addNewTopLevelNamespace();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testAddNewNamespace() throws Exception {
        new Expectations() {
            Namespace namespaceToAdd;
            {
                pageContent.getNamespace(); times = 1;
                pageContent.setNamespaceToAdd((Namespace) any); times = 1;
                pageContent.getNamespaceToAdd(); result = namespaceToAdd; times = 1;
                namespaceToAdd.setGroupPermissionsList((List<GroupPermissions>) any); times = 1;
                pageContent.setAddingNamespace(true); times = 1;
            }
        };

        adminController.addNewNamespace();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testAddNewContent() throws Exception {
        new Expectations() {
            Content contentToAdd;
            {
                pageContent.setAddingContent(true); times = 1;
                pageContent.setContentToAdd(new Content()); times = 1;
                pageContent.getContentToAdd(); result = contentToAdd; times = 1;
                contentToAdd.setGroupPermissionsList((List<GroupPermissions>) any); times = 1;
            }
        };

        adminController.addNewContent();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testAddNewStyle() throws Exception {
        new Expectations() {
            Style styleToAdd;
            {
                pageContent.setAddingStyle(true); times = 1;
                pageContent.setStyleToAdd(new Style()); times = 1;
                pageContent.getStyleToAdd(); result = styleToAdd; times = 1;
                styleToAdd.setGroupPermissionsList((List<GroupPermissions>) any); times = 1;
            }
        };

        adminController.addNewStyle();
    }

    @Test
    public void testAddGroup() throws Exception {
        new Expectations() {
            @Mocked({"saveBaseContent()", "saveBaseContent(BaseContent)"})
            AdminController adminController;
            BaseContent content;
            List<GroupPermissions> groupPermissionsList;
            {
                pageContent.getBaseContent(); result = content; times = 1;
                content.getGroupPermissionsList(); result = groupPermissionsList; times = 1;
                groupPermissionsList.add(new GroupPermissions(anyString, true, false, false, false)); times = 1;
            }
        };
        adminController.addGroup();
    }

    @Test
    public void testAddGroupToNewContent() {
        new Expectations() {{
            pageContent.addGroupToNewContent(new GroupPermissions(anyString, true, false, false, false)); times = 1;
        }};

        adminController.addGroupToNewContent();
    }

    @Test
    public void testAddContent() throws Exception {
        new Expectations() {
            Content contentToAdd;
            List<BaseContent> namespaceContents;
            {
                pageContent.getContentToAdd(); result = contentToAdd; times = 1;
                pageContent.getNamespace(); result = new Namespace(); times = 1;
                theTree.getContentHolder(); result = contentHolder; times = 1;
                contentHolder.add(contentToAdd); times = 1;
                theTree.getContentManager(); result = contentManager; times = 1;
                contentManager.saveContent(contentToAdd); times = 1;
                pageContent.setAddingContent(false); times = 1;
                pageContent.getNamespaceContents(); result = namespaceContents; times = 1;
                namespaceContents.add(contentToAdd); times = 1;
                theTree.createTreeModel(); times = 1;
                FacesContext.getCurrentInstance(); result = facesContext; times = 1;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, anyString, anyString));
            }
        };

        adminController.addContent();
    }

    @Test
    public void testAddStyle() throws Exception {
        new Expectations() {
            Style styleToAdd;
            List<BaseContent> namespaceContents;
            {
                pageContent.getStyleToAdd(); result = styleToAdd; times = 1;
                pageContent.getNamespace(); times = 1;
                theTree.getContentHolder(); result = contentHolder; times = 1;
                contentHolder.add(styleToAdd); times = 1;
                theTree.getContentManager(); result = contentManager; times = 1;
                contentManager.saveStyle(styleToAdd); times = 1;
                pageContent.setAddingStyle(false); times = 1;
                pageContent.getNamespaceContents(); result = namespaceContents; times = 1;
                namespaceContents.add(styleToAdd); times = 1;
                theTree.createTreeModel(); times = 1;
                FacesContext.getCurrentInstance(); result = facesContext; times = 1;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, anyString, anyString));
            }
        };

        adminController.addStyle();
    }

    @Test
    public void testRemoveGroup() throws Exception {
        final BaseContent bc = new Content();
        bc.getGroupPermissionsList().add(new GroupPermissions("testGroup", true, true, true, true));

        new Expectations() {
            @Mocked({"saveBaseContent()", "saveBaseContent(BaseContent)"})
            AdminController adminController;
            Map<String,Object> attributes;
            UIComponent c;
            RequestContext requestContext;
            {
                e.getComponent(); result = c; c.getAttributes(); result = attributes;
                attributes.get("group"); result = "testGroup"; times = 1;
                pageContent.getBaseContent(); result = bc; times = 1;
                groupsDataTable.getClientId((FacesContext)any); result = "testClientId"; times = 1;
                RequestContext.getCurrentInstance(); result = requestContext; times = 1;
                requestContext.addPartialUpdateTarget("testClientId"); times = 1;
            }
        };

        adminController.setGroupsDataTable(groupsDataTable);
        adminController.removeGroup(e);
    }

    @Test
    public void testRemoveGroupForNewContent() throws Exception {
        final BaseContent bc = new Content();
        bc.getGroupPermissionsList().add(new GroupPermissions("testGroup", true, true, true, true));

        new Expectations() {
            Map<String,Object> attributes;
            UIComponent c;
            RequestContext requestContext;
            DataTable dialogGroupsTable;
            {
                e.getComponent(); result = c; c.getAttributes(); result = attributes;
                attributes.get("group"); result = "testGroup"; times = 1;
                pageContent.getBaseContentToAdd(); result = bc; times = 1;
                pageContent.getBaseContent();
                groupsDataTable.getClientId((FacesContext) any); result = "testClientId"; times = 1;
                RequestContext.getCurrentInstance(); result = requestContext; times = 1;
                requestContext.addPartialUpdateTarget("testClientId"); times = 1;
            }
        };

        adminController.setDialogGroupsDataTable(groupsDataTable);
        adminController.removeGroupForNewContent(e);
    }

    @Test
    public void testPermissionChanged() {
        new MockUp<AdminController>() {
            @Mock(invocations = 1)
            void saveBaseContent() {
            }
        };
        adminController.permissionChanged();
    }

    @Test
    public void testSaveNewNamespace() throws Exception {
        new Expectations() {
            Namespace newNamespace;
            List<BaseContent> namespaceContents;
            {
                pageContent.getNamespaceToAdd(); times = 1; result = newNamespace;
                newNamespace.setNodeName(anyString); times = 1;
                theTree.getContentManager(); result = contentManager;
                pageContent.getNamespaceToAdd(); times = 1;
                contentManager.saveNamespace((Namespace) any);
                pageContent.getNamespaceContents(); times = 1; result = namespaceContents;
                pageContent.getNamespaceToAdd(); times = 1;
                namespaceContents.add((BaseContent) any);
                pageContent.setAddingNamespace(false); times = 1;
                pageContent.setAddingTopLevelNamespace(false); times = 1;
                theTree.createTreeModel(); times = 1;
            }
        };
        adminController.saveNewNamespace();
    }

    @Test
    public void testSaveContent() throws Exception {
        new Expectations() {
            Content content;
            {
                pageContent.getContent(); times = 1; result = content;
                theTree.getContentManager(); times = 1; result = contentManager;
                contentManager.saveContent(content); times = 1;
                FacesContext.getCurrentInstance(); result = facesContext; times = 1;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, anyString, anyString));
            }
        };
        adminController.saveContent();
    }

    @Mocked
    DragDropEvent dragDropEvent;

    @Mocked
    UIComponent genericComponent;

    @Mocked
    Editor editor;

    @Test
    public void testStyleDrop() throws Exception {
        Deencapsulation.setField(adminController, "facesContext", facesContext);
        Deencapsulation.setField(adminController, "stylePanel", genericComponent);
        Deencapsulation.setField(adminController, "editor", editor);
        Deencapsulation.setField(adminController, "dropper", genericComponent);
        Deencapsulation.setField(adminController, "theTreeComponent", genericComponent);
        new Expectations() {
            @Mocked({"makeContentCss()"})
            AdminController adminController;
            Content content;
            Style style;
            List<Style> styles;
            Map<String,String> paramMap;
            ExternalContext ec;
            RequestContext requestContext;
            {
                facesContext.getExternalContext(); result = ec;
                ec.getRequestParameterMap(); result = paramMap;
                paramMap.get("styleNamespace"); result = "testNamespace";
                paramMap.get("styleName"); result = "testName";
                theTree.getContentManager(); result = contentManager;
                contentManager.loadStyle(new Namespace("testNamespace"), "testName"); result = style;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.contains(style); result = false;
                content.getStyles(); result = styles;
                styles.add(style);
                theTree.getContentManager(); result = contentManager;
                contentManager.saveContent(content);
                RequestContext.getCurrentInstance(); result = requestContext;
                dragDropEvent.getDropId(); result = "testDropId";
                requestContext.addPartialUpdateTarget("testDropId");
                genericComponent.getClientId((FacesContext) any); result = "stylePanelId";
                requestContext.addPartialUpdateTarget("stylePanelId");
                editor.getClientId((FacesContext) any); result = "editorId";
                requestContext.addPartialUpdateTarget("editorId");
                genericComponent.getClientId((FacesContext) any); result = "dropperId";
                requestContext.addPartialUpdateTarget("dropperId");
                genericComponent.getClientId((FacesContext) any); result = "treeId";
                requestContext.addPartialUpdateTarget("treeId");
            }
        };
        adminController.styleDrop(dragDropEvent);
    }

    @Test
    public void testStyleDropStyleAlreadyThere() throws Exception {
        Deencapsulation.setField(adminController, "facesContext", facesContext);
        Deencapsulation.setField(adminController, "stylePanel", genericComponent);
        Deencapsulation.setField(adminController, "editor", editor);
        Deencapsulation.setField(adminController, "dropper", genericComponent);
        Deencapsulation.setField(adminController, "theTreeComponent", genericComponent);
        new Expectations() {
            @Mocked({"makeContentCss()"})
            AdminController adminController;
            Content content;
            Style style;
            List<Style> styles;
            Map<String,String> paramMap;
            ExternalContext ec;
            RequestContext requestContext;
            {
                facesContext.getExternalContext(); result = ec;
                ec.getRequestParameterMap(); result = paramMap;
                paramMap.get("styleNamespace"); result = "testNamespace";
                paramMap.get("styleName"); result = "testName";
                theTree.getContentManager(); result = contentManager;
                contentManager.loadStyle(new Namespace("testNamespace"), "testName"); result = style;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.contains(style); result = true;
            }
        };
        adminController.styleDrop(dragDropEvent);
    }

    @Test
    public void testSaveStyle() throws Exception {
        new Expectations() {
            Style style;
            {
                pageContent.getStyle(); times = 1; result = style;
                theTree.getContentManager(); times = 1; result = contentManager;
                contentManager.saveStyle(style); times = 1;
                FacesContext.getCurrentInstance(); result = facesContext; times = 1;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, anyString, anyString));
            }
        };
        adminController.saveStyle();
    }

    @Test
    public void testRemoveStyle() throws Exception {
        Deencapsulation.setField(adminController, "facesContext", facesContext);
        Deencapsulation.setField(adminController, "stylePanel", genericComponent);
        Deencapsulation.setField(adminController, "editor", editor);
        Deencapsulation.setField(adminController, "dropper", genericComponent);
        Deencapsulation.setField(adminController, "theTreeComponent", genericComponent);
        new Expectations() {
            @Mocked({"makeContentCss()"})
            AdminController adminController;
            UIComponent component;
            Map<String,Object> attributes;
            TreeNode styleNode;
            Style style;
            Content content;
            List<Style> styles;
            RequestContext requestContext;
            {
                e.getSource(); result = component;
                component.getAttributes(); result = attributes;
                attributes.get("namespace"); result = "testNamespace";
                component.getAttributes(); result = attributes;
                attributes.get("styleName"); result = "testStyle";
                theTree.getContentHolder(); result = contentHolder;
                contentHolder.find(new ContentKey("testStyle", "testNamespace", "style")); result = styleNode;
                styleNode.getData(); result = style;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.contains(style); result = true;

                content.getStyles(); result = styles;
                styles.remove(style);
                theTree.getContentManager(); result = contentManager;
                contentManager.saveContent(content);

                RequestContext.getCurrentInstance(); result = requestContext;
                editor.getClientId((FacesContext) any); result = "editorId";
                requestContext.addPartialUpdateTarget("editorId");
                genericComponent.getClientId((FacesContext) any); result = "stylePanelId";
                requestContext.addPartialUpdateTarget("stylePanelId");
                genericComponent.getClientId((FacesContext) any); result = "dropperId";
                requestContext.addPartialUpdateTarget("dropperId");
                genericComponent.getClientId((FacesContext) any); result = "treeId";
                requestContext.addPartialUpdateTarget("treeId");
            }
        };

        adminController.removeStyle(e);
    }

    @Test
    public void testRemoveStyleAlreadyGone() throws Exception {
        Deencapsulation.setField(adminController, "facesContext", facesContext);
        Deencapsulation.setField(adminController, "stylePanel", genericComponent);
        Deencapsulation.setField(adminController, "editor", editor);
        Deencapsulation.setField(adminController, "dropper", genericComponent);
        Deencapsulation.setField(adminController, "theTreeComponent", genericComponent);
        new Expectations() {
            @Mocked({"makeContentCss()"})
            AdminController adminController;
            UIComponent component;
            Map<String,Object> attributes;
            TreeNode styleNode;
            Style style;
            Content content;
            List<Style> styles;
            RequestContext requestContext;
            {
                e.getSource(); result = component;
                component.getAttributes(); result = attributes;
                attributes.get("namespace"); result = "testNamespace";
                component.getAttributes(); result = attributes;
                attributes.get("styleName"); result = "testStyle";
                theTree.getContentHolder(); result = contentHolder;
                contentHolder.find(new ContentKey("testStyle", "testNamespace", "style")); result = styleNode;
                styleNode.getData(); result = style;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.contains(style); result = false;

                RequestContext.getCurrentInstance(); result = requestContext;
                editor.getClientId((FacesContext) any); result = "editorId";
                requestContext.addPartialUpdateTarget("editorId");
                genericComponent.getClientId((FacesContext) any); result = "stylePanelId";
                requestContext.addPartialUpdateTarget("stylePanelId");
                genericComponent.getClientId((FacesContext) any); result = "dropperId";
                requestContext.addPartialUpdateTarget("dropperId");
                genericComponent.getClientId((FacesContext) any); result = "treeId";
                requestContext.addPartialUpdateTarget("treeId");
            }
        };

        adminController.removeStyle(e);
    }

    @Test
    public void testRemoveBaseContentEmptyNamespace() throws Exception {
        new Expectations() {
            Namespace namespace;
            List<BaseContent> namespaceContents;
            {
                pageContent.getContentToRemove(); result = namespace;
                theTree.getContentManager(); result = contentManager; times = 2;
                namespace.getNamespace(); result = namespace;
                contentManager.loadChildNamespaces(namespace); result = Collections.emptyList();
                contentManager.loadStyle(namespace); result = Collections.emptyList();
                contentManager.loadContent(namespace); result = Collections.emptyList();
                contentManager.deleteNamespace(namespace);
                pageContent.getNamespaceContents(); result = namespaceContents;
                namespaceContents.remove(namespace);
                theTree.createTreeModel();
            }
        };

        adminController.removeBaseContent();
    }

    @Test(expected = ValidatorException.class)
    public void testRemoveBaseContentNonEmptyNamespace() throws Exception {
        final List<Namespace> namespaceList = new Vector<Namespace>();
        namespaceList.add(new Namespace());
        new Expectations() {
            Namespace namespace;
            List<BaseContent> namespaceContents;
            {
                pageContent.getContentToRemove(); result = namespace;
                theTree.getContentManager(); result = contentManager; times = 2;
                namespace.getNamespace(); result = namespace;
                contentManager.loadChildNamespaces(namespace); result = namespaceList;
                FacesContext.getCurrentInstance(); result = facesContext;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, anyString, null));
            }
        };

        adminController.removeBaseContent();
    }

    @Test
    public void testRemoveBaseContentContent() throws Exception {
        new Expectations() {
            Content content;
            List<BaseContent> namespaceContents;
            {
                pageContent.getContentToRemove(); result = content;
                theTree.getContentManager(); result = contentManager;
                contentManager.deleteContent(content);
                pageContent.getNamespaceContents(); result = namespaceContents;
                namespaceContents.remove(content);
                theTree.createTreeModel();
            }
        };

        adminController.removeBaseContent();
    }

    @Test
    public void testRemoveBaseContentStyle() throws Exception {
        new Expectations() {
            Style style;
            List<BaseContent> namespaceContents;
            {
                pageContent.getContentToRemove(); result = style;
                theTree.getContentManager(); result = contentManager;
                contentManager.deleteStyle(style);
                pageContent.getNamespaceContents(); result = namespaceContents;
                namespaceContents.remove(style);
                theTree.createTreeModel();
            }
        };

        adminController.removeBaseContent();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void testCreateEmbeddedDb() throws Exception {
        new Expectations() {
            File file;
            CmfContext cmfContext;
            JpaEntityManagerProvider entityManagerProvider;
            {
                new File(anyString);
                file.getParentFile(); result = file;
                file.exists(); result = true;
                CmfContext.getInstance(); result = cmfContext;
                cmfContext.getEntityManagerProvider(); result = entityManagerProvider;
                entityManagerProvider.createEmbeddedDb((Properties) any);
                theTree.createTreeModel();
            }
        };
        Deencapsulation.setField(adminController, "embeddedDbNeedsConfig", true);
        adminController.createEmbeddedDb();
        assertFalse(Deencapsulation.<Boolean>getField(adminController, "embeddedDbNeedsConfig"));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test(expected = FacesException.class)
    public void testCreateEmbeddedDbFail() throws Exception {
        new Expectations() {
            File file;
            {
                new File(anyString);
                file.getParentFile(); result = file;
                file.exists(); result = false;
                FacesContext.getCurrentInstance(); result = facesContext;
                file.getParentFile(); result = file;
                file.getAbsolutePath(); result = "filePath";
                facesContext.addMessage(null, (FacesMessage) any);
            }
        };
        adminController.createEmbeddedDb();
    }

    @Test
    public void testSaveBaseContentContent() throws Exception {
        new NonStrictExpectations() {
            Content content;
            {
                pageContent.getBaseContent(); result = content;
                theTree.getContentManager(); result = contentManager;
            }
        };

        adminController.permissionChanged();

        new Verifications() {{
            contentManager.saveContent((Content) any); times = 1;
        }};
    }

    @Test
    public void testSaveBaseContentNamespace() throws Exception {
        new NonStrictExpectations() {
            Namespace namespace;
            {
                pageContent.getBaseContent(); result = namespace;
                theTree.getContentManager(); result = contentManager;
            }
        };

        adminController.permissionChanged();

        new Verifications() {{
            contentManager.saveNamespace((Namespace) any); times = 1;
        }};
    }

    @Test
    public void testSaveBaseContentStyle() throws Exception {
        new NonStrictExpectations() {
            Style style;
            {
                pageContent.getBaseContent(); result = style;
                theTree.getContentManager(); result = contentManager;
            }
        };

        adminController.permissionChanged();

        new Verifications() {{
            contentManager.saveStyle((Style) any); times = 1;
        }};
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testFetchNamespaceContents() throws Exception {
        final TreeNode newContent = new DefaultTreeNode();
        newContent.setSelected(false);
        final TreeNode selectedNode = new DefaultTreeNode();
        selectedNode.setSelected(true);
        final List<Content> contentList = new ArrayList<Content>();
        contentList.add(new Content());
        new NonStrictExpectations() {
            Namespace namespace;
            List<BaseContent> namespaceContents;
            {
                facesContext.getPartialViewContext(); result = partialViewContext;
                partialViewContext.isAjaxRequest(); result = false;
                theTree.getContentHolder(); result = contentHolder;
                contentHolder.find(new ContentKey(null, "namespace", "namespace")); result = newContent;
                newContent.getData(); result = namespace;
                selectedNode.getData(); result = namespace;
                theTree.getSelectedNode(); result = selectedNode;
                pageContent.getNamespaceContents(); result = new Vector<BaseContent>();
                theTree.getContentManager(); result = contentManager;
                contentManager.loadContent((Namespace)any); result = contentList;

                pageContent.getNamespace(); result = namespace;
                pageContent.setNamespaceContents((List<BaseContent>) any);
                pageContent.getNamespaceContents(); result = namespaceContents;
                theTree.getContentManager(); result = contentManager;
                contentManager.loadChildNamespaces((Namespace) any); result = Collections.emptyList();
                namespace.getParent(); result = null;
                namespaceContents.add(namespace);
            }
        };
        pageContent.setNamespaceContents(new ArrayList<BaseContent>());
        adminController.setIncomingNamespace("namespace");
        adminController.pageContent = new PageContent();
        adminController.pageContent.setTheContent(Namespace.createFromString("net.tralfamadore"));
        adminController.loadNamespace();
    }

    @Test
    public void testMakeContentCss() throws Exception {
        Deencapsulation.setField(adminController, "facesContext", facesContext);
        Deencapsulation.setField(adminController, "stylePanel", genericComponent);
        Deencapsulation.setField(adminController, "editor", editor);
        Deencapsulation.setField(adminController, "dropper", genericComponent);
        Deencapsulation.setField(adminController, "theTreeComponent", genericComponent);
        final List<Style> styleList = new Vector<Style>();
        Style style = new Style();
        style.setStyle("testing");
        styleList.add(style);
        new Expectations() {
            Content content;
            Style style;
            List<Style> styles;
            Map<String,String> paramMap;
            ExternalContext ec;
            RequestContext requestContext;
            {
                facesContext.getExternalContext(); result = ec;
                ec.getRequestParameterMap(); result = paramMap;
                paramMap.get("styleNamespace"); result = "testNamespace";
                paramMap.get("styleName"); result = "testName";
                theTree.getContentManager(); result = contentManager;
                contentManager.loadStyle(new Namespace("testNamespace"), "testName"); result = style;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.contains(style); result = true;

                pageContent.getContent(); result = content; times = 2;
                content.getStyles(); result = styles;
                pageContent.getContent(); result = content;
                content.getStyles(); result = styles;
                styles.iterator(); result = styleList.iterator();
                style.getStyle(); result = "testStyle";
            }
        };
        adminController.styleDrop(dragDropEvent);
        assertEquals("testStyle", adminController.getContentCss());
    }

    @Test
    public void testNewDefaultGroupPermissions() throws Exception {
        final String group = "testGroup";
        GroupPermissions groupPermissions = new GroupPermissions(group, true, true, true, true);

        new NonStrictExpectations() {
            CmfContext cmfContext;
            {
                CmfContext.getInstance(); result = cmfContext;
                cmfContext.getCurrentUser(); result = group;
            }
        };

        List<GroupPermissions> ret = AdminController.newDefaultGroupPermissions();
        assertEquals(1, ret.size());
        assertEquals(groupPermissions, ret.get(0));
    }

    public static class AdminTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(new TypeLiteral<ContentManager>(){}).toProvider(AdminTestModule.ContentManagerMockProvider.class);
            bind(new TypeLiteral<TheTree>(){}).toProvider(TheTreeProvider.class);
            bind(new TypeLiteral<PageContent>(){}).toProvider(PageContentMockProvider.class);
            bind(FacesContext.class).to(MockFacesContext.class).asEagerSingleton();
        }

        public static class ContentManagerMockProvider implements Provider<ContentManager> {
            @Override
            public ContentManager get() {
                return TestContentManager.getInstance();
            }
        }

        public static class TheTreeProvider implements Provider<TheTree> {
            @Override
            public TheTree get() {
                return new TheTree();
            }
        }

        public static class ToolbarMockProvider implements Provider<Toolbar> {
            @Override
            public Toolbar get() {
                return Toolbar.TOOLBAR_BASIC;
            }
        }

        public static class PageContentMockProvider implements Provider<PageContent> {
            static PageContent pageContent = new PageContent();
            @Override
            public PageContent get() {
                return pageContent;
            }
        }
    }

    public static class GuiceAdminIntegration extends GuiceTestRunner {
        public GuiceAdminIntegration(final Class<?> classToRun) throws InitializationError {
            super(classToRun, new AdminControllerTest.AdminTestModule());
        }
    }
}

