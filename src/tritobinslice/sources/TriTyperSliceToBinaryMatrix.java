package tritobinslice.sources;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import tritobin.sources.SNP;
import tritobin.sources.SNPLoader;
import tritobin.sources.TriTyperGenotypeData;


public class TriTyperSliceToBinaryMatrix
{
	private String nullChar = "\5";
	
	private TriTyperGenotypeData data;
	private File dest;
	private File slice;
	private String[] individuals;
	private String[] markers;
	
	//the subselection (indices) of String[] individuals
	private int[] individualsToBeSelected;

	public TriTyperSliceToBinaryMatrix(TriTyperGenotypeData data, File dest, File slice) throws Exception
	{
		this.data = data;
		this.dest = dest;
		this.slice = slice;
		this.individuals = data.getIndividuals();
		this.markers = data.getSNPs();
		
		//read individuals from slice.txt and match against individuals in TriTyper
		ArrayList<String> individualsSlice = readSliceFile(slice);
		individualsToBeSelected = new int[individualsSlice.size()];
		
		int selectIndex = 0;
		for (int i = 0; i < individuals.length; i++)
		{
			if(individualsSlice.contains(individuals[i]))
			{
				individualsToBeSelected[selectIndex] = i;
				selectIndex++;
				individualsSlice.remove(individuals[i]);
			}
		}
		
		//check if all individuals in the slice file were selected and indexed
		if(individualsSlice.size() > 0)
		{
			System.err.println("Not matched:");
			for(String s : individualsSlice)
			{
				System.err.println(s);
			}
			throw new Exception("ERROR: Not all individuals in your slice file were matched. See above.");
		}
		
	}
	
	private ArrayList<String> readSliceFile(File f) throws FileNotFoundException
	{
		Scanner s = new Scanner(f);
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
		    list.add(s.next());
		}
		s.close();
		return list;
	}

	public File makeBinaryBackend(String name, String investigation) throws Exception
	{
		if (dest.exists())
		{
			throw new IOException("Destination file '" + dest.getName() + "' already exists");
		}

		FileOutputStream fos = new FileOutputStream(dest);
		DataOutputStream dos = new DataOutputStream(fos);

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

		dos.writeInt(individualsToBeSelected.length); // #columns
		dos.writeInt(markers.length); // #rows

		// write col/row headers
		for (int i = 0; i < individualsToBeSelected.length; i++)
		{
			int index = individualsToBeSelected[i];
			dos.writeByte(individuals[index].length());
		}

		for (int i = 0; i < markers.length; i++)
		{
			dos.writeByte(markers[i].length());
		}

		for (int i = 0; i < individualsToBeSelected.length; i++)
		{
			int index = individualsToBeSelected[i];
			dos.writeBytes(individuals[index]);
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

	private void writeBinary(DataOutputStream dos, TriTyperGenotypeData data) throws ParseException, IOException
	{
		SNPLoader loader = data.createSNPLoader();
		for (int m = 0; m < markers.length; m++)
		{
			SNP snpObject = data.getSNPObject(m);
			loader.loadGenotypes(snpObject);
			
			byte[] all1 = snpObject.getAllele1();
			byte[] all2 = snpObject.getAllele2();
			
			for (int i = 0; i < individualsToBeSelected.length; i++)
			{
				int index = individualsToBeSelected[i];
				dos.write(all1[index]);
				dos.write(all2[index]);
			}
		}
	}

}
