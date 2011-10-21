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

package net.tralfamadore;


/**
 * User: billreh
 * Date: 2/10/11
 * Time: 8:45 PM
 */
public class SeleniumServerManager {
    private static class SeleniumServerManagerHolder {
        private static final SeleniumServerManager INSTANCE = new SeleniumServerManager();
    }

    private Object server;

    public static SeleniumServerManager getInstance() {
        return SeleniumServerManagerHolder.INSTANCE;
    }

    private SeleniumServerManager() {
    }

    public void startServer() {
//        if(server == null) {
//            try {
//                server = new SeleniumServer();
//            } catch(Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        try {
//            server.start();
//        } catch(Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public void stopServer() {
        if(server != null) {
            try {
//                server.stop();
                server = null;
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}