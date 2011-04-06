/* \file E:/eclipse/Production/molgenis_apps/generated/cpp/org/molgenis/data/DataSourcePerEntity.cpp
 * \brief Generated source file for CPP JNI interface
 * Copyright:   GBIC 2010-2011, all rights reserved
 * Date:        April 5, 2011
 * Generator:   org.molgenis.generators.cpp.SourcePerEntityGen 3.3.3
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 
#include "MolgenisServer.h"

MolgenisServer::MolgenisServer(JNIEnv* env){
	init(env);
}

void MolgenisServer::init(JNIEnv* env){
	this->env=env;
	this->clsC = env->FindClass("org/molgenis/xgap/xqtlworkbench_standalone/TestDatabase");
	if(clsC != NULL){
      cout << "Found: TestDatabase" << endl;
      this->coID = env->GetMethodID(clsC, "<init>", "()V");
      check(env," Mapped: Constructor",true);
      this->obj = env->NewObject(this->clsC, this->coID);
      check(env,"Created: Object",true);
      getDatabaseID = env->GetMethodID(env->GetObjectClass(obj), "getDatabase", "()Lorg/molgenis/framework/db/Database;");
      check(env," Mapped: org/molgenis/framework/db/Database getDatabase()",true);
  	}else{
      cout << "No such class: org.molgenis.xgap.xqtlworkbench_standalone.TestDatabase class" << endl;
  	}
}

jobject MolgenisServer::Java(){
  return this->obj;
}

jobject MolgenisServer::getDatabase(){
  jobject temp =  env->CallObjectMethod(this->obj,getDatabaseID, NULL);
  check(env,"Method called: HSQL Database created",true);
  return temp;
}

void MolgenisServer::check(JNIEnv* env, string message, bool verbose){
  if (env->ExceptionOccurred()) {
	env->ExceptionDescribe();
  }else{
  	if(verbose)	cout << message << endl;
  }
}

MolgenisServer::~MolgenisServer(){
	//Compiler TODO: Figure out if I need manually to call the entity.hasAncestor
}
