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
	set${CPPName(field)}(${CPPName(field)});
	</#foreach>
}

void ${CPPName(entity)}::init(JNIEnv* env){
	this->verbose = false;
	this->env=env;
	this->clsC = env->FindClass("${entity.namespace?replace(".","/")}/${CPPName(entity)}");
	if(clsC != NULL){
    	cout << "Found: ${entity.namespace}.${CPPName(entity)}" << endl;
    	this->coID = env->GetMethodID(this->clsC, "<init>", "()V");
    	check(env,"Mapped: ${entity.namespace}.${CPPName(entity)} Constructor",verbose);
    	this->obj = env->NewObject(this->clsC, this->coID);
      	check(env,"Created: ${entity.namespace}.${CPPName(entity)}",verbose);
      	
      	this->queryID = env->GetStaticMethodID(this->clsC, "query", "(Lorg/molgenis/framework/db/Database;)Lorg/molgenis/framework/db/Query;");
    	check(env,"Mapped: ${entity.namespace}.${CPPName(entity)} QUERY",verbose);
    	this->findID = env->GetStaticMethodID(this->clsC, "find", "(Lorg/molgenis/framework/db/Database;[Lorg/molgenis/framework/db/QueryRule;)Ljava/util/List;");
    	check(env,"Mapped: ${entity.namespace}.${CPPName(entity)} FIND",verbose);
    	
    	<#foreach field in entity.getImplementedFields()>
    	this->get${CPPName(field)}ID = env->GetMethodID(this->clsC, "get${CPPName(field)}", "()${CPPJavaType(field)}");
    	check(env,"Mapped: ${entity.namespace}.${CPPName(entity)}.get${CPPName(field)}()",verbose);
    	this->set${CPPName(field)}ID = env->GetMethodID(this->clsC, "set${CPPName(field)}", "(${CPPJavaType(field)})V");
    	check(env,"Mapped: ${entity.namespace}.${CPPName(entity)}.set${CPPName(field)}(${CPPJavaType(field)})",verbose);
    	</#foreach>
  	}else{
  	  cout << "No such class: ${entity.namespace}.${CPPName(entity)} class" << endl;
  	}
}

jobject ${CPPName(entity)}::Java(){
  return this->obj;
}

void ${CPPName(entity)}::check(JNIEnv* env, string message, bool verbose){
  if (env->ExceptionOccurred()) {
	env->ExceptionDescribe();
  }else{
  	if(verbose)	cout << message << endl;
  }
}

jobject ${CPPName(entity)}::query(jobject db){
  jobject temp =  env->CallStaticObjectMethod(this->clsC,queryID, db);
  check(env,"Method called: query of ${entity.namespace}.${CPPName(entity)}",verbose);
  return temp;
}

jobject ${CPPName(entity)}::find(jobject db){
  jobject temp =  env->CallStaticObjectMethod(this->clsC,findID, db);
  check(env,"Method called: find of ${entity.namespace}.${CPPName(entity)}",verbose);
  return temp;
}

${CPPName(entity)}::~${CPPName(entity)}(){
	//Compiler TODO: Figure out if I need manually to call the entity.hasAncestor
}


<#foreach field in entity.getImplementedFields()>
  	
${CPPType(field)} ${CPPName(entity)}::get${CPPName(field)}(void){
	${CPPType(field)} r;
	<#if ( CPPType(field) == "string") >
	jboolean blnIsCopy;
	r = env->GetStringUTFChars((jstring)env->CallObjectMethod(this->obj,get${CPPName(field)}ID),&blnIsCopy);
	<#elseif (CPPType(field) == "bool")>
	r = (${CPPType(field)})env->CallBooleanMethod(this->obj,get${CPPName(field)}ID);
	<#elseif (CPPType(field) == "double")>
	r = (${CPPType(field)})env->CallDoubleMethod(this->obj,get${CPPName(field)}ID);
	<#elseif (CPPType(field) == "int")>
	r = (${CPPType(field)})env->CallIntMethod(this->obj,get${CPPName(field)}ID);
	<#elseif (CPPType(field) == "vector<int>")>
	jintArray javaArray = (jintArray)env->CallObjectMethod(this->obj,get${CPPName(field)}ID);
	jint nitems = env->GetArrayLength(javaArray);
	cout << "Found:" << nitems << "in array" << endl;
	for(int x=0;x<nitems;x++){
	  r.push_back((int)(*env->GetIntArrayElements(javaArray, 0)));
	}
	<#else>
	r = (${CPPType(field)})env->CallObjectMethod(this->obj,get${CPPName(field)}ID);
	</#if>
	return r;
}

void ${CPPName(entity)}::set${CPPName(field)}(${CPPType(field)} in){
	env->CallObjectMethod(this->obj,set${CPPName(field)}ID);
}
</#foreach>