<#--helper functions-->
<#include "CPPHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

#include <cstdlib>
#include <cstdio>
#include <string>
#include <vector>
#include <jni.h>
<#list model.entities as entity>
#include "${entity.namespace?replace(".", "/")}/${JavaName(entity)}.h"
</#list>

using namespace std;

char* classpath = (char*)"-Djava.class.path=${UserHome}/build/classes;${UserHome?replace("_apps","")}/bin";

JNIEnv* create_vm(JavaVM** jvm) {
  JNIEnv* env;
  JavaVMInitArgs vm_args;
  JavaVMOption options;
  options.optionString = classpath;
  
  vm_args.version = JNI_VERSION_1_6; 						//JDK version. This indicates version 1.6
  vm_args.nOptions = 1;
  vm_args.options = &options;
  vm_args.ignoreUnrecognized = 0;

  int ret = JNI_CreateJavaVM(jvm, (void**)&env, &vm_args);
  if(ret < 0)
    printf("\nUnable to Launch JVM\n");       
  return env;
}

int main(int argc, char* argv[]){
  JNIEnv* env;
  JavaVM* jvm;
  env = create_vm(&jvm);
  if(!(env == NULL)){
    <#list model.entities as entity>
  	${JavaName(entity)}* test${entity_index} = new ${JavaName(entity)}(env);
  	test${entity_index}->Java();
  	</#list>
	//TODO Add your own code  
  }
  jvm->DestroyJavaVM();
}   
