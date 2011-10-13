package net.tralfamadore.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 3:25 AM
 */
public class FacesContextProvider {
   @Produces
   @RequestScoped
   public FacesContext getFacesContext() {
      return FacesContext.getCurrentInstance();
   }
}
