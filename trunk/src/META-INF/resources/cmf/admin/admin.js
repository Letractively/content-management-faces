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

String.prototype.trim = function() { return this.replace(/^\s+|\s+$/g, ''); };

function handleDrop(event, ui) {
    var draggable = ui.draggable;
    var context = draggable.context;
    var childNodes = context.childNodes;
    var anchorElement;
    if(childNodes.length > 1)
        anchorElement = childNodes[1];
    else
        anchorElement = childNodes[0];
    var ref = anchorElement.href;
    var lastSlash = ref.lastIndexOf("/");
    var ntlSlash = ref.substring(0, lastSlash - 1).lastIndexOf("/");
    var s = ref.indexOf('?');
    var ss;
    if( s > -1) {
        ss = ref.substring(0, s);
        ref = ss;
    }
    s = ref.indexOf(';');
    if( s > -1) {
        ss = ref.substring(0, s);
        ref = ss;
    }
    var name = ref.substring(lastSlash + 1);
    var namespace = ref.substring(ntlSlash + 1, lastSlash);
    var form = $(".formClass").get()[0].id;
    PrimeFaces.addSubmitParam(form, 'styleNamespace', namespace);
    PrimeFaces.addSubmitParam(form, 'styleName', name)
}

function startDrop() {
//    alert("starting drop");
}

function addCss(xhr, status, args) {
    var editor = theEditor.getEditor();
    editor.addCss(args.css);

    var config;
    if(editor != null) {
        config = editor.config;
        editor.container.remove(false);
        CKEDITOR.remove(editor);
        editor = null;
        theEditor = null;
    }
    try {
        var css = null;
        if(args != null) {
            css = args.css;
        }

        if(css != null && css.trim().length > 0) {
            CKEDITOR.on('instanceCreated', function(e) {
                var ed = e.editor;
                ed._.styles = [];
                ed.addCss(css);
            });
        }

        var editorId = $(".formClass").get()[0].id + ':editor';
        theEditor = new CKEditor(editorId, config);
    } catch(err) {
        alert(err);
    }
}

function preenInput(event) {
    if(event == null)
        event = window.event;
    var code = event.keyCode;
    if(code == null)
        code = event.which;
    if(
        (code < 48 || code > 57) &&  // numbers
        (code < 65 || code > 90) &&  // uppercase letters
        (code < 97 || code > 122) && // lowercase letters
        code!==46 &&                 //delete
        code!==8 &&                  //back space
        code!==37 &&                 // left arrow
        code!==39                    // right arrow
    ) {
        event.preventDefault();
        return false;
    }
    return true;
}

PrimeFaces.addSubmitParam  = function(parent, name, value) {
    $(this.escapeClientId(parent)).append("<input id=\"" + "submitParam_" +  name + "\" + type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");

    var child = $("#submitParam_" + name);
    child.val(value);

    return this;
};

function showConfigSuccess(xhr) {
    if(xhr.status == "success") {
        var val = $("#derbySuccessPath").html();
        if(val != null && val != "") {
            $("#configDb").hide();
            $("#configDbSuccess").show();
        }
    }
}

