<#include "GeneratorHelper.ftl">
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="1.0" 
             xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd">
	<persistence-unit name="molgenis" transaction-type="RESOURCE_LOCAL">
<#list model.entities as entity>
            <class>${entity.namespace}.${JavaName(entity)}</class>
</#list>
	    <properties>
	      <property name="javax.persistence.jdbc.url" value="${options.dbUri}"/>
	      <property name="javax.persistence.jdbc.password" value="${options.dbPassword}"/>
	      <property name="javax.persistence.jdbc.driver" value="${options.dbDriver}"/>
	      <property name="javax.persistence.jdbc.user" value="${options.dbUser}"/>
          <property name="javax.persistence.validation.mode" value="none"/>    
          <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL5Dialect"/>
          <property name="hibernate.show_sql" value="true"/>
          
          <!--
          Automatically validates or exports schema DDL to the database when the SessionFactory is created. 
          With create-drop, the database schema will be dropped when the SessionFactory is closed explicitly.
		e.g. validate | update | create | create-drop
           -->
            <property name="hibernate.hbm2ddl.auto" value="validate"/>
           
	    </properties>
	</persistence-unit>
	<persistence-unit name="molgenis_test" transaction-type="RESOURCE_LOCAL">
<#list model.entities as entity>
            <class>${entity.namespace}.${JavaName(entity)}</class>
</#list>
	    <properties>
	      <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost/test_molgenis_test?innodb_autoinc_lock_mode=2"/>
	      <property name="javax.persistence.jdbc.password" value="molgenis"/>
	      <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver"/>
	      <property name="javax.persistence.jdbc.user" value="molgenis"/>
	      <property name="javax.persistence.validation.mode" value="none"/>
          <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL5Dialect"/>
          <property name="hibernate.show_sql" value="true"/>
	    
          <!--
          Automatically validates or exports schema DDL to the database when the SessionFactory is created. 
          With create-drop, the database schema will be dropped when the SessionFactory is closed explicitly.
		e.g. validate | update | create | create-drop
           -->
            <property name="hibernate.hbm2ddl.auto" value="validate"/>	    
	    
	    </properties>
	</persistence-unit>		
</persistence>