package generic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * \brief Generic file utilities class<br>
 *
 * This class contains generic function related to Files
 * bugs: none found<br>
 */
public class FileUtils {

  static public boolean deleteDirectory(File path) {
    if( path.exists() ) {
	  File[] files = path.listFiles();
	  for(int i=0; i<files.length; i++) {
	    if(files[i].isDirectory()) {
	      deleteDirectory(files[i]);
	    }else{
          files[i].delete();
        }
      }
    }
    return(path.delete());
  }

  public static boolean deleteDirectory(String location) {
    return deleteDirectory(new File(location));
  }
  
  public static Document readXML(File file){
    if(!file.exists()) return null;
    Document doc = null;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      doc = db.parse(file);
      doc.getDocumentElement().normalize();
    }catch(Exception e){
      System.err.println("XML read/parse Error: "+e);
      e.printStackTrace();
    }
    return doc;
  }
  
	public static void unJar(String path, String to,boolean verbose){
		File file = new File(path);
		Utils.console("Starting unjar of: "+ file.getAbsolutePath() + File.separator + "*.jar");
		if( file.exists() ) {
		  File[] files;
		  if(file.isFile()){
		     files = new File[]{file};
		  }else{
		     files = file.listFiles();
		  }
			for(int i=0; i<files.length; i++) {
				if(!files[i].isDirectory() && files[i].getName().endsWith("jar")) {
					Utils.console("unJar file: "+ files[i].getName() + " (" + (i+1) + "/" + files.length +")");
					try {
						FileInputStream fis =  new FileInputStream(files[i]);
						JarInputStream zis = new JarInputStream(new BufferedInputStream(fis));
						ZipEntry entry;
						while((entry = zis.getNextEntry()) != null){
							writejarEntry(zis,entry,to,false);
						}
						zis.close();
					} catch (IOException e) {
						Utils.log("unJar error:",e);
					}
				}
			}
		}else{
			System.err.println("Failed to create: " + file.getAbsolutePath());
		}
	}
	
	static void writeInJar(JarOutputStream zos, File[] files,String path, boolean verbose) throws IOException{
		CRC32 crc = new CRC32();
		int bytesRead;
		byte[] buffer = new byte[1024];
		for(int i=0; i<files.length; i++) {
			File file = files[i];
			
			if(file.isDirectory()){
				String tname = files[i].getName();
				tname = tname.replaceAll("\\"+System.getProperty("file.separator"), "/");
				JarEntry entry = new JarEntry(tname);
	            entry.setMethod(JarEntry.STORED);
	            entry.setCrc(crc.getValue());
	            writeInJar(zos,file.listFiles(),path + ((!path.equals("")) ? File.separator : "") + file.getName(),verbose);
			}else{
				String entryname = path + ((!path.equals("")) ? File.separator : "") + files[i].getName();
				entryname = entryname.replaceAll("\\"+System.getProperty("file.separator"), "/");
				if(!(entryname.contains("MANIFEST.MF"))){
		            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		            crc.reset();
		            while ((bytesRead = bis.read(buffer)) != -1) {
		                crc.update(buffer, 0, bytesRead);
		            }
		            bis.close();
		            // Reset to beginning of input stream
		            bis = new BufferedInputStream(new FileInputStream(file));
		            
			            JarEntry entry = new JarEntry(entryname);
			            entry.setMethod(ZipEntry.STORED);
			            entry.setCompressedSize(file.length());
			            entry.setSize(file.length());
			            entry.setCrc(crc.getValue());
			            zos.putNextEntry(entry);
		            while ((bytesRead = bis.read(buffer)) != -1) {
		                zos.write(buffer, 0, bytesRead);
		            }
		            bis.close();
				}else{
					Utils.console("Packing: " + file.getAbsolutePath());
				}
			}
        }
	}
	
	static void writejarEntry(JarInputStream jarfile, ZipEntry entry, String path, boolean verbose) throws IOException{
		File tofp = new File(path + File.separator + entry.getName());
		tofp.mkdirs();
		if(entry.isDirectory()){
			return;
		}else{
			tofp.delete();
		}
		int buffer = (int) (entry.getSize() > 0 ? entry.getSize(): 1024); 
		int count = 0;
        int sumcount = 0;
        byte data[] = new byte[buffer];
        FileOutputStream fos = null;
        try{
        	fos = new FileOutputStream(tofp);
        }catch(Exception e){
        	System.err.println("Unable to extract file:" + tofp + " from " + path);
        	return;
        }
        BufferedOutputStream dest = new BufferedOutputStream(fos, buffer);
        while((count = jarfile.read(data, 0, buffer)) != -1) {
           dest.write(data, 0, count);
           sumcount += count;
        }
        if(verbose) System.out.println("Uncompressed: "+ entry.getName() + " size: " + sumcount + " with buffersize: " + buffer);
        dest.flush();
        dest.close();
	}
	
	static void createManifestFile(String loc_output, String mainclass, String creator){
		try {
			FileWriter fos = new FileWriter(loc_output + File.separator + "MANIFEST.MF");
			fos.write("Manifest-Version: 1.0" + "\n");
			fos.write("Main-Class: " + mainclass + "\n");
			fos.write("Created-By: " + creator + "\n");
			fos.close();
		} catch (IOException e) {
			Utils.log("Exception: ",e);
		}
        Utils.console("Written Manifest file");
	}
  
}
