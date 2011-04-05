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
	#include <vector>
	#include <jni.h>
	using namespace std;
	
  
  /**
   * \brief ${CPPName(entity)}<br>
   *
   * bugs: none found<br>
   */  
  class ${CPPName(entity)}{
  public:
  	${CPPName(entity)}(JNIEnv* env);
  	~${CPPName(entity)}();
  	${CPPName(entity)}(JNIEnv* env<#foreach field in entity.getImplementedFields()>, ${CPPType(field)} ${CPPName(field)}</#foreach>);
  	void init(JNIEnv* env);
  	jobject New${CPPName(entity)}();
  	jobject Get(char* name);
  	<#foreach field in entity.getImplementedFields()>
  	
  	${CPPType(field)} get${CPPName(field)}(void);
  	void set${CPPName(field)}(${CPPType(field)} in);
  	</#foreach>
  	
  protected:
  	JNIEnv*     env;
  	jclass      clsC;
  	jmethodID   coID;
  	jmethodID   findByIdID;
  	jmethodID   findByNameID;
  	jmethodID   getID;
  private:
  	<#foreach field in entity.getImplementedFields()>
  	${CPPType(field)} ${CPPName(field)};
  	</#foreach>
  };
  
  
#endif