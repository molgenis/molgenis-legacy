package org.molgenis.compute.commandline;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.compute.ComputeParameter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Worksheet
{
	// The worksheet variable
	public List<Tuple> worksheet = new ArrayList<Tuple>();
	public List<Tuple> folded = new ArrayList<Tuple>();
	public List<Tuple> reduced = new ArrayList<Tuple>();
	List<ComputeParameter> parameterlist; // parameters.txt
	List<Tuple> userworksheet; // original user worksheet
	public Set<String> reducedfields; // fields (lists) that are reduced to a single value
	public Set<String> foldon = new HashSet<String>(); // fields on which we folded
	public Set<String> list; // fields that remain a list

	public Set<String> getConstants()
	{
		Set<String> constants = new HashSet<String>();
		for (String field : reducedfields)
		{
			if (!foldon.contains(field))
			{
				constants.add(field);
			}
		}

		return (constants);
	}

	// map with (parameter name, parameter object) tuples
	// public Map<String, ComputeParameter> computeparameters = new HashMap<String, ComputeParameter>();

	public Worksheet(ComputeBundle computebundle)
	{
		// set parameter list
		this.parameterlist = computebundle.getComputeParameters(); // parameters.txt
		this.userworksheet = computebundle.getUserParameters(); // original user worksheet

		// set compute parameters
		// for (ComputeParameter cp : this.parameterlist)
		// {
		// this.computeparameters.put(cp.getName(), cp);
		// }

		// fillConstants();

		// iteratively replace variables by values
		fillWorksheet();

		// print("Filled worksheet: " + this.worksheet);
		// throw new RuntimeException("BREAK!");
	}

	/*
	 * private void fillConstants() { boolean foundMore = true; while (foundMore) { foundMore = false; for (ComputeParameter cp : parameterlist) { // first set the hasOne if (cp.getDefaultValue() != null && cp.getDefaultValue().contains("${")) { for (ComputeParameter otherCp : parameterlist) { //
	 * for constants we know: if (cp.getDefaultValue().contains("${" + otherCp.getName() + "}") && otherCp.getDefaultValue() != null && !otherCp.getDefaultValue().contains("${")) { // for constants, we know it works if (!cp.getHasOne_Name().contains(otherCp.getName())) {
	 * cp.setDefaultValue(cp.getDefaultValue().replace("${" + otherCp.getName() + "}", otherCp.getDefaultValue())); }
	 * 
	 * } } } } }
	 * 
	 * // pretty print // for (ComputeParameter cp : parameterlist) // { // System.out.println(cp.getName() + " defaultValue: '"+cp.getDefaultValue()+"', hasone: " + cp.getHasOne_Name()); // } // throw new RuntimeException("BREAK"); }
	 */
	private void fillWorksheet()
	{
		Map<String, String> parameters = new HashMap<String, String>();

		// novel worksheet that combines user worksheet with parameters
		List<Tuple> worksheet = new ArrayList<Tuple>();

		// fill worksheet and iteratively substitute values that point to parameters
		for (Tuple usertuple : userworksheet)
		{
			// first put all parameters/values in map
			parameters.clear();

			// add parameters.txt fields to parameters
			for (ComputeParameter cp : parameterlist)
			{
				String field = cp.getName();
				String value = cp.getDefaultValue();

				// only add if it not exists yet
				if (!parameters.containsKey(field))
				{
					parameters.put(cp.getName(), value == null ? "" : value);
				}
				else
				{
					// // check whether existing value is empty
					// // if not: error
					// if (value != null && value != "") {
					throw new RuntimeException("Parameter " + field + " occurs > 1 times in your parameter.txt file.");
					// }
				}
			}

			// add user worksheet values to wt, for this tuple
			for (String field : usertuple.getFields())
			{
				String value = usertuple.getString(field);
				parameters.put(field, value == null ? "" : value);
			}

			// iteratively substitute values that point to parameters
			String fieldtemplate, value, originalvalue;
			Template template;
			Configuration conf = new Configuration();
			StringWriter filledtemplate;

			boolean done = false;
			while (!done)
			{
				boolean updated = false;
				for (String field : parameters.keySet())
				{
					try
					{
						// do substitution for field
						originalvalue = parameters.get(field);
						fieldtemplate = new String(originalvalue == null ? "" : originalvalue);
						template = new Template(field, new StringReader(fieldtemplate), conf);
						filledtemplate = new StringWriter();
						template.process(parameters, filledtemplate);

						value = filledtemplate.toString();

						if (!value.equalsIgnoreCase(originalvalue))
						{
							// update value of field in parameter list
							parameters.put(field, value);
							updated = true;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				if (!updated) done = true; // nothing changed, so we're done
			}

			// all values in parameters for this usertupele + parameters.txt are now iteratively filled

			// put these parameters in worksheet tuple (wt)

			Tuple wt = new SimpleTuple();

			for (String field : parameters.keySet())
			{
				value = parameters.get(field);
				wt.set(field, value == null ? "" : value);
			}

			// add wt to worksheet
			worksheet.add(wt);
		}

		setWorksheet(worksheet);
	}

	private void setWorksheet(List<Tuple> ws)
	{
		this.worksheet = ws;
	}

	public void foldWorksheet(List<String> targets)
	{
		/*
		 * Fold worksheet based on targets. Example (targets = lane, sequencer): lane, barcode, sequencer (1, a, x); (1, b, x); (2, a, x)
		 * 
		 * Becomes: [1, 1], [a, b], [x, x] [2], [a], [x]
		 * 
		 * Use reduceTargets(worksheet, targets) to reduce the instances of the targets (for easy use in FTL templates): (1, [a, b], x); (2, [a], x)
		 */

//		if(0 == targets.size())
//		{
//			this.folded = this.cloneWorksheet(this.worksheet);
//			return;
//		}

		Map<String, ArrayList<Object>> tupleset = null; // [(Lane: 1,1,1), (Sample: a,b,c), (Flowcell: x,y,z)]
		Map<String, Map<String, ArrayList<Object>>> wsset = new HashMap<String, Map<String, ArrayList<Object>>>(); // Suppose target is lane: [1: [(Lane: 1,1,1), (Sample: a,b,c), (Flowcell: x,y,z)]]

		for (Tuple t : this.worksheet)
		{
			// fill ws

			String key = "";// t.getString(targets);

			for (String target : targets)
			{
				key += t.getString(target) + "_";
			}

			if (!key.equals("") && wsset != null && wsset.containsKey(key))
			{
				// we already have a tupleset corresponding to this key
				tupleset = wsset.get(key);
			}
			else
			{
				// intialize tupleset: each field gets empty List of objects
				tupleset = new HashMap<String, ArrayList<Object>>();
				for (String field : t.getFields())
				{
					tupleset.put(field, new ArrayList<Object>());
				}
			}

			// add values in tuple t to this tupleset
			for (String field : t.getFields())
			{
				ArrayList<Object> lst = tupleset.get(field);
				lst.add(t.getObject(field));
				tupleset.put(field, lst);
			}

			// put the updated tupleset wsset, for the given key (= unique combination of targets)
			wsset.put(key, tupleset);
		}

		// put folded tuples in 'folded worksheet'
		this.folded.clear();
		for (String key : wsset.keySet())
		{
			tupleset = wsset.get(key);

			// this is bizarre:
			Map<String, Object> m = new HashMap<String, Object>();
			for (String field : tupleset.keySet())
			{
				m.put(field, tupleset.get(field));
			}

			SimpleTuple st = new SimpleTuple(m);

			this.folded.add(st);
		}

		// check: is the worksheet (ws) that we want to return, after expansion, equal to the original worksheet?
		// if not, throw exception
		
		List<Tuple> expWs = expandWorksheet(this.folded);
		print("this.folded: " + this.folded);
		print("expWs: " + expWs);
		print("worksheet: " + worksheet);
		
		if (!equalWorksheets(expandWorksheet(this.folded), worksheet))
		{
			throw new RuntimeException(">> Error: worksheets should be equal but are not!");
		}
	}

	private List<Tuple> cloneWorksheet(List<Tuple> otherWorksheet)
	{
		List<Tuple> result = new ArrayList<Tuple>();
		for(Tuple t: worksheet)
		{
			result.add(this.cloneTuple(t));
		}

		return result;
	}

	private Set<String> reduceFieldSet(List<String> targets)
	{
//		if (targets.size() == 0)
//		{
//			Set<String> reduceParams = new HashSet<String>();
//			for (ComputeParameter cp : parameterlist)
//			{
//				reduceParams.add(cp.getName());
//			}			
//			this.list = new HashSet<String>();
//			return(reduceParams);
//		}

		// Let R and L be two sets. R contains 'reduce' parameters, L contains 'list' parameters.
		Set<String> reduceParams = new HashSet<String>();
		Set<String> listParams = new HashSet<String>();

		// (i) put targets in R
		for (String t : targets)
		{
			reduceParams.add(t);
		}

		// (ii) put all the target's (indirect) hasOnes in R
		boolean ready = false;
		while (!ready)
		{
			ready = true;
			for (ComputeParameter cp : parameterlist)
			{
				if (reduceParams.contains(cp.getName()))
				{
					// we want to reduce on cp, so we also want to reduce on its hasOnes
					// thus, add its hasOnes, but only if not yet present!
					for (String ho : cp.getHasOne_Name())
					{
						if (!reduceParams.contains(ho))
						{
							// we found a new reduce parameter
							reduceParams.add(ho);

							// more work to do
							ready = false;
						}
					}
				}
			}
		}

		// put targets and hasOnes also in global list that we print in header of script
		for (String field : reduceParams)
		{
			foldon.add(field);
		}

		// (iii) put all empty parameters that are not in R, in L
		for (ComputeParameter cp : parameterlist)
		{
			if (cp.getDefaultValue() == null && !reduceParams.contains(cp.getName()))
			{
				listParams.add(cp.getName());
			}
		}

		// (iv) put all not-null parameters that refer to a constant in R
		for (ComputeParameter cp : parameterlist)
		{
			if (cp.getDefaultValue() != null && !reduceParams.contains(cp.getName()))
			{
				if (!cp.getDefaultValue().contains("${"))
				{
					// cp must be a constant, so add it to R
					reduceParams.add(cp.getName());
				}
			}
		}

		// (v) for each parameter cp that is not in R and not in L, determine the 'set' of parameters it refers to. Put (cp, set) in map.
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (ComputeParameter cp : parameterlist)
		{
			if (!reduceParams.contains(cp.getName()) && !listParams.contains(cp.getName()))
			{
				Set<String> set = new HashSet<String>();
				for (ComputeParameter cp2 : parameterlist)
				{
					if (cp.getDefaultValue().contains("${" + cp2.getName() + "}"))
					{
						// cp refers to cp2
						set.add(cp2.getName());
					}
				}

				// put cp and its set of parameters in map
				map.put(cp.getName(), set);
			}
		}

		// (vi) iteratively determine for all parameters whether they go to R or L
		ready = false;
		while (!ready)
		{
			ready = true;

			for (String p : map.keySet())
			{
				boolean pinR = true;

				for (String p2 : map.get(p))
				{
					if (listParams.contains(p2))
					{
						pinR = false;
						listParams.add(p);
						map.remove(p);
						ready = false;
						break;
					}
					else if (!reduceParams.contains(p2))
					{
						// p2 is not in "R u L", so its destination is unkown, yet
						pinR = false;
						break;
					}
				}

				if (!ready) break;

				if (pinR)
				{
					// p should be element R
					reduceParams.add(p);
					map.remove(p);
					ready = false;
					break;
				}

			}

		}

		// (vii) check: are all parameters in "R u L"?
		// print("R: " + R);
		// print("L: " + L);
		Set<String> allp = new HashSet<String>();
		for (ComputeParameter cp : parameterlist)
		{
			allp.add(cp.getName());
		}

		print("R: " + reduceParams);
		print("L: " + listParams);
		print("allp: " + allp);

		if (!allp.containsAll(reduceParams) || !allp.containsAll(listParams)) throw new RuntimeException("You just found a bug!");
		allp.removeAll(reduceParams);
		allp.removeAll(listParams);
		if (!allp.isEmpty()) throw new RuntimeException(
				"You just found a bug! There are parameters for which it is unclear whether you want to reduce on them.");

		// make L global
		this.list = listParams;
		return (reduceParams);
	}

	public void reduceTargets(List<String> targets)
	{
		if(1 == targets.size() && "line_number".equals(targets.get(0))) 
		{
			this.reduced = cloneWorksheet(this.folded);
			this.reducedfields.clear();
			for (ComputeParameter cp : parameterlist)
			{
				this.reducedfields.add(cp.getName());
			}
			return;
		}
		
		// reduce the targets in worksheet (eg lane = 1, 1, 1, 1) to one single value (lane = 1) for easy use in freemarker

		this.reducedfields = reduceFieldSet(targets);

		// clear data that is now in reduced worksheet
		this.reduced.clear();

		for (Tuple t : folded)
		{
			Tuple tclone = cloneTuple(t);

			for (String rfield : reducedfields)
			{
				if (!tclone.getFields().contains(rfield))
				{
					throw new RuntimeException("Field: " + rfield + " is not known!");
				}
				else
				{
					if (!tclone.isNull(rfield))
					{ // tclone has a value, which should be a list
						@SuppressWarnings("unchecked")
						List<String> ls = (List<String>) tclone.getList(rfield);

						// check: all values should be equal
						String value = ls.get(0);
						for (int i = 1; i < ls.size(); i++)
						{
							if (!ls.get(i).equalsIgnoreCase(value))
							{
								throw new RuntimeException("Cannot reduce field " + rfield + " because it contains different values!");
							}
						}

						// reduce to one value
						tclone.set(rfield, value);
					}

				}
			}
			this.reduced.add(tclone);
			// for (String target : targets)
			// {
			// // get the 'hasOne' list
			// ComputeParameter cp = this.computeparameters.get(target);
			// List<String> hasOne_names = cp.getHasOne_Name();
			// hasOne_names.add(target);
			//
			// if (!tclone.getFields().contains(target))
			// {
			// throw new RuntimeException("target: " + target + " is not known");
			// }
			// else
			// {
			// for (String hasOne : hasOne_names)
			// {
			// if (!tclone.isNull(hasOne))
			// { // tclone has a value
			// List<String> ls = (List<String>) tclone.getList(hasOne);
			//
			// // check: all values should be equal
			// String value = ls.get(0);
			// for (int i = 1; i < ls.size(); i++)
			// {
			// if (!ls.get(i).equalsIgnoreCase(value))
			// {
			// throw new RuntimeException("Cannot reduce field " + hasOne
			// + " because it contains different values!");
			// }
			// }
			//
			// // reduce to one value
			// tclone.set(hasOne, value);
			// }
			// }
			// }
			// }
			// this.reduced.add(tclone);
		}
	}

	private Tuple cloneTuple(Tuple t)
	{
		Tuple tclone = new SimpleTuple();

		for (String field : t.getFields())
		{
			if (t.getObject(field) instanceof List)
			{
				List<String> clone = new ArrayList<String>();
				clone.addAll((Collection<? extends String>) t.getList(field));
				tclone.set(field, clone);
			}
			else
			{
				tclone.set(field, t.getString(field));
			}
		}

		return tclone;
	}

	public static List<Tuple> expandWorksheet(List<Tuple> worksheet)
	{
		// Remark: works on 'non-reduced' worksheets

		List<Tuple> ws = new ArrayList<Tuple>();

		for (Tuple t : worksheet)
		{
			int nelements = t.getList(t.getFields().get(0)).size();

			List<String> fields = t.getFields();
			for (int i = 0; i < nelements; i++)
			{
				Tuple st = new SimpleTuple();

				// fill this new tuple, based on i'th elements in the tupleset
				for (String field : fields)
				{
					st.set(field, t.getList(field).get(i));
				}

				ws.add(st);
			}
		}
		return ws;
	}

	private static boolean equalTuples(Tuple t1, Tuple t2)
	{

		List<String> fields1 = t1.getFields();
		List<String> fields2 = t2.getFields();

		// is number of fields equal?
		if (fields1.size() != fields2.size())
		{
			return false;
		}

		for (String field : fields1)
		{
			// does t2 also contain this field
			if (!fields2.contains(field)) return false;

			// is content equal for this field?
			Object o1 = t1.getObject(field);
			Object o2 = t2.getObject(field);

			if (!(o1 == null && o2 == null))
			{ // if not both o1 and o2 are null
				if (o1 == null || o2 == null)
				{ // if one of the two is null, then return false
					return false;
				}
				// both o1 and o2 are not null

				if (!o1.equals(o2))
				{ // if value of o1 is different from value of o2 then return false
					return false;
				}
			}
		}

		return true;
	}

	private static boolean equalWorksheets(List<Tuple> ws1, List<Tuple> ws2)
	{
		// if each tuple in ws1 matches a different tuple in ws2
		// and each tuple in ws2 is matched to a tuple in ws1,
		// then two worksheets are considered equal

		// create a list with all indices of tuples in ws2
		List<Integer> li = new ArrayList<Integer>();
		for (int i = 0; i < ws2.size(); i++)
		{
			li.add(i);
		}

		// for each tuple in ws1, find a matching tuple in ws2 and remove the 'matching index' from li
		for (Tuple t1 : ws1)
		{

			boolean match = false;
			for (int index = 0; index < li.size(); index++)
			{
				if (equalTuples(t1, ws2.get(li.get(index))))
				{
					match = true; // match found!
					li.remove(index); // remove tuple i from list li
					break;
				}
			}

			if (!match) return (false);
		}

		// list li should be empty now!
		if (0 < li.size()) return (false);

		return true;
	}

	private void print(String string)
	{
		System.out.println(">> " + string);
	}

	/** Find all tuples in unfolded worksheet that have matching target values as the foldedTuple */
	public void getUnfolded(String[] targets, Tuple foldedTuple)
	{
		// for each unfolded tuple we check if it matches folded tuple on the selected targets
		// then we will set column 'name' to 'value'
		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple unfoldedTuple : this.worksheet)
		{
			// we assume match, unless we find a target that is not equal
			boolean matchAllTargets = true;
			// compare values on each target, if unequal we change match to false
			for (String target : targets)
			{
				// ignore if not all targets match
				if (!unfoldedTuple.getObject(target).equals(foldedTuple.getObject(target)))
				{
					matchAllTargets = false;
				}
			}
			// if all fields have matched
			if (matchAllTargets)
			{
				result.add(unfoldedTuple);
			}
		}

	}
}
