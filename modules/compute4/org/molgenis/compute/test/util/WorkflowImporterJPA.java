package org.molgenis.compute.test.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.ComputeRequirement;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 15/08/2012 Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowImporterJPA {
	private File parametersFile, workflowFile, protocolsDir;

	public static void main(String[] args) {
		if (args.length == 3) {
			System.out.println("*** START WORKFLOW IMPORT");
		} else {
			System.out.println("Not enough parameters");
			System.exit(1);
		}

		try {
			new WorkflowImporterJPA().process(args[0], args[1], args[2]);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	private void process(String parametersFileName, String workflowFileName,
			String protocolsDirName) throws DatabaseException {
		// self-explanatory code
		parametersFile = new File(parametersFileName);
		workflowFile = new File(workflowFileName);
		protocolsDir = new File(protocolsDirName);

		continueIfExist(parametersFile);
		continueIfExist(workflowFile);
		continueIfExist(protocolsDir);

		Database db = DatabaseFactory.create();
		try {
			db.beginTx();

			String workflowName = workflowFile.getName();

			// create workflow
			Workflow workflow = new Workflow();
			workflow.setName(workflowName);
			db.add(workflow);

			// create one requirement which we use for all protocols (for
			// testing)
			ComputeRequirement requirement = new ComputeRequirement();
			requirement.setName("TestRequirement");
			requirement.setCores(1);
			requirement.setNodes(1);
			requirement.setMem("test");
			requirement.setWalltime("test");
			db.add(requirement);

			// parse the parameters file
			CsvReader reader = new CsvFileReader(parametersFile);
			Hashtable<ComputeParameter, Vector<String>> collectionParameterHasOnes = new Hashtable<ComputeParameter, Vector<String>>();
			Vector<ComputeParameter> parameters = new Vector<ComputeParameter>();
			for (Tuple row : reader) {
				// String description = row.getString("description");

				String name = row.getString("Name");
				if (name.equals("#"))
					continue;

				ComputeParameter parameter = new ComputeParameter();
				parameter.setName(name);
				if (row.getString("defaultValue") != null)
					parameter.setDefaultValue(row.getString("defaultValue"));

				String dataType = row.getString("dataType");
				if (dataType == null)
					parameter.setDataType("string");
				else
					parameter.setDataType(dataType);

				parameter.setWorkflow(workflow);

				String hasOne_name = row.getString("hasOne_name");
				if (hasOne_name != null) {
					Vector<String> hasOnes = splitCommas(hasOne_name);
					collectionParameterHasOnes.put(parameter, hasOnes);
				}

				parameters.add(parameter);
				// System.out.println(">> " + parameter.toString());
				db.add(parameter);
			}

			// db.add(parameters);

			// find parameters has ones
			Enumeration<ComputeParameter> ekeys = collectionParameterHasOnes
					.keys();
			while (ekeys.hasMoreElements()) {
				ComputeParameter parameter = (ComputeParameter) ekeys
						.nextElement();
				Vector<String> parNames = collectionParameterHasOnes
						.get(parameter);

				Vector<ComputeParameter> vecParameters = new Vector<ComputeParameter>();
				for (String name : parNames) {
					ComputeParameter hasParameter = findParameter(parameters,
							name.trim());
					vecParameters.add(hasParameter);
				}
				parameter.setHasOne(vecParameters);
			}

			db.update(parameters);

			// add protocols
			Vector<ComputeProtocol> protocols = new Vector<ComputeProtocol>();
			if (protocolsDir.isDirectory()) {
				String[] files = protocolsDir.list();
				for (int i = 0; i < files.length; i++) {
					String fName = files[i];

					String protocolName;

					int dotPos = fName.lastIndexOf(".");
					if (dotPos > -1) {
						protocolName = fName.substring(0, dotPos);
					} else
						protocolName = fName;

					String protocolFile = protocolsDir.getPath()
							+ System.getProperty("file.separator") + fName;

					if (new File(protocolFile).isDirectory())
						continue;

					String listing = getFileAsString(protocolFile);

					// Get string list of target names
					List<String> targetStringList = findTargetList(listing);

					// convert targetList to a parameterList
					List<ComputeParameter> targetList = new ArrayList<ComputeParameter>();
					Iterator<String> it = targetStringList.iterator();
					while (it.hasNext()) {
						targetList.add(findParameter(parameters, it.next()));
					}

					// Why do we have requirements here if we have xref to
					// ComputeRequirements

					ComputeProtocol protocol = new ComputeProtocol();
					protocol.setName(protocolName);
					protocol.setScriptTemplate(listing);
					if (0 < targetList.size())
						protocol.setIterateOver(targetList);
					protocol.setRequirements(requirement);

					//
					protocol.setCores(1);
					protocol.setNodes(1);
					protocol.setMem("");
					protocol.setWalltime("");

					protocols.add(protocol);
					db.add(protocol);
				}
			}

			// add workflow elements
			Vector<WorkflowElement> workflowElements = new Vector<WorkflowElement>();
			reader = new CsvFileReader(workflowFile);
			for (Tuple row : reader) {
				String workflowElementName = row.getString("name");
				String protocolName = row.getString("protocol_name");
				String previousSteps = row.getString("PreviousSteps_name");

				WorkflowElement element = new WorkflowElement();
				element.setName(workflowElementName);
				element.setWorkflow(workflow);

				ComputeProtocol p = findProtocol(protocols, protocolName);
				element.setProtocol(p);

				if (previousSteps != null) {
					Vector<String> previousStepsNames = splitCommas(previousSteps);
					Vector<WorkflowElement> previousStepsVector = new Vector<WorkflowElement>();
					for (String prev : previousStepsNames) {
						WorkflowElement elPrev = findWorkflowElement(
								workflowElements, prev);
						previousStepsVector.add(elPrev);
					}
					element.setPreviousSteps(previousStepsVector);
				}

				workflowElements.add(element);
				db.add(element);
			}

			db.commitTx();
			System.out.println("... done");
		} catch (Exception e) {
			db.rollbackTx();
			e.printStackTrace();
		}

	}

	private List<String> findTargetList(String listing) {
		int start = listing.indexOf("#FOREACH");
		if (start == -1) {
			return new ArrayList<String>();
		} else {
			start += "#FOREACH".length();
			int stop = listing.indexOf("\n", start);

			String targetsString = listing.substring(start, stop);
			String[] targets = targetsString.split(",");

			List<String> targetList = new ArrayList<String>();
			for (int i = 0; i < targets.length; i++) {
				targetList.add(targets[i].trim());
			}

			return targetList;
		}
	}

	private WorkflowElement findWorkflowElement(Vector<WorkflowElement> vector,
			String name) {
		for (WorkflowElement par : vector) {
			if (par.getName().equalsIgnoreCase(name))
				return par;
		}
		return null;
	}

	private ComputeProtocol findProtocol(Vector<ComputeProtocol> vector,
			String name) {
		for (ComputeProtocol par : vector) {
			if (par.getName().equalsIgnoreCase(name))
				return par;
		}
		return null;
	}

	private ComputeParameter findParameter(Vector<ComputeParameter> vector,
			String name) {
		for (ComputeParameter par : vector) {
			if (par.getName().equalsIgnoreCase(name)) {
				return par;
			}
		}

		System.out.println(">> ???!");
		return null;
	}

	private void continueIfExist(File file) {
		if (!file.exists()) {
			System.out.println("Error: " + file.getName() + " does not exist");
			System.exit(1);
		}
	}

	private Vector<String> splitCommas(String list) {
		list = list.trim();
		Vector<String> values = new Vector<String>();

		while (list.indexOf(",") > -1) {
			int posComa = list.indexOf(",");
			String name = list.substring(0, posComa).trim();
			if (name != "")
				values.addElement(name);
			list = list.substring(posComa + 1);
		}
		values.add(list.trim());
		return values;
	}

	private final String getFileAsString(String filename) throws IOException {
		File file = new File(filename);

		final BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(file));
		final byte[] bytes = new byte[(int) file.length()];
		bis.read(bytes);
		bis.close();
		return new String(bytes);
	}
}
