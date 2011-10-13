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

package net.tralfamadore.test;

import net.tralfamadore.viewScope.ViewScoped;
import org.apache.commons.logging.Log;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * User: billreh
 * Date: 10/12/11
 * Time: 4:03 AM
 */
@Named
@ViewScoped
public class ViewBean implements Serializable {
    @Inject
    private Log log;

    private String prop = "view property";

    public ViewBean() {
    }

    @PostConstruct
    private void init() {
        log.info("ViewBean created");
    }

    public String getProp() {
        log.info("ViewBean property accessed");
        return prop;
    }

    public void setProp(String prop) {
        log.info("ViewBean property set");
        this.prop = prop;
    }

    public void action() {
        log.info("ViewBean action called");
    }
}
