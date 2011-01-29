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

package net.tralfamadore.cmf;

import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 2:34 AM
 */
public class Template extends BaseContent {
    private String content;
    private List<Script> scripts = new Vector<Script>();
    private List<Style> styles = new Vector<Style>();


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Template)) return false;
        if (!super.equals(o)) return false;

        Template template = (Template) o;

        if (content != null ? !content.equals(template.content) : template.content != null) return false;
        if (scripts != null ? !scripts.equals(template.scripts) : template.scripts != null) return false;
        //noinspection RedundantIfStatement
        if (styles != null ? !styles.equals(template.styles) : template.styles != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (scripts != null ? scripts.hashCode() : 0);
        result = 31 * result + (styles != null ? styles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Template{" +
                "content='" + content + '\'' +
                ", scripts=" + scripts +
                ", styles=" + styles +
                "} " + super.toString();
    }
}
