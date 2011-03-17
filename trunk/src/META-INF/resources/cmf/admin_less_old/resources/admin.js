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


function showConfigSuccess(xhr) {
    if(xhr.status == "success") {
        var val = $("#derbySuccessPath").html();
        if(val != null && val != "") {
            $("#configDb").hide();
            $("#configDbSuccess").show();
        }
    }
}

function doTheThing(xhr) {
    if(xhr.status == "success") {
        CKEDITOR.instances.ckCode.destroy();
        CKEDITOR.on('instanceCreated', function(e) {
            var editor = e.editor;
            var css = $("#currentStyles").text();
            editor.addCss(css);
        });
        CKEDITOR.replace('ckCode');
        CKEDITOR.instances.ckCode.resetDirty();
        showEditorAndHideNamespace();
        CKEDITOR.instances.ckCode.focus();
    }
}

function rendTreeAndDropStyle(xhr) {
    if(xhr.status == "success") {
        rendTree(xhr);
        updateEditorPostStyleDrop();
    }
}

function updateEditorPostStyleDrop() {
    CKEDITOR.instances.ckCode.destroy();
    CKEDITOR.on('instanceCreated', function(e) {
        var editor = e.editor;
        var css = $("#currentStyles").text();
        editor.addCss(css);
    });
    CKEDITOR.replace('ckCode');
    CKEDITOR.instances.ckCode.resetDirty();
    showEditorAndHideNamespace();
    CKEDITOR.instances.ckCode.focus();
}

function hideOriginalStyle() {
    $("#cssEditor").hide();
}

function doTheStyleThing() {
    var content = $("#cssEditor").val();
    getCssEditor().setCode(content);
    showCssEditor();
    $("#cssEditor").hide();
}

function doTheStyleThing2(xhr) {
    var status = xhr.status;
    var content;
    if(status == "success") {
        content = $("#cssEditor").val();
        cssEditor.setCode(content);
        showCssEditor();
        $("#cssEditor").hide();
        rendTree(xhr);
    }
}
function addCssToEditor() {
    var content = $("#cssEditor").val();
    cssEditor.setCode(content);
    CKEDITOR.instances.ckCode.addCss(content);
//    CKEDITOR.instances.ckCode.setMode('source');
    CKEDITOR.instances.ckCode.setMode('wysiwyg');
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
        $("#ckCode").text(data);
    } else {
        CKEDITOR.instances.ckCode.resetDirty();
    }
}


//noinspection JSUnusedGlobalSymbols
function updateField(editor) {
    var data = editor.getData();
    $("#ckCode").text(data);
}

function updateEditor() {
    var editor = CKEDITOR.instances.ckCode;
    var data = editor.getData();
    $("#ckCode").text(data);
}

function updateStyleEditor() {
    var data = getCssEditor().getCode();
    $("#cssEditor").text(data);
}

//noinspection JSUnusedGlobalSymbols
function resetDirty() {
    CKEDITOR.instances.ckCode.resetDirty();
}

function hideEditor() {
    $("#configDbSuccess").css("display", "none");
    $("#cke_ckCode").css('display',  'none');
    $("#styles").css("display", "none");
    $("#theEditor").css("display", "none");
    $("#submit").css('display',  'none');
}

function showEditor() {
    $("#configDbSuccess").css("display", "none");
    $("#cke_ckCode").css("display", "block");
    $("#styles").css("display", "block");
    $("#theEditor").css("display", "block");
    $("#submit").css("display", "inline");
    $("#cke_ckCode").css("display", "block");
}

function hideEditorAndShowNamespaceAdmin() {
    hideEditorAndShowNamespace();
    $("#submitNamespace").hide();
    $("#in").hide();
}
function hideEditorAndShowNamespace() {
    hideEditor();
    $("#configDbSuccess").css("display", "none");
    $("#configDb").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addNamespace").css("display", "block");
    $("#addStyle").css("display", "none");
    $("#submitNamespace").show();
    $("#in").show();
    $("#in").focus();
}

function hideEditorAndShowStyle() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addStyle").css("display", "block");
    $("#style").focus();
}

function hideEditorAndShowContent() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addContent").css("display", "block");
    $("#addStyle").css("display", "none");
    $("#content").focus();
}

function showEditorAndHideNamespace() {
    showEditor();
    $("#configDbSuccess").css("display", "none");
    $("#configDb").css("display", "none");
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addStyle").css("display", "none");
}

function showCssEditor() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addScriptEditor").css("display", "none");
    $("#addCssEditor").css("display", "block");
    $("#addStyle").css("display", "none");
    getCssEditor().focus();
}

//noinspection JSUnusedGlobalSymbols
function showJsEditor() {
    hideEditor();
    $("#addNamespace").css("display", "none");
    $("#addContent").css("display", "none");
    $("#addCssEditor").css("display", "none");
    $("#addScriptEditor").css("display", "block");
    $("#addStyle").css("display", "none");
    scriptEditor.setCode("function blah(arg) {\n  alert(arg);\n}");
}

//noinspection JSUnusedGlobalSymbols
function handleNodeClick(args) {
    var el = args.event.target.nextElementSibling;

    if(el != null)
        $(el).fadeIn('slowly');
    if(lastHighlightedNodeElement != null)
        $(lastHighlightedNodeElement).fadeOut('slowly');

    lastHighlightedNode = args.node;
    lastHighlightedNodeElement = el;

    return tree1.onEventToggleHighlight(args);
}

function getCssEditor() {
    if(cssEditor == null) {
        cssEditor = CodeMirror.fromTextArea('cssEditor', {
            height: "307px",
            parserfile: "parsecss.js",
            stylesheet: "../codeMirror/css/csscolors.css",
            path: "../codeMirror/js/",
            reindentOnLoad: true,
            lineNumbers: true
        });
    }
    return cssEditor;
}

function rendTree(xhr) {
    if(xhr.status == "success" && $.browser.mozilla) {
        tree1 = new YAHOO.widget.TreeView("theTree");
        tree1.singleNodeHighlight = true;
        tree1.subscribe('clickEvent',handleNodeClick);
        tree1.expandAll();
        tree1.render();
    }
}

$(document).ready(function() {
    var css = $("#currentStyles").text();
    CKEDITOR.on('instanceCreated', function(e) {
        var editor = e.editor;
        editor.addCss(css);
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
    if(tree1 != null) {
        tree1.render();
    }
});
