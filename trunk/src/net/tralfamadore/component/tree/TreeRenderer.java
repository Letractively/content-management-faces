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

package net.tralfamadore.component.tree;

import net.tralfamadore.admin.TreeModel;
import net.tralfamadore.admin.TreeNode;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 2:55 PM
 */
@FacesRenderer(rendererType = "Tree", componentFamily = "javax.faces.Output")
public class TreeRenderer extends Renderer {
    private Map<String,UITreeNode> nodes;

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // we don't do it this way
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Tree tree = (Tree) component;
        TreeModel treeModel = (TreeModel) tree.getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.append("<div id=\"theTree\"><ul>");
        for(TreeNode n : treeModel.root().getChildren()) {
            context.getExternalContext().getRequestMap().put(tree.getVar(), n);
            renderHtml(n, context, component);
            context.getExternalContext().getRequestMap().remove(tree.getVar());
        }
        responseWriter.append("</ul></div>");
        responseWriter.append("<script type=\"text/javascript\">");
        responseWriter.append("(function() {\n" +
                "                    var treeInit = function() {\n" +
                "                        tree1 = new YAHOO.widget.TreeView(\"theTree\");\n" +
                "                        tree1.singleNodeHighlight = true;\n" +
                "                        tree1.subscribe('clickEvent',handleNodeClick);\n" +
                "                        tree1.expandAll();\n" +
                "                        tree1.render();\n" +
                "                    };\n" +
                " \n" +
                "                    //Add an onDOMReady handler to build the tree when the document is ready\n" +
                "                    YAHOO.util.Event.onDOMReady(treeInit);\n" +
                " \n" +
                "                })();");
        responseWriter.append("</script>");
        responseWriter.flush();
    }

    private void renderHtml(TreeNode node, FacesContext context, UIComponent component)
            throws  IOException
    {
        Tree tree = (Tree) component;
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.append("<li>");
        renderNode(node, context, component);
        if(!node.getChildren().isEmpty())
            responseWriter.append("<ul>");
        for(TreeNode n : node.getChildren()) {
            context.getExternalContext().getRequestMap().put(tree.getVar(), n);
            renderHtml(n, context, component);
        }
        if(!node.getChildren().isEmpty())
            responseWriter.append("</ul>");
        responseWriter.append("</li>");
    }

    private void renderNode(TreeNode node, FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        UITreeNode n = getUITreeNodeByType(component, node.getType());

        responseWriter.startElement("span", n);

        if(n.getStyleClass() != null)
            responseWriter.writeAttribute("class", n.getStyleClass(), "styleClass");

        renderChildren(context, n);

        responseWriter.endElement("span");
    }

    protected void renderChildren(FacesContext context, UIComponent component) throws IOException {
        Iterator<UIComponent> it = component.getChildren().iterator();

        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            UIComponent child = it.next();
            renderChild(context, child);
        }
    }

    protected void renderChild(FacesContext context, UIComponent child) throws IOException {
        if(!child.isRendered())
            return;

        child.encodeBegin(context);

        if(child.getRendersChildren())
            child.encodeChildren(context);
        else
            renderChildren(context, child);

        child.encodeEnd(context);
    }

    public UITreeNode getUITreeNodeByType(UIComponent tree, String type) {
        if(nodes == null) {
            nodes = new HashMap<String,UITreeNode>();
            for(UIComponent child : tree.getChildren()) {
                if(!(child instanceof  UITreeNode))
                    continue;
                UITreeNode uiTreeNode = (UITreeNode) child;

                nodes.put(uiTreeNode.getType(), uiTreeNode);
            }
        }

        UITreeNode node = nodes.get(type);

        if(node == null)
            throw new javax.faces.FacesException("Unsupported tree node type:" + type);

        return node;
    }
}
