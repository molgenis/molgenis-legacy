package tmp;

import java.io.File;
import java.util.ArrayList;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

public class ParserForRoan
{

	public ParserForRoan(String location) throws Exception
	{
		CsvFileReader reader = new CsvFileReader(new File(location));
		
		final ArrayList<String> allIndv = new ArrayList<String>();
		final ArrayList<String> allParents = new ArrayList<String>();
		
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple)
					throws Exception
			{
				String indv = tuple.getString(0);
				String dad = tuple.getString(3);
				String mom = tuple.getString(4);
				//System.out.println(indv+", " +dad+ ", "+mom);
				allIndv.add(indv);
				if(dad != null) allParents.add(dad);
				if(mom != null ) allParents.add(mom);
			}
		});
		
		for(String parent : allParents)
		{
			if(!allIndv.contains(parent)){
				System.out.println("Missing parent: " + parent);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception
	{
		new ParserForRoan("/Users/joerivandervelde/Downloads/selection_celiacsprue.csv");

	}

}
