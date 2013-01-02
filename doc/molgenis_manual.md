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

# Database XML format

## \<molgenis>
The `<molgenis>` element is the root of each MOLGENIS application definition file and can contain data definition and/or user interface definition elements. The model can be split, i.e. there can be multiple MOLGENIS XML files for one application, for example *_db.xml and *_ui.xml (see section on molgenis.properties file). 
Example usage of the <molgenis> element:

```xml
<molgenis name="myfirstdb" label="My First MOLGENIS">
  <module name="mymodule"/>
  ...
</molgenis>
```

###Attributes:
<table>
<tr><th>Attribute</th><th>Required</th><th>Description</th></tr>
<tr><td>name</td><td>required</td><td>Name of your MOLGENIS blueprint. This will be used by the generator to name the java packages that are generated. Example: name="name"</td><td></tr>
<tr><td>label</td><td></td><td>Label of your MOLGENIS system. This will be shown on the screen as well as heading in the generated documentation. Example: label="My first MOLGENIS"</td></tr>
<tr><td>version</td><td></td><td>Version of your MOLGENIS system. It is recommended to use this to manage the versions of your application. Example: version=”1.2.3”</td></tr>
</table>

###Child elements
\<module>

## \<module>
The `<module>` element allows designers to group entities in packages which will show up in the generated documentation (and in future MOLGENIS also in the package structure). Example usage:

```xml
<molgenis name="example">
    <module name="module1">
        <description>This is my first module</description>
          <entity name="entity1">
                <field name="f1" type="string" unique="true"/>	
                <field name="f2" type="string"/>
                <field name="f3" type="string"/>
          </entity>
          <entity name="entity2">
                <field name="f1" type="string" unique="true"/>	
                <field name="f2" type="string"/>
                <field name="f3" type="string"/>
          </entity>
       </module>
</molgenis>
```

###Attributes
<table>
<tr><th>Attribute</th><th>Required</th><th>Description</th></tr>
<tr><td>name</td><td>required</td><td>Globally unique name for this entity (within this blueprint).</td></tr>
</table>

###Child elements
* `<entity>`
* `<description>`

##\<entity>
The `<entity>` element defines the structure of one data entity and will result in a table in the database, and several Java classes. Example usage of the <entity> element:
```xml
<entity name="my_class">
    <description>This is my first entity.</description>
    <field name="name" type="string"/>
    <field name="investigation" type="string"/>
    <unique fields="name,investigation"/>
</entity>

<entity name="my_subclass" extends="my_class">
    <description>This class extends my_class and will have fields name,investigation and otherField</description>
    <field name="otherField"/>
</entity>
```

###Attributes
<table>
<tr><th>Attribute</th><th>Required</th><th>Description</th></tr>
<tr><td>name</td><td>required</td><td>Globally unique name for this entity (within this blueprint). Example: name="name"</td><tr.
<tr><td>label</td><td></td>A user-friendly alias to show as form header (default: copied from name). Example: label="Nice name"</td></tr>
<tr><td>extends</td><td></td><td>You can use inheritance to make your entity inherit the fields of its 'superclass'. Example: extends="other_entity"</td></tr>
<tr><td>abstract</td><td></td>You define what programmers call 'interfaces'. This are abstract objects that you can use as 'contract' for other entities to 'implement'. Example: abstract="true"</td></tr>
<tr><td>implements</td><td></td><td>You can use inheritance to make your entity inherit the fields of an 'interface' using implements and refering to 'abstract' entities. The implemented fields are copied to this entity. Example: implements="abstract_entity"</td></tr>
<tr>
<tr><td>decorator</td><td></td><td>You can add custom code to change the way entities are added, updated and removed. See the section on how to write a MappingDecorator plugin. Example: decorator="package.MyDecoratorClass"</td></tr>
</table>

###Child elements
 * Zero or one `<description>` to describe this entity; a description can contain xhtml.
 * zero or more `<field>` that detail entity structure.
 * Zero or more `<unique>` indicating unique constraints on field(s).

###Notes
* Cascading deletes can be configured via FieldElement where you can set xref_cascade

##\<field>
A `<field>` defines one property of an entity (i.e., a table column). 
Example usage of the `<field>` element:
```xml
<field name="field_name" description="this is my first field of type string"/>
<field name="field_name" type="autoid" description="this is a id field, unique autonum integer"/>
<field name="field_name" type="xref" xref_field="other_entity.id_field"
       description="this is a crossrerence to otherentity"/>
<field name="field_name" type="enum" enum_options="[option1,option2]"
       description="this is field of type enum"/>
```

<table>
<tr><th>Attribute</th><th>Required</th><th>Description</th></tr>
<tr><td>name</td><td>required</td><td>Locally unique name for this entity (within this entity). Example: name="name"</td></tr>
<tr><td>type</td><td></td><td>Define the type of data that can be stored in this field (default: string). Examples:
<ul>
   <li> type="autoid":  auto incremented column (useful for entity ID).
   </li><li> type="string": a single line text string of variable length, max 255 chars.
   </li><li> type="int": a natural number.
   </li><li> type="boolean": a boolean.
   </li><li> type="decimal": a decimal number.
   </li><li> type="date": a date.
   </li><li> type="datetime": a date that includes the time.
   </li><li> type="file": attaches a file to the entity.
   </li><li> type="text": a multiline textarea of max 2gb.
   </li><li> type="xref": references to a field in another entity specified by xref_field attribute (required for xref). This will be shown as variable lookup-list.
   </li><li> type="mref": many-to-many references to a field in another entity specified by xref_field attribute (required for mref). This will be shown as multiple select lookup-list. (Under the hood, a link table is generated)
   </li><li> type="enum": references to a fixed look-up list options, specificed by enum_options attribute (required for enum)
</ul>
</td></tr>
<tr><td>label</td><td></td><td>A user-friendly alias to show as form header (default: copied from name). Example: label="Nice entity name"</td></tr>
<tr><td>unique</td><td></td><td>Defines if values of this field must be unique within the entity (default: "false"). Example: unique="true"</td></tr>
<tr><td>nillable</td><td></td><td>Definies if this field can be left without value (default: "false"). Example: nillable="true"</td></tr>
<tr><td>readonly</td><td></td><td>Defines if this field cannot be edited once they are saved (default: "false"). Example: readonly="true"</td></tr>
<tr><td>length</td><td></td><td>Limits the length of a string to 1<=n<=255 (default: "255"). Example: length="12"</td></tr>
<tr><td>xref_entity</td><td>when type="xref"</td><td>Specifies a foreign key to the entity that this xref field must reference to. Example: xref_entity="OtherEntity"</td></tr>
<tr><td>xref_cascade</td><td></td><td>This will enable cascading deletes which means that is the related element is deleted this entity will be deleted as well (default: "false"). Example: xref_cascade="true"</td></tr>
<tr><td>enum_options</td><td>when type="enum"</td><td>The fixed list of options for this enum (required for enum). Example: enum_options="[value1,value2]"</td></tr>
<tr><td>description</td><td></td>Describes this field. This will be visibible to the user in the UI when (s)he mouses over the field or visits the documentation pages. Example: description="One line description"</td></tr>
<tr><td>default</td><td></td><td>Sets a default value for this field. This value is automatically filled in for this field unless the user decides otherwise. Example: default="Pre-filling"</td></tr>
<tr><td>hidden</td><td></td><td>Optional settings to hide field from view. This requires the fields to be nillable="true" or auto="true" or default!=""</td></tr>
</table>

###Child elements
none.

# User Interface XML format
