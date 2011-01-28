<#include "GeneratorHelper.ftl">
<#list model.getConcreteEntities() as entity>
INSERT INTO MolgenisEntity(name,classname) values ("${JavaName(entity)}","${entity.namespace}.${JavaName(entity)}");
<#--list entity.fields as field>
INSERT INTO molgenis_fieldmetadata(entity,name,description) SELECT id, "${name(field)}", "<#if field.description != field.name>${field.description}</#if>" from molgenis_entitymetadata where name="${name(entity)}";
</#list-->
</#list>

INSERT INTO MolgenisRole (__Type, id, name) values ("MolgenisGroup", 1, "system");
INSERT INTO MolgenisRole (__Type, id, name) values ("MolgenisUser", 2, "admin");
INSERT INTO MolgenisRole (__Type, id, name) values ("MolgenisUser", 3, "anonymous");
INSERT INTO MolgenisGroup (id) values (1);
INSERT INTO MolgenisUser (id, password, emailaddress, firstname, lastname, active) values (2, "admin", "", "admin", "admin", true);
INSERT INTO MolgenisUser (id, password, emailaddress, firstname, lastname, active, superuser) values (3, "anonymous", "", "anonymous", "anonymous", true, true);

INSERT INTO MolgenisRole_allowedToView (MolgenisRole, allowedToView) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser";
INSERT INTO MolgenisRole_allowedToView (MolgenisRole, allowedToView) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisGroup";
INSERT INTO MolgenisRole_allowedToView (MolgenisRole, allowedToView) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUserGroupLink";
INSERT INTO MolgenisRole_allowedToEdit (MolgenisRole, allowedToEdit) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser";
INSERT INTO MolgenisRole_allowedToEdit (MolgenisRole, allowedToEdit) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisGroup";
INSERT INTO MolgenisRole_allowedToEdit (MolgenisRole, allowedToEdit) SELECT 2, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUserGroupLink";
INSERT INTO MolgenisRole_allowedToEdit (MolgenisRole, allowedToEdit) SELECT 3, id FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser"; 

INSERT INTO MolgenisUserGroupLink (group_, user_) VALUES (1, 2);
INSERT INTO MolgenisUserGroupLink (group_, user_) VALUES (1, 3);