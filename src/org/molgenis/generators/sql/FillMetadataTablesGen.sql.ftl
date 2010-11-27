<#include "GeneratorHelper.ftl">
INSERT INTO MolgenisUserGroup(id,name,editAll) values (1,"admin",true);
INSERT INTO MolgenisUser(id,name,password,active,superuser) values (1,"admin","admin",true,true);
INSERT INTO MolgenisUserGroup(id,name,editAll) values (2,"anonymous",true);
INSERT INTO MolgenisUser(id,name,password, active) values (2,"anonymous","anonymous",true);
INSERT INTO MolgenisUserGroup_allowedToEdit(MolgenisUserGroup,allowedToEdit) SELECT 2, id FROM molgenisentity WHERE molgenisentity.name = "MolgenisUser"; 

<#list model.getConcreteEntities() as entity>
INSERT INTO molgenisentity(name,classname) values ("${JavaName(entity)}","${entity.namespace}.${JavaName(entity)}");
<#--list entity.fields as field>
INSERT INTO molgenis_fieldmetadata(entity,name,description) SELECT id, "${name(field)}", "<#if field.description != field.name>${field.description}</#if>" from molgenis_entitymetadata where name="${name(entity)}";
</#list-->
</#list>