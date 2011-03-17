/*
 Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
 For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config )
{
    config.toolbar = 'MyToolbar';

    config.toolbar_MyToolbar =
            [
                ['Source'],
                ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print', 'SpellChecker', 'Scayt'],
                ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
                ['Bold','Italic','Underline','Strike'],
                ['NumberedList','BulletedList'],
                ['Maximize', 'ShowBlocks'],
                ['About'],
                '/',
                ['Outdent','Indent','Blockquote','CreateDiv'],
                ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
                ['Link','Unlink','Anchor'],
                ['Image','Table','HorizontalRule'],
                ['Format','Font','FontSize'],
                ['TextColor','BGColor']
            ];
    // config.language = 'fr';
    // config.uiColor = '#AADC6E';
};
