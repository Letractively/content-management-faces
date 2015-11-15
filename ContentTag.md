# Content #

The content tag is the basic tag for inserting content with cmf.  A name and namespace are specified in the attributes of the tag and correlated in the admin interface to insert dynamic content.  Text inside the tag is considered the default text if no text has been dynamically assigned through the interface.

## Info ##
| Tag | content |
|:----|:--------|
| Component Class | `net.tralfamadore.cmf.component.content.Content` |
| Component Type | `javax.faces.component.UIOutput` |
| Renderer Class | `net.tralfamadore.component.content.ContentRenderer` |


## Attributes ##
| **Name** | **Default** | **Type** | **Description** |
|:---------|:------------|:---------|:----------------|
| _name_   | null        | String   | _Required._  Name for the content.  The name must be unique within its namespace. |
| _namespace_ | null        | String   | _Required._  Path under wich content and styles exist.  All namespace names are written as name.of.namespace.separated.by.periods and must be unique. |
| id       | assigned    | String   | Unique component identifier. |
| escape   | true        | boolean  | Set to true to escape html. |
| binding  | null        | UIComponent | An el expression that points to a server side UIComponent field in a backing bean, which can be cast to the `net.tralfamadore.component.content.Content` class. |
| rendered | true        | boolean  | Whether the component is rendered. |
| style    | null        | String   | Html style attribute. |
| styleClass | null        | String   | Css class name. |
| dir      | assigned    | String   | Unique component identifier. |
| lang     | null        | String   | Html lang attribute. |
| title    | null        | String   | Html dir attribute. |

## Example ##

In your .xhtml file you would put something like the following:

`<cmf:content name="header" namespace="com.mysite.page1">Default Header</cmf:content>`

In the administration panel you would configure content for that namespace and name:

![http://content-management-faces.googlecode.com/svn/wiki/images/contentTagExample.png](http://content-management-faces.googlecode.com/svn/wiki/images/contentTagExample.png)