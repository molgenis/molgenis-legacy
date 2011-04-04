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

#include <stdio.h>
#include <jni.h>
#include <string.h>

#ifndef ${BLOCKName(entity)}_H_
  #define ${BLOCKName(entity)}_H_

  
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