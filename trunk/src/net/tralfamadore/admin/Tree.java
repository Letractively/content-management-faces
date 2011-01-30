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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.tralfamadore.admin;

import net.tralfamadore.cmf.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 4:14 AM
 *
 * This managed bean is a backing bean for the admin page and is also used by the {@link Admin} managed bean.  It
 * holds the model for the tree (the list of namespaces, content, styles, etc).
 */
@ManagedBean
@SessionScoped
public class Tree implements Serializable {
    /** the content manager */
    private final ContentManager contentManager = TestContentManager.getInstance();

    /** the tree model, representing all of our content */
    private TreeModel treeModel = new TreeModel();

    /** the currently selected namespace */
    private Namespace selectedNamespace;

    /** namespace to be added */
    private String newNamespace;

    /** name of content to be added */
    private String newContentName;

    /** name of style to be added */
    private String newStyleName;


    public Tree() {
        createTreeModel();
    }


    /* getters and setters */

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(TreeModel treeModel) {
        this.treeModel = treeModel;
    }

    public String getNewNamespace() {
        return newNamespace;
    }

    public void setNewNamespace(String newNamespace) {
        this.newNamespace = newNamespace;
    }

    public String getNewContentName() {
        return newContentName;
    }

    public void setNewContentName(String newContentName) {
        this.newContentName = newContentName;
    }

    public String getNewStyleName() {
        return newStyleName;
    }

    public void setNewStyleName(String newStyleName) {
        this.newStyleName = newStyleName;
    }

    public Namespace getSelectedNamespace() {
        return selectedNamespace;
    }

    public void setSelectedNamespace(Namespace selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
    }

    /**
     * Virtual getter to return the namespace with a '.' at the end of it.
     *
     * @return The namespace with a '.' at the end of it.
     */
    public String getSelectedNamespaceString() {
        return selectedNamespace == null ? null : selectedNamespace.getFullName() + '.';
    }

    /**
     * Creates the tree model for the tree component from information gotten from the content manager.  Used internally
     * and by other components to "re-render" the model when it is updated.
     */
    public void createTreeModel() {
        List<Namespace> namespaces = contentManager.loadAllNamespaces();
        treeModel = new TreeModel();
        for(Namespace namespace : namespaces) {
            treeModel.addNode(namespace);
        }

        List<Content> contentList = contentManager.loadAllContent();
        for(Content content : contentList) {
            treeModel.addNode(content);
        }

        List<Style> styles = contentManager.loadAllStyles();
        for(Style style : styles) {
            treeModel.addNode(style);
        }

        List<Script> scripts = contentManager.loadAllScripts();
        for(Script script : scripts) {
            treeModel.addNode(script);
        }
    }
}
