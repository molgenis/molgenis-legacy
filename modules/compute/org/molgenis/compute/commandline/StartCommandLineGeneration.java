package org.molgenis.compute.commandline;

import org.molgenis.compute.WorksheetHelper;
import org.molgenis.util.Tuple;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: georgebyelas
 * Date: 02/11/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class StartCommandLineGeneration
{
    private String fileWorksheet = null;

    private void run() throws Exception
    {
        List<Tuple> worksheet = new WorksheetHelper().readTuplesFromFile(new File(fileWorksheet));
    }

    /*
    * arg0 - file with worksheet elements
    * */
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("enter worksheet file");
            System.exit(1);
        }
        else
        {
            StartCommandLineGeneration generation = new StartCommandLineGeneration();

            generation.setFileWorksheet(args[0]);

            try
            {
                generation.run();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setFileWorksheet(String fileWorksheet)
    {
        this.fileWorksheet = fileWorksheet;
    }
}
