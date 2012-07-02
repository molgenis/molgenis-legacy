package external;

import org.molgenis.MolgenisOptions;
import org.molgenis.model.JDBCModelExtractor;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.MolgenisModelParser;
import org.molgenis.model.elements.Model;


public class ExtractPsiModel
{
	public static void main(String[] args) throws MolgenisModelException
	{
		MolgenisOptions options = new MolgenisOptions();
		
		options.db_driver = "com.mysql.jdbc.Driver";
		options.db_user = "molgenis";
		options.db_password = "molgenis";
		options.db_uri = "jdbc:mysql://localhost/psi";
		
		String xml = JDBCModelExtractor.extractXml(options) ;
		
		System.out.println( xml );
		
		Model m = MolgenisModelParser.parseDbSchema(xml);
		
		String tab = ModelToExcel.write(m);
		
		System.out.println( tab );
	}
	

}
