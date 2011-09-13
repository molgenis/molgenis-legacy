package matrix.implementations.binary.etc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import org.molgenis.util.trityper.reader.SNP;
import org.molgenis.util.trityper.reader.SNPLoader;
import org.molgenis.util.trityper.reader.TriTyperGenotypeData;

public class TriTyperToBinaryMatrix
{
	private String nullChar = "\5";
	
	private TriTyperGenotypeData data;
	private File dest;
	private String[] individuals;
	private String[] markers;

	public TriTyperToBinaryMatrix(TriTyperGenotypeData data, File dest)
	{
		this.data = data;
		this.dest = dest;
		this.individuals = data.getIndividuals();
		this.markers = data.getSNPs();
	}

	public File makeBinaryBackend(String name, String investigation) throws Exception
	{
		if (dest.exists())
		{
			throw new IOException("Destination file '" + dest.getName() + "' already exists");
		}

		FileOutputStream fos = new FileOutputStream(dest);
		final DataOutputStream dos = new DataOutputStream(fos);

		// 0) write nullCharacter
		dos.writeBytes(this.nullChar);

		// 1) properties belonging to the 'Data' object
		dos.writeByte(name.length()); // name
		dos.writeBytes(name);
		dos.writeByte(investigation.length()); // inv
		dos.writeBytes(investigation);
		dos.writeByte("Individual".length()); // columns
		dos.writeBytes("Individual");
		dos.writeByte("Marker".length()); // rows
		dos.writeBytes("Marker");
		dos.writeBoolean(false); // true for decimal, false for text

		dos.writeInt(individuals.length); // #columns
		dos.writeInt(markers.length); // #rows

		// write col/row headers
		for (int i = 0; i < individuals.length; i++)
		{
			dos.writeByte(individuals[i].length());
		}

		for (int i = 0; i < markers.length; i++)
		{
			dos.writeByte(markers[i].length());
		}

		for (int i = 0; i < individuals.length; i++)
		{
			dos.writeBytes(individuals[i]);
		}

		for (int i = 0; i < markers.length; i++)
		{
			dos.writeBytes(markers[i]);
		}

		// snp length 2
		dos.writeByte(2);

		writeBinary(dos, data);

		return dest;
	}

	private void writeBinary(final DataOutputStream dos, TriTyperGenotypeData data) throws ParseException, IOException
	{
		SNPLoader loader = data.createSNPLoader();
		for (int m = 0; m < markers.length; m++)
		{
			SNP snpObject = data.getSNPObject(m);
			loader.loadGenotypes(snpObject);
			
			byte[] all1 = snpObject.getAllele1();
			byte[] all2 = snpObject.getAllele2();
			
			for (int i = 0; i < individuals.length; i++)
			{
				String allele = new String(new char[]{(char)all1[i], (char)all2[i]});
				dos.writeBytes(allele);
			}
		}
	}

}
