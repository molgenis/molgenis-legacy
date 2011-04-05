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

${CPPName(entity)}::${CPPName(entity)}(JNIEnv* env){
	this->env=env;
	this->clsC = env->FindClass("${CPPName(entity)}");
	if(clsC != NULL){
    	//Get constructor ID for ${CPPName(entity)}
    	coID = env->GetMethodID(clsC, "<init>", "(V)V");
    	findByIdID = env->GetMethodID(clsC, "findByID", "(I)L${package?replace(".cpp","")}.${JavaName(entity)}");
  		findByNameID = env->GetMethodID(clsC, "findByName", "(Ljava.lang.String)L${package?replace(".cpp","")}.${JavaName(entity)}");
    	getID = env->GetMethodID(clsC, "get", "(Ljava.lang.String)Ljava.lang.Object");
  	}else{
    	printf("\nUnable to find the requested class\n");     
  	}
}

${CPPName(entity)}::${CPPName(entity)}(JNIEnv* env<#foreach field in entity.getImplementedFields()>, ${CPPType(field)} ${CPPName(field)}</#foreach>){
	${CPPName(entity)}(env);
	<#foreach field in entity.getImplementedFields()>
	this->${CPPName(field)} = ${CPPName(field)};
	</#foreach>
}

<#foreach field in entity.getImplementedFields()>
  	
${CPPType(field)} ${CPPName(entity)}::get${CPPName(field)}(void){
	return this->${CPPName(field)};
}

void ${CPPName(entity)}::set${CPPName(field)}(${CPPType(field)} in){
	this->${CPPName(field)}=in;
}
</#foreach>