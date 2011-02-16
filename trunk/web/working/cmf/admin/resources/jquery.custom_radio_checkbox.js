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

//##############################
// jQuery Custom Radio-buttons and Checkbox; basically it's styling/theming for Checkbox and Radiobutton elements in forms
// By Dharmavirsinh Jhala - dharmavir@gmail.com
// Date of Release: 13th March 10
// Version: 0.8
/*
 USAGE:
	$(document).ready(function(){
		$(":radio").behaveLikeCheckbox();
	}
*/

var elmHeight = "25";	// should be specified based on image size

// Extend JQuery Functionality For Custom Radio Button Functionality
jQuery.fn.extend({
dgStyle: function()
{
	// Initialize with initial load time control state
	$.each($(this), function(){
		var elm	=	$(this).children().get(0);
		elmType = $(elm).attr("type");
		$(this).data('type',elmType);
		$(this).data('checked',$(elm).attr("checked"));
		$(this).dgClear();
	});
	$(this).mousedown(function() { $(this).dgEffect(); });
	$(this).mouseup(function() { $(this).dgHandle(); });	
},
dgClear: function()
{
	if($(this).data("checked") == true)
	{
		$(this).css("backgroundPosition","center -"+(elmHeight*2)+"px");
		}
	else
	{
		$(this).css("backgroundPosition","center 0");
		}	
},
dgEffect: function()
{
	if($(this).data("checked") == true)
		$(this).css({backgroundPosition:"center -"+(elmHeight*3)+"px"});
	else
		$(this).css({backgroundPosition:"center -"+(elmHeight)+"px"});
},
dgHandle: function()
{
	var elm	=	$(this).children().get(0);
	if($(this).data("checked") == true)
		$(elm).dgUncheck(this);
	else
		$(elm).dgCheck(this);
	
	if($(this).data('type') == 'radio')
	{
		$.each($("input[name='"+$(elm).attr("name")+"']"),function()
		{
			if(elm!=this)
				$(this).dgUncheck(-1);
		});
	}
},
dgCheck: function(div)
{
	$(this).attr("checked",true);
	$(div).data('checked',true).css({backgroundPosition:"center -"+(elmHeight*2)+"px"});
},
dgUncheck: function(div)
{
	$(this).attr("checked",false);
	if(div != -1)
		$(div).data('checked',false).css({backgroundPosition:"center 0"});
	else
		$(this).parent().data("checked",false).css({backgroundPosition:"center 0"});
}
});