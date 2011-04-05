<#include "CPPHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* \file ${file}
 * \brief Generated source file for CPP JNI interface
 * Copyright:   GBIC 2010-${year?c}, all rights reserved
 * Date:        ${date}
 * Generator:   ${generator} ${version}
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 
#include "${CPPName(entity)}.h"

${CPPName(entity)}::${CPPName(entity)}(JNIEnv* env)<#if entity.hasAncestor()> : ${CPPName(entity.getAncestor())}(env)</#if>{
	init(env);
}

${CPPName(entity)}::${CPPName(entity)}(JNIEnv* env<#foreach field in entity.getImplementedFields()>, ${CPPType(field)} ${CPPName(field)}</#foreach>)<#if entity.hasAncestor()> : ${CPPName(entity.getAncestor())}(env)</#if>{

	init(env);
	<#foreach field in entity.getImplementedFields()>
	this->${CPPName(field)} = ${CPPName(field)};
	</#foreach>
}

void ${CPPName(entity)}::init(JNIEnv* env){
	this->env=env;
	this->clsC = env->FindClass("${entity.namespace?replace(".","/")}/${CPPName(entity)}");
	if(clsC != NULL){
    	//Get constructor ID for ${CPPName(entity)}
    	printf("Found: ${entity.namespace}.${CPPName(entity)} class\n");
    	coID = env->GetMethodID(clsC, "<init>", "()V");
    	//findByIdID = env->GetMethodID(clsC, "findByID", "(I)L${package?replace(".cpp","")}.${JavaName(entity)}");
  	}else{
    	printf("Unable to find the ${entity.namespace}.${CPPName(entity)} class\n");
  	}
}

jobject ${CPPName(entity)}::Java(){
  jobject temp = env->NewObject(this->clsC, this->coID); 
  if (env->ExceptionOccurred()) {
   	env->ExceptionDescribe();
  }else{
    printf("Java class object ${entity.namespace}.${CPPName(entity)} created\n");
  }
  return temp;
}

${CPPName(entity)}::~${CPPName(entity)}(){
	//Compiler TODO: Figure out if I need manually to call the entity.hasAncestor
}

<#foreach field in entity.getImplementedFields()>
  	
${CPPType(field)} ${CPPName(entity)}::get${CPPName(field)}(void){
	return this->${CPPName(field)};
}

void ${CPPName(entity)}::set${CPPName(field)}(${CPPType(field)} in){
	this->${CPPName(field)}=in;
}
</#foreach>