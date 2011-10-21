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

package net.tralfamadore.security;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.*;

/**
 * User: billreh
 * Date: 3/24/11
 * Time: 12:37 AM
 */
public class CmfELResolver extends ELResolver {
    private final Map<Object,Object> cmf = new HashMap<Object, Object>();

    public CmfELResolver() {
        cmf.put("poo", "poo!");
    }

    @Override
    public Object getValue(ELContext elContext, Object base, Object property)
            throws NullPointerException, ELException
    {
        if(base == null) {
            if("cmf".equals(property)) {
                elContext.setPropertyResolved(true);
                return cmf;
            } else {
                return null;
            }
        }

        if(base == cmf || "cmf".equals(base)) {
            elContext.setPropertyResolved(true);
            return cmf.get(property);
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property)
            throws NullPointerException, ELException
    {
        Object o = getValue(elContext, base, property);
        if(o == null)
            return Object.class;
        return o.getClass();
    }

    @Override
    public void setValue(ELContext elContext, Object base, Object property, Object value)
            throws NullPointerException, ELException
    {
        if("cmf".equals(base) || cmf == base) {
            cmf.put(property, value);
            elContext.setPropertyResolved(true);
        }
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property)
            throws NullPointerException, ELException
    {
        elContext.setPropertyResolved(true);
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
        if(base == null) {
            List<FeatureDescriptor> list = new Vector<FeatureDescriptor>();
            FeatureDescriptor fd = new FeatureDescriptor();
            fd.setDisplayName("cmf");
            fd.setName("cmf");
            fd.setPreferred(true);
            fd.setShortDescription("The base of the cmf namespace");
            list.add(fd);
            return list.iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
        if(base == null)
            return cmf.getClass();
        return Object.class;
    }
}
