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

import javax.el.*;
import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: billreh
 * Date: 3/24/11
 * Time: 12:37 AM
 */
public class CmfELResolver extends ELResolver {
    public static class CMF {
        public Map<Object,Object> cmf = new HashMap<Object, Object>();
    }

    CMF cmf = new CMF();

    @Override
    public Object getValue(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException
    {
//        System.out.println("getValue");
//        System.out.println("base: " + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
//        System.out.println("property: " + (property == null ? "" : property.getClass().getSimpleName()) + " " + property);

        if(base == null) {
            if("cmf".equals(property)) {
                System.out.println("cmf motherfucker");
                return cmf;
            } else {
                return null;
            }
        }
        if(base == cmf)
            System.out.println("property: " + property);
        if(property == "poo")
            System.out.println("base: " + base);

        System.out.println("cmf? " + base);
//        return cmf.cmf.get(property);
        return null;
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException
    {
//        System.out.println("getType");
//        System.out.println("base: (" + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
//        System.out.println("property: (" + (property == null ? "" : property.getClass().getSimpleName()) + " " + property);
        return null;
    }

    @Override
    public void setValue(ELContext elContext, Object base, Object property, Object value)
            throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException
    {
//        System.out.println("setValue");
//        System.out.println("base: (" + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
//        System.out.println("property: (" + (property == null ? "" : property.getClass().getSimpleName()) + " " + property);
        System.out.println("value: (" + (value == null ? "" : value.getClass().getSimpleName()) + " " + value);
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException
    {
//        System.out.println("isReadOnly");
//        System.out.println("base: (" + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
//        System.out.println("property: (" + (property == null ? "" : property.getClass().getSimpleName()) + " " + property);
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
//        System.out.println("getFeatureDescriptors");
//        System.out.println("base: (" + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
//        System.out.println("getCommonPropertyType");
//        System.out.println("base: (" + (base == null ? "" : base.getClass().getSimpleName()) + " " + base);
        return null;
    }
}
