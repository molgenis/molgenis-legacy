/* \file E:/eclipse/Production/molgenis_apps/generated/cpp/org/molgenis/data/DataIncludePerEntity.h
 * \brief Generated header file for CPP JNI interface
 * Copyright:   GBIC 2010-2011, all rights reserved
 * Date:        April 5, 2011
 * Generator:   org.molgenis.generators.cpp.IncludePerEntityGen 3.3.3
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

#ifndef MOLGENISERVER_H_
  #define MOLGENISERVER_H_
	
	#include <cstdlib>
	#include <cstdio>
	#include <string>
	#include <cstring>
	#include <vector>
	#include <iostream>
	#include <jni.h>
	using namespace std;
	
  
  /**
   * \brief MolgenisServer<br>
   * This class contains the implementation of MolgenisServer
   * It provides 2 constructors both call init to have the java class mapped in the JVM environment
   * For each field getters and setters are provided, setting the CPP state of the object
   * Call the save() function to save the object in the data
   * bugs: none found<br>
   */  
  class MolgenisServer{
  public:
  	MolgenisServer(JNIEnv* env);
  	jobject Java();
  	jobject getDatabase();
  	~MolgenisServer();
  	void check(JNIEnv* env, string message, bool verbose);
  protected:
  	JNIEnv*     env;
  	jclass      clsC;
  	jobject 	obj;
  	jmethodID   coID;
  	jmethodID   getDatabaseID;
  private:
  	void init(JNIEnv* env);
  };
  
  
#endif