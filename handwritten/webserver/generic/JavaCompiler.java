package generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * \brief Uses the CommandExecutor to compile java files and build .jar files<br>
 *
 * This class add support for compiling .java files and manipulating .jar files.
 * bugs: none found<br>
 */
public class JavaCompiler extends CommandExecutor{
	public class CompileUnit {
		public boolean active           = true;
		public boolean packToJar        = true;
		public String loc_input         = "src";
		public String loc_base_input    = "";
		public String loc_output        = "build";
		public String mainClass         = "";
		public Boolean runnableJar      = false;
		public String customJarName     = "";
		public Vector<String> loc_class = new Vector<String>();
		
		//Compilation unit defaults to building src/ into build/
		public CompileUnit(){
			
		}
		
		//Compilation unit defaults to building src/ into build/
		public CompileUnit(String src_path,String out_path){
			loc_input = src_path;
			loc_output= out_path;
		}
		
		//Compilation unit defaults to building src/ into build/
		public CompileUnit(String src_path,String out_path, String[] deps){
			this(src_path,out_path);
			addDependencies(deps);
		}
		
		//Compilation unit defaults to building src/ into build/
		public CompileUnit(String src_path,String out_path, String[] deps,String mainClass){
			this(src_path,out_path,deps);
			setMainClass(mainClass);
		}
		
		//Adds a dependancy *.jar files will be re packed into 1 single jar
		//Can also be used to make out of source builds for packages
		public void addDependency(String path){
			loc_class.add(path);
		}
		
		//Adds a list of dependancies 
		public void addDependencies(String[] path){
			for(String p : path){
				addDependency(p);
			}
		}
		
		//getJarName get the output jar name
		public String getJarName(){
			if(customJarName!=""){
				return customJarName + ".jar";
			}else{
				if(loc_input.contains(File.separator)){
					return loc_input.substring(loc_input.lastIndexOf(File.separator)+1) + ".jar";
				}
				return loc_input + ".jar";
			}
		}
		
		//Get the dependencies string we can use on the javac classpath
		public String getDependencies(){
			String input = loc_base_input + File.separator + loc_input;
			input = input.startsWith(File.separator)?input.substring(1):input;
			String out = loc_output + ";" + input;
			for(String d : loc_class){
				out = out + ";" + (d.startsWith(File.separator)?d.substring(1):d);
			}
			return out;
		}
		
		//Set the base baseDir for compilation (Easier out of source ?)
		public void setBaseDir(String baseDir){
			loc_base_input = baseDir;
		}
		
		//Sets the main class name for the Manifest
		public void setMainClass(String name){
			mainClass = name;
			runnableJar = true;
		}
		
		public void setCustomJarName(String name){
			customJarName = name;
		}
		
		public Boolean isRunnableJar(){
			return runnableJar;
		}
	}
	
	CompileUnit tocompile = new CompileUnit();
	ArrayList<Thread> running_tasks = new ArrayList<Thread>();
	
	public JavaCompiler() {
		super();
	}
	
	public JavaCompiler(CompileUnit u) {
		super();
		tocompile = u;
	}

	public void CompileTarget(CompileUnit c) {
		Utils.console("Active compile target: " + c.loc_input);
		c = addCompileTarget(c, ((!c.loc_base_input.equals(""))?c.loc_base_input + File.separator:"") + c.loc_input);
		addtask(new Thread(this));
		if(c.active){
			tocompile = c;
			waitForFinish();
			cleartasks();
		}else{
			Utils.console("Inactive compile target");
		}
	}
	
	private CompileUnit addCompileTarget(CompileUnit c, String inputlocation) {
		File path = new File(inputlocation);
		if( path.exists() ) {
			File[] files = path.listFiles();
			 for(int i=0; i<files.length; i++) {
		         if(!files[i].isDirectory()) {
		        	 if(files[i].getName().endsWith("java")){
		        		 String command = "javac " + files[i].getAbsolutePath() + " -d " + c.loc_output + " -cp " + c.getDependencies();
		        		 addCommand(command);
		        	 }else{
		        		 File d = new File(inputlocation.replace(c.loc_input, c.loc_output));
		        		 d.mkdirs();
		        		 addCommand("copy " + files[i].getAbsolutePath() + " " + d.getAbsolutePath() + " /Y");
		        	 }
	        	 }else{
	        		 c = addCompileTarget(c,files[i].getAbsolutePath());
	        	 }
			 }
		}else{
			System.err.println("No such path: " + inputlocation + "Compilation Unit DISABLED");
			c.active=false;
		}
		return c;
	}
	
  void toJarFile(String jarfilepath){
    try {
	  File tofp = new File(jarfilepath);
	  File fromfp = new File(tocompile.loc_output);
	  if (tofp.exists()) {
	    System.err.println("Overwriting old jar: " + jarfilepath);
		tofp.delete();
      }
      File f = new File(tocompile.loc_output + File.separator + "/MANIFEST.MF");
      JarOutputStream zos;
      FileOutputStream fos;
      if(f.exists()){
    	  FileInputStream i = new FileInputStream(f);
    	  Manifest manifest = new Manifest(i);
    	  fos = new FileOutputStream(tofp);
    	  zos = new JarOutputStream(fos,manifest);
      }else{
    	  fos = new FileOutputStream(tofp);
          zos = new JarOutputStream(fos);
      }
	  
      File[] files = fromfp.listFiles();
      FileUtils.writeInJar(zos,files,"",true);
      zos.close();
      System.out.println("Build a JAR: " + jarfilepath);
	}catch(Exception e){
	  Utils.log("Something wrong: ", e);
	}
  }
	
	public void run() {
		if(!tocompile.active)return;
		File f = new File(tocompile.loc_output);
		FileUtils.deleteDirectory(f);
		f.mkdirs();
		for(String location : tocompile.loc_class){
			FileUtils.unJar(location, tocompile.loc_output, false);	
		}
		super.run();
		if(tocompile.packToJar){
			if(tocompile.isRunnableJar()){
				FileUtils.createManifestFile(tocompile.loc_output, tocompile.mainClass,"Danny Arends");
			}
			toJarFile(tocompile.getJarName());
			FileUtils.deleteDirectory(f);
		}
	}
	
	public void addtask(Thread task) {
		running_tasks.add(task);
	}
	
	public void cleartasks() {
		running_tasks.clear();
	}
	
	public void waitForFinish(){
		for(Thread task : running_tasks){
			try{
			task.start();
			}catch(Exception e){
				System.err.println("Error starting job is the JavaCompiler Cleared (cleartasks())? ");
			}
		}
		boolean process_done=false;
		while(!process_done){
			process_done=true;
			for(Thread task : running_tasks){
				if(task.isAlive()){
					process_done=false;
				}
				try{
					Thread.sleep(100); 
				}catch(Exception ie){}
			}
		}
	}

	public CompileUnit newCompileUnit() {
		return new CompileUnit();
	}



	public CompileUnit newCompileUnit(String src_path, String out_path) {
		return new CompileUnit(src_path,out_path);
	}

}
