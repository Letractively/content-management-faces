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

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * User: billreh
 * Date: 2/10/11
 * Time: 10:50 PM
 */
public class ContentTest {
    @Test
    public void testTheContent() throws Exception {
        Content content = new Content();
        content.setNamespace(Namespace.createFromString("net.tralfamadore"));
        content.setName("article");
        content.setDateCreated(new Date());
        content.setDateModified(new Date());
        content.setContent("hello there");


        Content content2 = new Content();
        content2.setNamespace(content.getNamespace());
        content2.setName(content.getName());
        content2.setDateCreated(content.getDateCreated());
        content2.setDateModified(content.getDateModified());
        content2.setContent(content.getContent());

        assertEquals(content, content2);

        content2.setNamespace(Namespace.createFromString("net.tralfamadore.site"));
        assertNotSame(content, content2);
        content2.setNamespace(content.getNamespace());
        assertEquals(content, content2);

        content2.setName("moo");
        assertNotSame(content, content2);
        content2.setName(content.getName());
        assertEquals(content, content2);

        content2.setDateCreated(new Date());
        assertNotSame(content, content2);
        content2.setDateCreated(content.getDateCreated());
        assertEquals(content, content2);

        content.setDateModified(new Date());
        assertNotSame(content, content2);
        content2.setDateModified(content.getDateModified());
        assertEquals(content, content2);

        content2.setContent("blah blah");
        assertNotSame(content, content2);
    }
}
