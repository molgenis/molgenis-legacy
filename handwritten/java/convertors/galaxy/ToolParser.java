package convertors.galaxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ToolParser
{
	public static void main(String [] args) throws IOException, JAXBException
	{
		File dir = new File("D:/Development/molgenis3_3/galaxy/tools/");

		//Map<String, Integer> nodeStats = new LinkedHashMap<String, Integer>();
		for (File f : getFilesRecursive(dir))
		{
			try
			{
				Tool t = loadTool(f);
				
				System.out.println(t);
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("error with file " + f + e.getMessage());
				return;
			}

		}
	}
	
	private static Tool loadTool(File xml) throws JAXBException
	{
		JAXBContext jaxbContext = JAXBContext.newInstance("org.molgenis.model.tool");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Tool)unmarshaller.unmarshal(xml);
	}
	
	
	private static String toString(Tool model) throws JAXBException
	{
		// save to xml (FIXME: now print only)
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JAXBContext jaxbContext = JAXBContext.newInstance("org.molgenis.language.jaxb");
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.marshal(model, out);
		return out.toString().trim();
	}
	
	private static List<File> getFilesRecursive(final File f) throws IOException
	{
		List<File> files = new ArrayList<File>();

		if (f.isDirectory())
		{
			final File[] childs = f.listFiles();
			for (File child : childs)
			{
				files.addAll(getFilesRecursive(child));
			}
		}
		files.add(f);

		return files;
	}
}
