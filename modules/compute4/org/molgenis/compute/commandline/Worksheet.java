package org.molgenis.compute.commandline;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Worksheet {
	// The worksheet variable
	public List<Tuple> worksheet = new ArrayList<Tuple>();
	// public List<Tuple> folded = new ArrayList<Tuple>();
	// public List<Tuple> reduced = new ArrayList<Tuple>();
	List<ComputeParameter> parameterlist; // parameters.txt
	List<Tuple> userworksheet; // original user worksheet

	// public Set<String> reducedfields = new HashSet<String>(); // fields
	// (lists) that are reduced to a single value
	// public Set<String> foldon = new HashSet<String>(); // fields on which we
	// folded
	// public Set<String> list; // fields that remain a list

	// public Set<String> getConstants()
	// {
	// Set<String> constants = new HashSet<String>();
	// for (String field : reducedfields)
	// {
	// if (!foldon.contains(field))
	// {
	// constants.add(field);
	// }
	// }
	//
	// return (constants);
	// }

	// map with (parameter name, parameter object) tuples
	// public Map<String, ComputeParameter> computeparameters = new
	// HashMap<String, ComputeParameter>();

	public Worksheet(ComputeBundle computebundle) {
		// set parameter list
		this.parameterlist = computebundle.getComputeParameters(); // parameters.txt
		this.userworksheet = computebundle.getUserParameters(); // original user
																// worksheet

		fillWorksheet();

	}

	/**
	 * Construct worksheet
	 * 
	 * @param parameterList
	 *            originating from parameters.txt or from database
	 * @param worksheet
	 */
	public Worksheet(List<ComputeParameter> parameterList, List<Tuple> worksheet) {
		// set parameter list
		this.parameterlist = parameterList; // parameters.txt
		this.userworksheet = worksheet; // original user worksheet

		fillWorksheet();
	}

	private void fillWorksheet() {
		Map<String, String> parameters = new HashMap<String, String>();

		// novel worksheet that combines user worksheet with parameters
		List<Tuple> worksheet = new ArrayList<Tuple>();

		// fill worksheet and iteratively substitute values that point to
		// parameters
		for (Tuple usertuple : userworksheet) {
			// first put all parameters/values in map
			parameters.clear();

			// add parameters.txt fields to parameters
			for (ComputeParameter cp : parameterlist) {
				String field = cp.getName();
				String value = cp.getDefaultValue();

				// only add if it not exists yet
				if (!parameters.containsKey(field)) {
					parameters.put(cp.getName(), value == null ? "" : value);
				} else {
					// // check whether existing value is empty
					// // if not: error
					// if (value != null && value != "") {
					throw new RuntimeException("Parameter " + field + " occurs > 1 times in your parameter.txt file.");
					// }
				}
			}

			// add user worksheet values to wt, for this tuple
			for (String field : usertuple.getFields()) {
				String value = usertuple.getString(field);
				parameters.put(field, value == null ? "" : value);
			}

			// iteratively substitute values that point to parameters
			String fieldtemplate, value, originalvalue;
			Template template;
			Configuration conf = new Configuration();
			StringWriter filledtemplate;

			boolean done = false;
			while (!done) {
				boolean updated = false;
				for (String field : parameters.keySet()) {
					try {
						// do substitution for field
						originalvalue = parameters.get(field);
						fieldtemplate = new String(originalvalue == null ? "" : originalvalue);
						template = new Template(field, new StringReader(fieldtemplate), conf);
						filledtemplate = new StringWriter();
						template.process(parameters, filledtemplate);

						value = filledtemplate.toString();

						if (!value.equalsIgnoreCase(originalvalue)) {
							// update value of field in parameter list
							parameters.put(field, value);
							updated = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (!updated)
					done = true; // nothing changed, so we're done
			}

			// all values in parameters for this usertupele + parameters.txt are
			// now iteratively filled

			// put these parameters in worksheet tuple (wt)

			Tuple wt = new SimpleTuple();

			for (String field : parameters.keySet()) {
				value = parameters.get(field);
				wt.set(field, value == null ? "" : value);
			}

			// add wt to worksheet
			worksheet.add(wt);
		}

		setWorksheet(worksheet);
	}

	private void setWorksheet(List<Tuple> ws) {
		this.worksheet = ws;
	}

	/** Returns the folded list, based on folding targets */
	public static List<Tuple> foldWorksheet(List<Tuple> worksheet, List<ComputeParameter> parameterlist, List<String> targets) {
		/*
		 * Fold worksheet based on targets. Example (targets = lane, sequencer
		 * {because lane hasOne sequencer}): lane, barcode, sequencer (1, a, x);
		 * (1, b, x); (2, a, x)
		 * 
		 * Becomes: [1, 1], [a, b], [x, x]; [2], [a], [x]
		 * 
		 * Use reduceTargets(worksheet, targets) to reduce the instances of the
		 * targets (for easy use in FTL templates): (1, [a, b], x); (2, [a], x)
		 */

		Map<String, ArrayList<Object>> tupleset = null; // [(Lane: 1,1,1),
														// (Sample: a,b,c),
														// (Flowcell: x,y,z)]
		Map<String, Map<String, ArrayList<Object>>> wsset = new HashMap<String, Map<String, ArrayList<Object>>>(); // Suppose
																													// target
																													// is
																													// lane:
																													// [1:
																													// [(lane:
																													// 1,1,1),
																													// (sample:
																													// a,b,c),
																													// (flowcell:
																													// x,y,z)]]

		for (Tuple t : worksheet) {
			// fill ws

			String key = "";// t.getString(targets);

			// create unique key based on concat of folding targets
			for (String target : targets) {
				key += t.getString(target) + "_";
			}

			// get existing folding set or create new
			if (!key.equals("") && wsset != null && wsset.containsKey(key)) {
				// we already have a tupleset corresponding to this key
				tupleset = wsset.get(key);
			} else {
				// Initialize tupleset: each field gets empty List of objects
				tupleset = new HashMap<String, ArrayList<Object>>();
				for (String field : t.getFields()) {
					tupleset.put(field, new ArrayList<Object>());
				}
			}

			// add values in tuple t to this tupleset
			for (String field : t.getFields()) {
				ArrayList<Object> lst = tupleset.get(field);
				lst.add(t.getObject(field));
				tupleset.put(field, lst);
			}

			// put the updated tupleset wsset, for the given key (= unique
			// combination of targets)
			wsset.put(key, tupleset);
		}

		// put folded tuples in 'folded worksheet'
		List<Tuple> folded = new ArrayList<Tuple>();
		for (String key : wsset.keySet()) {
			tupleset = wsset.get(key);

			// this is bizarre:
			Map<String, Object> m = new HashMap<String, Object>();
			for (String field : tupleset.keySet()) {
				m.put(field, tupleset.get(field));
			}

			SimpleTuple st = new SimpleTuple(m);

			folded.add(st);
		}

		// check: is the worksheet (ws) that we want to return, after expansion,
		// equal to the original worksheet?
		// if not, throw exception

		System.out.println(">> original:");
		System.out.println(">> " + worksheet);
		System.out.println(">> folded:");
		System.out.println(">> " + folded);
		System.out.println(">> unfolded:");
		System.out.println(">> " + unfoldWorksheet(folded));

		if (!equalWorksheets(unfoldWorksheet(folded), worksheet)) {
			throw new RuntimeException(">> Error: folded and unfolded worksheets should be equal but are not!");
		}

		// reduce the folded worksheet (i.e., reduce list to single value where
		// 'allowed')
		return reduceTargets(folded, parameterlist, targets);
	}

	private static Set<String> reduceFieldSet(List<ComputeParameter> parameterlist, List<String> targets) {
		// Let R and L be two sets. R contains 'reduce' parameters, L contains
		// 'list' parameters.
		Set<String> reduceParams = new HashSet<String>();
		Set<String> listParams = new HashSet<String>();

		// (i) put targets in R
		for (String t : targets) {
			reduceParams.add(t);
		}

		// (ii) put all the target's (indirect) hasOnes in R
		boolean ready = false;
		while (!ready) {
			ready = true;
			for (ComputeParameter cp : parameterlist) {
				if (reduceParams.contains(cp.getName())) {
					// we want to reduce on cp, so we also want to reduce on its
					// hasOnes
					// thus, add its hasOnes, but only if not yet present!
					for (String ho : cp.getHasOne_Name()) {
						if (!reduceParams.contains(ho)) {
							// we found a new reduce parameter
							reduceParams.add(ho);

							// more work to do
							ready = false;
						}
					}
				}
			}
		}

		// (iii) put all parameters that are null and are not in R, in L
		for (ComputeParameter cp : parameterlist) {
			if (cp.getDefaultValue() == null && !reduceParams.contains(cp.getName())) {
				listParams.add(cp.getName());
			}
		}

		// (iv) put all not-null parameters that refer to a constant in R
		for (ComputeParameter cp : parameterlist) {
			if (cp.getDefaultValue() != null && !reduceParams.contains(cp.getName())) {
				if (!cp.getDefaultValue().contains("${")) {
					// cp must be a constant, so add it to R
					reduceParams.add(cp.getName());
				}
			}
		}

		// (v) for each parameter cp that is not in R and not in L, determine
		// the 'set' of parameters it refers to. Put (cp, set) in map.
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for (ComputeParameter cp : parameterlist) {
			if (!reduceParams.contains(cp.getName()) && !listParams.contains(cp.getName())) {
				Set<String> set = new HashSet<String>();
				for (ComputeParameter cp2 : parameterlist) {
					if (cp.getDefaultValue().contains("${" + cp2.getName() + "}")) {
						// cp refers to cp2
						set.add(cp2.getName());
					}
				}

				// put cp and its set of parameters in map
				map.put(cp.getName(), set);
			}
		}

		// (vi) iteratively determine for all parameters whether they go to R or
		// L
		ready = false;
		while (!ready) {
			ready = true;

			for (String p : map.keySet()) {
				boolean pinR = true;

				for (String p2 : map.get(p)) {
					if (listParams.contains(p2)) {
						pinR = false;
						listParams.add(p);
						map.remove(p);
						ready = false;
						break;
					} else if (!reduceParams.contains(p2)) {
						// p2 is not in "R u L", so its destination is unkown,
						// yet
						pinR = false;
						break;
					}
				}

				if (!ready)
					break;

				if (pinR) {
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
		for (ComputeParameter cp : parameterlist) {
			allp.add(cp.getName());
		}

		// print("R: " + reduceParams);
		// print("L: " + listParams);
		// print("allp: " + allp);

		if (!allp.containsAll(reduceParams) || !allp.containsAll(listParams))
			throw new RuntimeException(
					"You just found a bug! The union of (1) parameters on which you want to fold and (2) parameters on which you don't want to fold and which should thus be in a list, is not equal to the given set of ComputeParameters. Maybe this is due to the 'line_number' parameter?");
		allp.removeAll(reduceParams);
		allp.removeAll(listParams);
		if (!allp.isEmpty())
			throw new RuntimeException("You just found a bug! There are parameters for which it is unclear whether you want to fold on them.");

		// make L global
		// this.list = listParams;
		return (reduceParams);
	}

	public static List<Tuple> reduceTargets(List<Tuple> folded, List<ComputeParameter> parameterlist, List<String> targets) {
		// reduce the targets in worksheet (eg lane = 1, 1, 1, 1) to one single
		// value (lane = 1) for easy use in freemarker
		Set<String> reducedfields = reduceFieldSet(parameterlist, targets);
		if (1 == targets.size() && "line_number".equals(targets.get(0))) {
			reducedfields.addAll(folded.get(0).getFields());
		}

		// clear data that is now in reduced worksheet
		// this.reduced.clear();
		List<Tuple> reduced = new ArrayList<Tuple>();

		for (Tuple t : folded) {
			Tuple tclone = cloneTuple(t);

			for (String rfield : reducedfields) {
				if (!tclone.getFields().contains(rfield)) {
					throw new RuntimeException("Field: " + rfield + " is not known!");
				} else {
					if (!tclone.isNull(rfield)) { // tclone has a value, which
													// should be a list
						@SuppressWarnings("unchecked")
						// List<String> ls = (List<String>)
						// tclone.getList(rfield);
						List<?> lstmp = tclone.getList(rfield);
						List<String> ls = new ArrayList<String>();
						for (Object x : lstmp) {
							if (x == null)
								ls.add(null);
							else
								ls.add(x.toString());
						}

						// check: all values should be equal
						String value = ls.get(0);
						for (int i = 1; i < ls.size(); i++) {
							// if ((value != null &&
							// !value.equalsIgnoreCase(ls.get(i))) || (value ==
							// null && ls.get(i) != null))
							if (value != null && !value.equalsIgnoreCase(ls.get(i)) || value == null && ls.get(i) != null) {
								int j = i + 1;
								throw new RuntimeException("Cannot reduce field " + rfield + " because it contains different values!" + " Value 1: " + value + " is not the same as value " + j + ": "
										+ ls.get(i));
							}
						}

						// reduce to one value
						tclone.set(rfield, value);
					}

				}
			}
			reduced.add(tclone);
		}

		return reduced;
	}

	private static Tuple cloneTuple(Tuple t) {
		Tuple tclone = new SimpleTuple();

		for (String field : t.getFields()) {
			if (t.getObject(field) instanceof List) {
				List<String> clone = new ArrayList<String>();
				clone.addAll((Collection<? extends String>) t.getList(field));
				tclone.set(field, clone);
			} else {
				tclone.set(field, t.getString(field));
			}
		}

		return tclone;
	}

	public static List<Tuple> unfoldWorksheet(List<Tuple> worksheet) {
		List<Tuple> ws = new ArrayList<Tuple>();

		int nelements = 0;
		for (Tuple t : worksheet) {
			// check lenght of line_number field
			nelements = t.getList("line_number").size();

			List<String> fields = t.getFields();
			for (int i = 0; i < nelements; i++) {
				Tuple st = new SimpleTuple();

				// fill this new tuple, based on i'th elements in the tupleset
				for (String field : fields) {
					if (t.isNull(field)) {
						st.set(field, null);
					} else if (nelements > t.getList(field).size()) {
						st.set(field, t.getList(field).get(0));
					} else {
						st.set(field, t.getList(field).get(i));
					}
				}

				ws.add(st);
			}
		}
		return ws;
	}

	public static List<String> unfoldWorksheetCSV(List<Tuple> worksheet) {
		List<Tuple> w = unfoldWorksheet(worksheet);
		List<String> ws = new ArrayList<String>();

		for (Tuple t : w) {
			String row = "";
			for (String field : t.getFields()) {
				row = row + (row.equalsIgnoreCase("") ? "" : ", ") + field + "=\'" + t.getObject(field).toString() + "\'";
			}
			ws.add(row);
		}

		return ws;
	}

	private static boolean equalTuples(Tuple t1, Tuple t2) {

		List<String> fields1 = t1.getFields();
		List<String> fields2 = t2.getFields();

		// is number of fields equal?
		if (fields1.size() != fields2.size()) {
			return false;
		}

		for (String field : fields1) {
			if (t1.isNull(field)) {
				if (!t2.isNull(field))
					return false;
			} else if (!t1.getString(field).equals(t2.getString(field)))
				return false;
		}

		return true;
	}

	private static boolean equalWorksheets(List<Tuple> ws1, List<Tuple> ws2) {
		if (ws1.size() != ws2.size()) {
			Set<String> lineNumbers = new HashSet();
			for (Tuple t1 : ws1) {
				System.out.println(t1.getString("line_number"));
				if (lineNumbers.contains(t1.getString("line_number")))
					System.out.println("DUPLICATE!");
				else
					lineNumbers.add(t1.getString("line_number"));
			}
			System.out.println("worksheets of unequal lengths");
			return false;
		}

		// for each tuple in ws1, find a matching tuple in ws2 and remove the
		// 'matching index' from li
		for (Tuple t1 : ws1) {
			boolean match = false;
			for (Tuple t2 : ws2) {
				if (equalTuples(t1, t2)) {
					match = true; // match found!
					break;
				}
			}

			if (!match) {
				System.err.println("Folded not correctly: " + t1);
				System.err.println("testing fields:");

				// find a matching tuple in ws2 based on line number
				for (Tuple t : ws2) {
					if (t.getString("line_number").equals(t1.getString("line_number"))) {
						for (String field : t.getFields()) {
							if ((t.isNull(field) && !t1.isNull(field)) || !t.getString(field).equals(t1.getString(field))) {
								System.err.println("differences in field '" + field + "': " + t.getString(field) + "!=" + t1.getString(field));
							}
						}
					}
				}

				return (false);
			}
		}

		return true;
	}

	private void print(String string) {
		System.out.println(">> " + string);
	}

	/**
	 * Find all tuples in unfolded worksheet that have matching target values as
	 * the foldedTuple
	 */
	public void getUnfolded(String[] targets, Tuple foldedTuple) {
		// for each unfolded tuple we check if it matches folded tuple on the
		// selected targets
		// then we will set column 'name' to 'value'
		List<Tuple> result = new ArrayList<Tuple>();
		for (Tuple unfoldedTuple : this.worksheet) {
			// we assume match, unless we find a target that is not equal
			boolean matchAllTargets = true;
			// compare values on each target, if unequal we change match to
			// false
			for (String target : targets) {
				// ignore if not all targets match
				if (!unfoldedTuple.getObject(target).equals(foldedTuple.getObject(target))) {
					matchAllTargets = false;
				}
			}
			// if all fields have matched
			if (matchAllTargets) {
				result.add(unfoldedTuple);
			}
		}
	}

	public String getdefaultvalue(String parameter) {
		for (ComputeParameter cp : parameterlist) {
			if (cp.getName().equalsIgnoreCase(parameter)) {
				return cp.getDefaultValue();
			}
		}

		return null;
	}
}
