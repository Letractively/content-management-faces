# Introduction #

Content Management Faces is meant as a drop in cms solution for jsf2.  The idea is to have a single jar file (cmf.jar) that you can put into the WEB-INF/lib directory of your web application that will provide a jsf tag library to insert various content tags in your pages, and a web interface from which you can edit your content.

## Tags ##

Currently cmf has only one tag, the 

&lt;cmf:content&gt;

 tag.  See the ContentTag page for details.

## Admin Interface ##

Cmf provides a built in web interface for configuring content.  With it you can dynamically configure content and style the content.  See the AdminInterface page for details.

## Supported App Servers ##

  * [Glassfish 3](http://glassfish.java.net/)
  * [Tomcat 7](http://tomcat.apache.org/download-70.cgi)



Please see the [Quickstart](Quickstart.md) for installation instructions.