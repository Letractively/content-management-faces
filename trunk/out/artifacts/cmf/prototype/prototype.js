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

/**
 * Created by IntelliJ IDEA.
 * User: billreh
 * Date: 1/23/11
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */

var tree1;
var scriptEditor;
var cssEditor;
var lastHighlightedNodeElement;
var lastHighlightedNode;

//noinspection JSUnusedGlobalSymbols
function hideSlowly(id) {
    $("#" + id).fadeOut('slow');
}

//noinspection JSUnusedGlobalSymbols
function showSlowly(id) {
    $("#" + id).fadeIn('slow');
}

//noinspection JSUnusedGlobalSymbols
function doTheThing() {
    var content = $("#ckCode").val();
    CKEDITOR.instances.ckCode.setData(content);
    CKEDITOR.instances.ckCode.resetDirty();
    CKEDITOR.instances.ckCode.focus();
}

//noinspection JSUnusedGlobalSymbols
function checkTheDirty() {
    if (CKEDITOR.instances.ckCode.checkDirty()) {
        var ret = confirm("Data is unsaved, continue?");
        if(ret)
            CKEDITOR.instances.ckCode.resetDirty();
        return ret;
    }
    return true;
}

//noinspection JSUnusedGlobalSymbols
function updateEditorAndResetDirty(xhr) {
    if(xhr.status == "begin") {
        var editor = CKEDITOR.instances.ckCode;
        var data = editor.getData();
        $("#ckCode").html(data);
    } else {
        CKEDITOR.instances.ckCode.resetDirty();
    }
}


//noinspection JSUnusedGlobalSymbols
function updateField(editor) {
    var data = editor.getData();
    $("#ckCode").html(data);
}

//noinspection JSUnusedGlobalSymbols
function updateEditor() {
    var editor = CKEDITOR.instances.ckCode;
    var data = editor.getData();
    $("#ckCode").html(data);
}

//noinspection JSUnusedGlobalSymbols
function resetDirty() {
    CKEDITOR.instances.ckCode.resetDirty();
}

function insertHeader() {
    showEditor();
    CKEDITOR.instances.ckCode.setData("<h1>Header</h1>");
    $("#addNamespace").css("display", "none");
    $("#addNamespace").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addContent").css("display", "none");
}
function insertArticle() {
    showEditor();
    CKEDITOR.instances.ckCode.setData("<p>Article about stuff...</p>");
    $("#addNamespace").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addContent").css("display", "none");
}

function hideEditor() {
    $("#cke_ckCode").css('display',  'none');
    $("#styles").css("display", "none");
    $("#submit").css('display',  'none');
}

function showEditor() {
    $("#cke_ckCode").css("display", "block");
    $("#styles").css("display", "block");
    $("#submit").css("display", "inline");
}

function hideEditorAndShowNamespace(namespace) {
    hideEditor();
    if(namespace != null && namespace.length > 0)
        namespace = namespace + '.';
    else
        lastHighlightedNode = tree1.getRoot();
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#namespace").html(namespace);
    $("#addNamespace").css("display", "block");
    $("#namespace").focus();
}

function hideEditorAndShowContent(namespace) {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#contentNamespace").html(namespace);
    $("#addContent").css("display", "block");
    $("#content").focus();
}

//noinspection JSUnusedGlobalSymbols
function showEditorAndHideNamespace() {
    showEditor();
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
}

function showCssEditor() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "block");
    cssEditor.setCode(".emboldened {\n  font-weight: bold;\n}");
}

function showJsEditor() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addScriptEditor").css("display", "block");
    scriptEditor.setCode("function blah(arg) {\n  alert(arg);\n}");
}

function handleNodeClick(args) {
    var el = args.event.target.firstElementChild;

    if(el != null)
        $(el).fadeIn('slowly');
    if(lastHighlightedNodeElement != null)
        $(lastHighlightedNodeElement).fadeOut('slowly');

    lastHighlightedNode = args.node;
    lastHighlightedNodeElement = el;

    return tree1.onEventToggleHighlight(args);
}

function addNode(name) {
    var val = $("#" + name);
    var n = val.attr('value');
    new YAHOO.widget.HTMLNode('<b>' + n + '</b>', lastHighlightedNode, true);
    lastHighlightedNode.expand();
    lastHighlightedNode.showChildren();
    lastHighlightedNode.refresh();
}

function removeNode() {
    var node = tree1.getHighlightedNode();
    var parentNode = node.parent;
    if(node == null)
        return;
    tree1.removeNode(node, true);
    parentNode.expand();
    parentNode.refresh();
}

function addArticleStyle() {
    $("#theStyles").append(' <button onclick="showCssEditor(); return false;" class="styleBubble" title="net.tralfamadore.site.page1.articleStyles"> <span style="padding-left: 5px; padding-right: 5px;">articleStyles</span> </button>');
}

(function() {
    var treeInit = function() {
        tree1 = new YAHOO.widget.TreeView("theTree");
        tree1.singleNodeHighlight = true;
        tree1.subscribe('clickEvent',handleNodeClick);
        tree1.expandAll();
        tree1.render();
    };

    //Add an onDOMReady handler to build the tree when the document is ready
    YAHOO.util.Event.onDOMReady(treeInit);

})();

$(document).ready(function() {
    CKEDITOR.on('instanceCreated', function(e) {
        var editor = e.editor;
        editor.addCss('.moo{font-size: 30pt;}');
    });
    CKEDITOR.replace('ckCode');

    cssEditor = CodeMirror.fromTextArea('cssEditor', {
        height: "307px",
        parserfile: "parsecss.js",
        stylesheet: "../codeMirror/css/csscolors.css",
        path: "../codeMirror/js/",
        reindentOnLoad: true,
        lineNumbers: true
    });

//    scriptEditor = CodeMirror.fromTextArea('scriptEditor', {
//        height: "307px",
//        parserfile: ["tokenizejavascript.js", "parsejavascript.js"],
//        stylesheet: "../codeMirror/css/jscolors.css",
//        path: "../codeMirror/js/",
//        reindentOnLoad: true,
//        lineNumbers: true
//    });
});

