package org.molgenis.util.plink;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Driver to query BED (binary Plink genotype) files. See:
 * http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml
 */
public class BedFileDriver
{
	private int mode;
	private long nrOfElements;
	private File bedFile;

	/**
	 * Get the mode: mode 1 = SNP-major, mode 2 = individual-major
	 * 
	 * @return
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * Get the number of retrievable genotype elements of this BED file. Does
	 * not account for trailing null elements because they are indistinguishable
	 * from null genotypes in this format alone.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Construct new convertGenoCoding on this file
	 * 
	 * @param bedFile
	 * @throws Exception
	 */
	public BedFileDriver(File bedFile) throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(bedFile, "r");
		this.bedFile = bedFile;

		byte mn1 = raf.readByte();
		byte mn2 = raf.readByte();

		if (mn1 == 108 && mn2 == 27) // tested, bit code 01101100 00011011
		{
			//System.out.println("Plink magic number valid");
		}
		else
		{
			throw new Exception("Invalid Plink magic number");
		}

		byte bmode = raf.readByte();

		if (bmode == 1) // tested, bit code 00000001
		{
			//System.out.println("mode 1: SNP-major");
		}
		else if (bmode == 0) // assumed... bit code 00000000
		{
			//System.out.println("mode 0: individual-major");
		}
		else
		{
			throw new Exception("Mode not recognized: " + mode);
		}

		this.mode = bmode;
		this.nrOfElements = (raf.length() - 3) * 4;

		raf.close();
	}

	/**
	 * Convert bit coding in custom genotype coding.
	 * 
	 * @param in
	 * @param hom1
	 * @param hom2
	 * @param het
	 * @param _null
	 * @return
	 * @throws Exception
	 */
	public String convertGenoCoding(String in, String hom1, String hom2,
			String het, String _null) throws Exception
	{
		if (in.equals("00"))
		{
			return hom1;
		}
		if (in.equals("01"))
		{
			return het;
		}
		if (in.equals("11"))
		{
			return hom2;
		}
		if (in.equals("10"))
		{
			return _null;
		}
		throw new Exception("Input '" + in + "' not recognized");
	}

	/**
	 * Convert bit coding in common genotype signs: A & B for homozygotes, H for
	 * heterozygote, N for null.
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public String genoCodingCommon(String in) throws Exception
	{
		return convertGenoCoding(in, "A", "B", "H", "N");
	}

	/**
	 * Get a single element from the BED file
	 * 
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public String getElement(long index) throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(bedFile, "r");
		raf.seek((index / 4) + 3);
		String byteString = reverse(bits(raf.readByte()));
		raf.close();
		int bitpair = (int) (index % 4) * 2;
		return byteString.substring(bitpair, bitpair + 2);
	}

	/**
	 * Get a String[] of elements from the BED file.
	 * 
	 * from = inclusive to = exclusive
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public String[] getElements(long from, long to) throws IOException
	{
		long start = (from / 4) + 3;
		long stop = (to % 4) != 0 ? (to / 4) + 3 : (to / 4) + 2;
		byte[] res = new byte[(int) (stop - start) + 1];
		int res_index = 0;
		String[] result = new String[(int) (to - from)];

		RandomAccessFile raf = new RandomAccessFile(bedFile, "r");
		raf.seek((from / 4) + 3);
		raf.read(res);
		raf.close();

		for (int i = 0; i < res.length; i++)
		{
			byte b = res[i];
			String byteString = reverse(bits(b));

			int fromBitpair;
			if (i == 0)
			{
				fromBitpair = (int) (from % 4) * 2;
			}
			else
			{
				fromBitpair = 0;
			}

			int toBitpair;

			if (i == res.length - 1)
			{
				toBitpair = (to % 4) != 0 ? (int) (to % 4) * 2 : 8;
			}
			else
			{
				toBitpair = 8;
			}

			for (int pair = fromBitpair; pair < toBitpair; pair += 2)
			{
				String bla = byteString.substring(pair, pair + 2);
				result[res_index] = bla;
				res_index++;
			}
		}
		return result;
	}

	/**
	 * Helper function to get the bit values
	 * 
	 * @param b
	 * @return
	 */
	private String bits(byte b)
	{
		String bits = "";
		for (int bit = 7; bit >= 0; --bit)
		{
			bits = bits + ((b >>> bit) & 1);
		}
		return bits;
	}

	/**
	 * Helper function to reverse a string
	 * 
	 * @param string
	 * @return
	 */
	private String reverse(String string)
	{
		return new StringBuffer(string).reverse().toString();
	}

}
