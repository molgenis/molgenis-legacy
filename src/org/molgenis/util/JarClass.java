package org.molgenis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarClass {
	
	public static File getFileFromJARFile(String jar, String fileName) throws Exception{
		 JarInputStream jarFile = null;
		 try
		    {
		        jarFile = new JarInputStream(new FileInputStream(jar));
		        JarEntry jarEntry;
		        do 
		        {       
		                try{
		                        jarEntry = jarFile.getNextJarEntry();
		                }
		                catch(Exception ioe){
		                        throw new Exception("Unable to get next jar entry from jar file '"+jar+"'");
		                }
		                if (jarEntry != null){
		                        System.out.println(jarEntry.getName());
		                }
		        } while (jarEntry != null);
		        closeJarFile(jarFile);
		    }
		    catch(Exception ioe)
		    {
		        throw new Exception("Unable to get Jar input stream from '"+jar+"'");
		    }
		    finally
		    {
		        closeJarFile(jarFile);
		    }
		   return null;
	}
	
	public static ArrayList<String> getClassesFromJARFile(String jar, String packageName) throws Exception{
	    final ArrayList<String> classes = new ArrayList<String>();
	    JarInputStream jarFile = null;
	    try
	    {
	        jarFile = new JarInputStream(new FileInputStream(jar));
	        JarEntry jarEntry;
	        do 
	        {       
	                try
	                {
	                        jarEntry = jarFile.getNextJarEntry();
	                }
	                catch(Exception ioe)
	                {
	                        throw new Exception("Unable to get next jar entry from jar file '"+jar+"'");
	                }
	                if (jarEntry != null){
	                        extractClassFromJar(jar, packageName, classes, jarEntry);
	                }
	        } while (jarEntry != null);
	        closeJarFile(jarFile);
	    }
	    catch(Exception ioe)
	    {
	        throw new Exception("Unable to get Jar input stream from '"+jar+"'");
	    }
	    finally
	    {
	        closeJarFile(jarFile);
	    }
	   return classes;
	}
	
	
	private static void extractClassFromJar(final String jar, final String packageName, final ArrayList<String> classes, JarEntry jarEntry) throws Exception
	{
	    String className = jarEntry.getName();
	    if (className.endsWith(".class")) 
	    {
	        className = className.substring(0, className.length() - ".class".length());
	        if (className.startsWith(packageName))
	        {
	                try{
	                        classes.add(Class.forName(className.replace('/', '.')).toString());
	                } catch (Exception cnfe)
	                {
	                        throw new Exception("unable to find class named " + className.replace('/', '.') + "' within jar '" + jar + "'");
	                }
	        }
	    }
	}
	private static void closeJarFile(final JarInputStream jarFile)
	{
	    if(jarFile != null) 
	    { 
	        try{
	                jarFile.close(); 
	        }
	        catch(IOException ioe){
	        }
	    }
	}
}
