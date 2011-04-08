<#include "CPPHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* \file ${file}
 * \brief Generated header file for CPP JNI interface
 * Copyright:   GBIC 2010-${year?c}, all rights reserved
 * Date:        ${date}
 * Generator:   ${generator} ${version}
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

#ifndef ${BLOCKName(entity)}_H_
  #define ${BLOCKName(entity)}_H_
	
	#include <cstdlib>
	#include <cstdio>
	#include <string>
	#include <cstring>
	#include <iostream>
	#include <vector>
	#include <jni.h>
	<#if entity.hasAncestor()>
	#include "./${entity.getAncestor().getNamespace()?replace(".","/")}/${CPPName(entity.getAncestor())}.h"
	</#if>
	using namespace std;
	
  
  /**
   * \brief ${CPPName(entity)}<br>
   * This class contains the implementation of ${CPPName(entity)}
   * It provides 2 constructors both call init to have the java class mapped in the JVM environment
   * For each field getters and setters are provided, setting the CPP state of the object
   * Call the save() function to save the object in the data
   * bugs: none found<br>
   */  
  class ${CPPName(entity)}<#if entity.hasAncestor()> : public ${CPPName(entity.getAncestor())}</#if>{
  public:
  	${CPPName(entity)}(JNIEnv* env);
  	${CPPName(entity)}(JNIEnv* env<#foreach field in entity.getImplementedFields()>, ${CPPType(field)} ${CPPName(field)}</#foreach>);
  	void check(JNIEnv* env, string message, bool verbose);
  	~${CPPName(entity)}();
  	void init(JNIEnv* env);
  	jobject Java();
  	jobject query(jobject db);
  	jobject findByName(jobject db, char* name);
  	
  	<#foreach field in entity.getImplementedFields()>
  	
  	${CPPType(field)} get${CPPName(field)}(void);
  	void set${CPPName(field)}(${CPPType(field)} in);
  	</#foreach>
  	
  protected:
  	JNIEnv*     env;
  	jclass      clsC;
  	jobject 	obj;
  	jmethodID   coID;
  	jmethodID   queryID;
  	jmethodID   findID;
  	
  	<#foreach field in entity.getImplementedFields()>
  	jmethodID   get${CPPName(field)}ID;
  	jmethodID   set${CPPName(field)}ID;
  	</#foreach>
  private:
  	bool 		verbose;
  };
  
  
#endif