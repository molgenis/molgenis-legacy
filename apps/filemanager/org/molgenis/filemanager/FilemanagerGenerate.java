package org.molgenis.filemanager;

import org.apache.log4j.Logger;
import org.molgenis.Molgenis;

public class FilemanagerGenerate
{
	public static void main(String[] args) throws Exception
	{
		Logger logger = Logger.getLogger(FilemanagerGenerate.class.getSimpleName());
		new Molgenis("apps/filemanager/org/molgenis/filemanager/filemanager.properties").generate();
		logger.info("Filemanager generated successfully!");
	}
}
