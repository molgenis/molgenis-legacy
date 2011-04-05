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
#define PATH_SEPARATOR ';' 									//Should be ':' on Solaris, ';' other OSes
#define USER_CLASSPATH "." 									//Where the class files are located

JNIEnv* create_vm(JavaVM** jvm) {
  JNIEnv* env;
  JavaVMInitArgs vm_args;
  JavaVMOption options;
  options.optionString = (char*) "-Djava.class.path=."; 	//Path to the java source code
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
  if (env == NULL){
    return 1;
  }else{
	//TODO Add your own code  
  }
}   
