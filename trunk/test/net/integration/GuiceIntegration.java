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

package net.integration;

import org.junit.runners.model.InitializationError;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 10:18 AM
 */
public class GuiceIntegration extends GuiceTestRunner {
    /**
     * Creates a new GuiceTestRunner.
     *
     * @param classToRun the test class to run
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed
     */
    public GuiceIntegration(final Class<?> classToRun) throws InitializationError {
        super(classToRun, new TestModule());
    }
}
