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

import net.tralfamadore.config.CmfContext;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.io.IOException;

/**
 * User: billreh
 * Date: 2/11/11
 * Time: 7:56 PM
 */
public class SecurityPhaseListener implements PhaseListener {
    private CmfContext cmfContext = CmfContext.getInstance();

    @Override
    public void afterPhase(PhaseEvent event) {
        // ignore
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if(cmfContext.getCurrentUser() == null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            switch (CmfContext.getInstance().getSecurityType()) {
                case LOCAL:
                    try {
                        externalContext.redirect("./login.jsf");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case PRINCIPAL:
                    // the container should prompt for authentication however the user has it set up
                    break;

                case CUSTOM:
                    try {
                        externalContext.redirect(cmfContext.getCustomLoginUrl());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
