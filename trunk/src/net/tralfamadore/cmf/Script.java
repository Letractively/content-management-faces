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
 * Time: 11:45 PM
 */
public class Script extends BaseContent {
    private String script;


    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Script)) return false;
        if (!super.equals(o)) return false;

        Script script1 = (Script) o;

        return !(script != null ? !script.equals(script1.script) : script1.script != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (script != null ? script.hashCode() : 0);
        return result;
    }
}
