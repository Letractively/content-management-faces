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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.tralfamadore.config.CmfContext;
import org.junit.Test;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import static junit.framework.Assert.assertEquals;

/**
 * User: billreh
 * Date: 10/21/11
 * Time: 1:42 AM
 */
public class SecurityPhaseListenerTest {
    @Mocked
    FacesContext facesContext;

    @Mocked
    CmfContext cmfContext;

    @Mocked
    ExternalContext externalContext;

    @Test
    public void testPhaseListenerPhaseId() {
        assertEquals(PhaseId.RENDER_RESPONSE, new SecurityPhaseListener().getPhaseId());
    }

    @Test
    public void testPhaseListenerLocalSecurity() throws Exception {
        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            cmfContext.getCurrentUser(); result = null;
            cmfContext.getSecurityType(); result = SecurityType.LOCAL;
            externalContext.redirect(SecurityPhaseListener.loginUrl);
        }};

        SecurityPhaseListener phaseListener = new SecurityPhaseListener();
        //noinspection NullableProblems
        phaseListener.beforePhase(null);

        new Verifications() {{
            cmfContext.getCurrentUser(); times = 1;
            cmfContext.getSecurityType(); times = 1;
            CmfContext.getInstance(); times = 2;
            externalContext.redirect(SecurityPhaseListener.loginUrl); times = 1;
        }};
    }

    @Test
    public void testPhaseListenerCustomSecurity() throws Exception {
        final String customUrl = "customUrl";
        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            cmfContext.getCurrentUser(); result = null;
            cmfContext.getSecurityType(); result = SecurityType.CUSTOM;
            cmfContext.getCustomLoginUrl(); result = customUrl;
            externalContext.redirect(customUrl);
        }};

        SecurityPhaseListener phaseListener = new SecurityPhaseListener();
        //noinspection NullableProblems
        phaseListener.beforePhase(null);

        new Verifications() {{
            cmfContext.getCurrentUser(); times = 1;
            cmfContext.getSecurityType(); times = 1;
            cmfContext.getCustomLoginUrl(); times = 1;
            CmfContext.getInstance(); times = 2;
            externalContext.redirect(customUrl); times = 1;
        }};
    }

    @Test
    public void testPhaseListenerPrincipalSecurity() throws Exception {

        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            cmfContext.getCurrentUser(); result = null;
            cmfContext.getSecurityType(); result = SecurityType.PRINCIPAL;
        }};

        SecurityPhaseListener phaseListener = new SecurityPhaseListener();
        //noinspection NullableProblems
        phaseListener.beforePhase(null);

        new Verifications() {{
            cmfContext.getCurrentUser(); times = 1;
            cmfContext.getSecurityType(); times = 1;
            CmfContext.getInstance(); times = 2;
            externalContext.redirect(anyString); times = 0;
        }};
    }

    @Test
    public void testPhaseListenerNoneSecurity() throws Exception {

        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            cmfContext.getCurrentUser(); result = null;
            cmfContext.getSecurityType(); result = SecurityType.NONE;
        }};

        SecurityPhaseListener phaseListener = new SecurityPhaseListener();
        //noinspection NullableProblems
        phaseListener.beforePhase(null);

        new Verifications() {{
            cmfContext.getCurrentUser(); times = 1;
            cmfContext.getSecurityType(); times = 1;
            CmfContext.getInstance(); times = 2;
            externalContext.redirect(anyString); times = 0;
        }};
    }
}
