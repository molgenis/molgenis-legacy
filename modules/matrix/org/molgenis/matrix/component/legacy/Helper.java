package org.molgenis.matrix.component.legacy;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.matrix.component.legacy.SomeColType;

public class Helper
{
	public static List<SomeColType> getSomeColumns(){
		List<SomeColType> result = new ArrayList<SomeColType>();
		result.add(new SomeColType(1, "MYB28", "nc23435", "65721", 324546, "3"));
		result.add(new SomeColType(2, "HOX3", "nc768783", "76834", 78564, "X"));
		result.add(new SomeColType(3, "CCR1", "nc224577", "65743", 756879, "Y"));
		result.add(new SomeColType(4, "SRF2", "nc874636", "56722", 624546, "X"));
		result.add(new SomeColType(5, "AOP2", "nc916763", "89764", 14572, "3"));
		result.add(new SomeColType(6, "COL7A1", "nc169485", "78623", 397647, "5"));
		result.add(new SomeColType(7, "RAS", "nc7485", "452737", 854315, "2"));
		return result;
	}
	
	public static List<SomeRowType> getSomeRows(){
		List<SomeRowType> result = new ArrayList<SomeRowType>();
		result.add(new SomeRowType(56, "Laura", "Roslin", "Amsterdam", 1968));
		result.add(new SomeRowType(57, "Karl", "Agathon", "Rotterdam", 1972));
		result.add(new SomeRowType(58, "Bill", "Adama", "Rotterdam", 1987));
		result.add(new SomeRowType(59, "Lee", "Adama", "Amsterdam", 1995));
		result.add(new SomeRowType(60, "Kara", "Thrace", "Delft", 1905));
		result.add(new SomeRowType(61, "Gaius", "Baltar", "Delft", 1954));
		result.add(new SomeRowType(62, "Saul", "Tigh", "Amsterdam", 1912));
		result.add(new SomeRowType(63, "Sharon", "Agathon", "Delft", 1923));
		
		return result;
	}
}
