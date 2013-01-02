Table of contents

# Introduction
This document is a hands-on guide for MOLGENIS application development.

## Why MOLGENIS?

Relational (SQL) databases  are the workhorses of most structured data management around the world. However it still takes surprisingly amounts of effort to design and implement a full database application. The MOLGENIS  platform allows you to automatically generate rich database software to your specifications, including web user interfaces to manage and query your data, various database back ends to store your data, and programmatic interfaces to the R language and web services.  You tell MOLGENIS what to generate using an data model and user interface model described in XML; at the push of a button MOLGENIS translates this model into SQL, Java and R program files. Also documentation is generated. While the standard generated MOLGENIS is sufficient for most data management needs, MOLGENIS also allows you to plug in handwritten software components that build on the auto-generated software platform. 

## What will you achieve with this guide?
 * This guide can be used in a walk-through fashion to learn how:
 * To model rich data models using MOLGENIS data definition language 
 * To generate your own customized MOLGENIS databases from scratch 
 * To generate a MOLGENIS to access existing databases 
 * To enhance the standard generated MOLGENIS with your own UI plug-ins 
 * And how to automatically manage and retrieve your data using the Java, R and SOAP interfaces This guide assumes minimal Eclipse, Java and database experience; if not we suggest to team up with someone who does.

# MOLGENIS XML format

## <molgenis>
The <molgenis> element is the root of each MOLGENIS application definition file and can contain data definition and/or user interface definition elements. The model can be split, i.e. there can be multiple MOLGENIS XML files for one application, for example *_db.xml and *_ui.xml (see section on molgenis.properties file). 
Example usage of the <molgenis> element:

```xml
<molgenis name="myfirstdb" label="My First Database">
  <menu name="mainmenu"> ...
  <entity name="firstentity"/>
  <module name="mymodule"/>
  ...
</molgenis>
```

### Required attributes
 * name="name": name of your MOLGENIS blueprint. This will be used by the generator to name the java packages that are generated..

### Optional attributes
 * label="your label": Label of your MOLGENIS system. This will be shown on the screen as well as heading in the generated documentation.
 * Version=”1.2.3”: Version of your MOLGENIS system. It is recommended to use this to manage the versions of your application.

### Child elements
 * One or more [MenuElement \<menu>] or [FormElement \<form>] elements.
 * Zero or one <description> elements describing the application
 * Many [EntityElement <entity>] elements describing the data structure. 
 * Many [ModuleElement <module>] elements describing the data structure. Modules are containers to group \<entity> elements in a shared namespace.