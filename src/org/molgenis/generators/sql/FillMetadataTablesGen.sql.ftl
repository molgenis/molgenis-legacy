<#include "GeneratorHelper.ftl">
INSERT INTO molgenisrole(id,name,superuser) values (1,"admin",true);
INSERT INTO molgenisuser(id,password,active) values (1,"admin",true);
INSERT INTO molgenisrole(id,name,superuser) values (2,"anonymous",true);
INSERT INTO molgenisuser(id,password, active) values (2,"anonymous",true);
INSERT INTO molgenisrole_allowedtoedit(MolgenisRole,MolgenisEntity) SELECT 2, id FROM molgenisentity WHERE molgenisentity.name = "MolgenisUser"; 

<#list model.getConcreteEntities() as entity>
INSERT INTO molgenisentity(name,classname) values ("${JavaName(entity)}","${entity.namespace}.${JavaName(entity)}");
<#--list entity.fields as field>
INSERT INTO molgenis_fieldmetadata(entity,name,description) SELECT id, "${name(field)}", "<#if field.description != field.name>${field.description}</#if>" from molgenis_entitymetadata where name="${name(entity)}";
</#list-->
</#list>