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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * User: billreh
 * Date: 2/10/11
 * Time: 10:50 PM
 */
public class StyleTest {
    @Test
    public void testTheStyle() throws Exception {
        Style style = new Style();
        style.setNamespace(Namespace.createFromString("net.tralfamadore"));
        style.setName("style");
        style.setStyle("p { color: black; }");


        Style style2 = new Style();
        style2.setNamespace(style.getNamespace());
        style2.setName(style.getName());
        style2.setStyle(style.getStyle());

        assertEquals(style, style2);

        style2.setNamespace(Namespace.createFromString("net.tralfamadore.site"));
        assertNotSame(style, style2);
        style2.setNamespace(style.getNamespace());
        assertEquals(style, style2);

        style2.setName("moo");
        assertNotSame(style, style2);
        style2.setName(style.getName());
        assertEquals(style, style2);

        style2.setStyle("p { color: blue; }");
        assertNotSame(style, style2);
    }
}
