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

/**
 * User: billreh
 * Date: 2/11/11
 * Time: 8:25 PM
 */
public enum SecurityType {
    NONE(new NoneCurrentUserInfo()),
    LOCAL(new LocalCurrentUserInfo()),
    PRINCIPAL(new PrincipalCurrentUserInfo()),
    CUSTOM(new CustomCurrentUserInfo());

    private CurrentUserInfo currentUserInfo;

    SecurityType(CurrentUserInfo currentUserInfo) {
        this.currentUserInfo = currentUserInfo;
    }

    public CurrentUserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }
}
