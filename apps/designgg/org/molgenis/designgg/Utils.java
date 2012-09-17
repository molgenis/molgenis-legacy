package org.molgenis.designgg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Contains several methods used in more than one class and even in several layers.
 * 
 * @author Steffan Jacobs
 * 
 */
public class Utils
{

    /**
     * Deletes a directory whether empty or not.
     * 
     * @param path Directory to delete
     * @return Boolean indicating whether the directory was deleted successfully
     */
    public static boolean deleteDirectory( File path )
    {
        if ( path.exists() )
        {
            File[] files = path.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isDirectory() )
                {
                    deleteDirectory( files[i] );
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return ( path.delete() );
    }

    /**
     * Retrieves a file from the harddrive.
     * 
     * @param completefilename File name of a file to retrieve
     * @return Byte array of the file.
     * @throws Exception Any Exception that occurred.
     */
    public static byte[] getFile( String completefilename ) throws Exception
    {
        // Same as FileRepository.getFile()

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream( completefilename );
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = ( int ) fc.size();
        byte[] filebytes = new byte[sz];
        MappedByteBuffer bb = fc.map( FileChannel.MapMode.READ_ONLY, 0, sz );

        int i = 0;
        while ( bb.hasRemaining() )
        {
            filebytes[i] = bb.get();
            i++;
        }

        fis.close();

        return filebytes;
    }
    
    /**
     * Retrieves a file from the harddrive.
     * 
     * @param completefilename File name of a file to retrieve
     * @return Byte array of the file.
     * @throws Exception Any Exception that occurred.
     */
    public static void setFile( String fullFileName, byte[] fileContent ) throws Exception
    {              	
    	File f = new File(fullFileName);
    	f.getParentFile().mkdirs();
    	
    	// Open the file and then get a channel from the stream
    	FileOutputStream fos = new FileOutputStream( fullFileName );
    	FileChannel fc = fos.getChannel();
    	ByteBuffer bb = ByteBuffer.wrap( fileContent );
    	fc.write(bb);    	
    	
        // Get the file's size and then map it into memory
        //int fileSize = fileContent.length;

        //MappedByteBuffer mbb = fc.map( FileChannel.MapMode.READ_WRITE, 0, fileSize);
        // We write the contents of the file with one shot
        //mbb.put(fileContent); 
        
        fos.close();        
    } 

    /**
     * Get's the system's temporary directory
     * 
     * @return File Object representing the directory.
     */
    public static File getSystemTempDir()
    {
        // find the system tempdir
        String tmpdir = System.getProperty( "java.io.tmpdir" );
        if ( !( tmpdir.endsWith( "/" ) || tmpdir.endsWith( "\\" ) ) )
        {
            tmpdir = tmpdir + System.getProperty( "file.separator" );
        }
        return new File( tmpdir );
    }

}
