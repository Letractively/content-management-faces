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

package net.tralfamadore.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 10:16 AM
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner
{
  private final Injector injector;

  /**
   * Creates a new GuiceTestRunner.
   *
   * @param classToRun the test class to run
   * @param modules the Guice modules
   * @throws InitializationError if the test class is malformed
   */
  public GuiceTestRunner(final Class<?> classToRun, Module... modules) throws InitializationError
  {
    super(classToRun);
    this.injector = Guice.createInjector(modules);
  }

  @Override
  public Object createTest()
  {
    return injector.getInstance(getTestClass().getJavaClass());
  }

  @Override
  protected void validateZeroArgConstructor(List<Throwable> errors)
  {
    // Guice can inject constructors with parameters so we don't want this method to trigger an error
  }

  /**
   * Returns the Guice injector.
   *
   * @return the Guice injector
   */
  protected Injector getInjector()
  {
    return injector;
  }
}
