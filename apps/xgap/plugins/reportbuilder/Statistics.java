package plugins.reportbuilder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import matrix.DataMatrixInstance;

import org.apache.commons.math.stat.correlation.SpearmansCorrelation;

public class Statistics
{

	public static TreeMap<String, Double> getSpearManCorr(DataMatrixInstance i, String name, boolean isRow)
			throws Exception
	{
		HashMap<String, Double> map = new HashMap<String, Double>();
		ValueComparator bvc = new ValueComparator(map);
		boolean decimals = i.getData().getValueType().equals("Decimal") ? true : false;

		// could do per row, but is slower? dont do on big sets anyway..
		Object[][] elements = i.getElements();

		List<String> dimNames;
		int self = -1;

		if (isRow)
		{
			dimNames = i.getRowNames();
			self = i.getRowIndexForName(name);
		}
		else
		{
			// reassign
			dimNames = i.getColNames();
			self = i.getColIndexForName(name);

			// swap row and col, easier to iterate
			elements = transposeMatrix(elements);
		}
		
		double[] selfDoubles;
		if (decimals)
		{
			selfDoubles = getAsDoubles(elements[self]);
		}
		else
		{
			selfDoubles = getTextAsDoubles(elements[self]);
		}
		
		
		for (int index = 0; index < i.getNumberOfRows(); index++)
		{
			if (index != self)
			{
				double[] doubles;
				
				if (decimals)
				{
					doubles = getAsDoubles(elements[index]);
				}
				else
				{
					doubles = getTextAsDoubles(elements[index]);
				}
				double corr = new SpearmansCorrelation().correlation(doubles, selfDoubles);
				map.put(dimNames.get(index), corr);
			}
		}

		TreeMap<String, Double> sorted_map = new TreeMap(bvc);
		sorted_map.putAll(map);
		
		for (String key : sorted_map.keySet())
		{
			System.out.println("k: " + key + ", v: " + sorted_map.get(key));
		}

		return sorted_map;
	}

	public static Object[][] transposeMatrix(Object[][] m)
	{
		int r = m.length;
		int c = m[0].length;
		Object[][] t = new Object[c][r];
		for (int i = 0; i < r; ++i)
		{
			for (int j = 0; j < c; ++j)
			{
				t[j][i] = m[i][j];
			}
		}
		return t;
	}

	public static double[] getAsDoubles(Object[] e)
	{
		double[] res = new double[e.length];
		for (int i = 0; i < e.length; i++)
		{
			if (e[i] == null)
			{
				res[i] = 0;
			}
			else
			{
				res[i] = Double.parseDouble(e[i].toString());
			}

		}
		return res;
	}

	public static double[] getTextAsDoubles(Object[] e)
	{
		double[] res = new double[e.length];
		for (int i = 0; i < e.length; i++)
		{
			if (e[i] == null)
			{
				res[i] = 0;
			}
			else
			{
				double r = 0;
				for (char c : e[i].toString().toCharArray())
				{
					r += (byte) c;
				}

				res[i] = r;
			}

		}
		return res;
	}

	static class ValueComparator implements Comparator
	{

		Map base;

		public ValueComparator(Map base)
		{
			this.base = base;
		}

		public int compare(Object a, Object b)
		{

			if ((Double) base.get(a) < (Double) base.get(b))
			{
				return 1;
			}
			else if ((Double) base.get(a) == (Double) base.get(b))
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
	}

}
