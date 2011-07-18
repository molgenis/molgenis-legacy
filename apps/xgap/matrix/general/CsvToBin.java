package matrix.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.zip.DataFormatException;

import javax.naming.NamingException;

import org.molgenis.framework.db.DatabaseException;

import matrix.implementations.binary.BinaryDataMatrixWriter;

public class CsvToBin
{

	/**
	 * @param args
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		BinaryDataMatrixWriter b = new BinaryDataMatrixWriter();
		b.CsvToBin(args);
	}
}
