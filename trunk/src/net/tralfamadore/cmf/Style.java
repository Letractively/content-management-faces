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

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 11:44 PM
 */
public class Style extends BaseContent {
    private String style;


    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Style)) return false;
        if (!super.equals(o)) return false;

        Style style1 = (Style) o;

        return !(style != null ? !style.equals(style1.style) : style1.style != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (style != null ? style.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Style{" +
                "style='" + style + '\'' +
                "} " + super.toString();
    }
}
