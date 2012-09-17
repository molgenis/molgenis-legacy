package org.molgenis.mutation.util;

public class StringUtils
{
	public static String sentenceize(String s)
	{
		return
		org.apache.commons.lang.StringUtils.capitalize(
				org.apache.commons.lang.StringUtils.join(
						org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(s), " "
				)
		);
	}
}
