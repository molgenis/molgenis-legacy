package org.molgenis.util.plink.drivers;

public class Helper
{
	public static String errorMsg(int line, int col)
	{
		return "Empty value encountered at line "+line+", column "+col+". Please check your file and remove repeating separators, such as double whitespaces between values.";
	}
}
