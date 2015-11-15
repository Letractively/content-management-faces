# Introduction #

The following steps should get you up and running.  Once you're logged into the admin panel you can continue to use the in memory derby database or specify a place on the file system to persist the data.

# Details #

  1. You must me running in a servlet 3.0 container and be using web.xml version 3.0.
  1. Copy cmf.jar to your project's WEB-INF/lib directory.
  1. If you're running tomcat7, copy derby-10.5.3.0\_1.jar, eclipselink.jar, and javax.persistence.jar to your WEB-INF/lib directory as well.  You'll also need to deploy the jsf implementation libraries (available here: http://javaserverfaces.java.net/download.html).
  1. Copy cmf-config.xml to your project's WEB-INF directory.
  1. Examples of using postgres and mysql datasources are included in the cmf-config.xml file.  If you wish to use one of these databases, database generation scripts are located in the scripts directory.  (You'll need to supply your own jdbc jar files, i.e. mysql-connector.jar, in the WEB-INF/lib directory).
  1. If you're using an IDE, make sure it includes cmf.jar in its classpath so that it sees the tag library.
  1. Include the taglib in your html declaration:
```
      <html xmlns="http://www.w3.org/1999/xhtml"
            xmlns:h="http://java.sun.com/jsf/html"
            xmlns:f="http://java.sun.com/jsf/core"
            xmlns:ui="http://java.sun.com/jsf/facelets"
            xmlns:cmf="http://tralfamadore.net/cmf">
```
  1. You can now use the content tag in your pages like this:
```
      <cmf:content namespace="com.somesite" name="contentName"/>
```
  1. You can access the administration panel at
```
      http://yoursite.com/yourwebroot/cmf/admin/index.jsf
```
  1. The administration panel will prompt you to specify a place on the file system to persist the content data.  If you wish to use the in memory database you can ignore this, but your data will not be persisted between deployments or restarts.

**Note:** Currently, the package has only been verified with Glassfish v3.0.1 and Tomcat 7.   There is no jboss6 support as of yet.