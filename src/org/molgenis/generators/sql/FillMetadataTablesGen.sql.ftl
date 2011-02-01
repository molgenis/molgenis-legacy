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
INSERT INTO MolgenisUser (id, password, emailaddress, firstname, lastname, active, superuser) values (2, "md5_21232f297a57a5a743894a0e4a801fc3", "", "admin", "admin", true, true);
INSERT INTO MolgenisUser (id, password, emailaddress, firstname, lastname, active) values (3, "md5_294de3557d9d00b3d2d8a1e6aab028cf", "", "anonymous", "anonymous", true);

INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "read" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "read" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisGroup";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "read" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUserGroupLink";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "write" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "write" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisGroup";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 2, id, "write" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUserGroupLink";
INSERT INTO MolgenisPermission (role_, entity, permission) SELECT 3, id, "read" FROM MolgenisEntity WHERE MolgenisEntity.name = "MolgenisUser";

<#assign schema = model.getUserinterface()>
<#list schema.getChildren() as screen>
	<#if screen.getClass() == "Form.class">
		<#-- Add code for readonly="true" -->
	</#if>
</#list>

INSERT INTO MolgenisUserGroupLink (group_, user_) VALUES (1, 2);
INSERT INTO MolgenisUserGroupLink (group_, user_) VALUES (1, 3);