package org.molgenis.hemodb.plugins;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.apache.commons.lang.StringUtils;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.hemodb.HemoProbe;
import org.molgenis.hemodb.HemoSample;
import org.molgenis.hemodb.HemoSampleGroup;
import org.molgenis.hemodb.plugins.QuestionsModel.QuestionState;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;
import plugins.matrix.manager.MatrixManagerModel;

/**
 * EditIndividualController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>AddIndividualModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>
 * AddIndividualView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class QuestionOne extends EasyPluginController<QuestionsModel> {

	public QuestionOne(String name, ScreenController<?> parent) {
		super(name, parent);
		this.setModel((QuestionsModel) parent.getModel()); // the default model

		// create sub plugin
		new MatrixManagerHemodb("QuestionsSub", this);
	}

	public ScreenView getView() {
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}


	/**
	 * At each page view: reload data from database into model and/or change.
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception {	
		selectSampleGroupsForDropdown(db);

		// also reload sub plugin
		this.get("QuestionsSub").reload(db);
	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */

	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {
		getModel().setAction(request.getAction());

		if ("back".equals(request.getAction())) {
			getModel().setState(QuestionState.BEGINNING);
		}

		try {
			// GENE EXPRESSION DATA MATRIX SELECTION -> shows which type of gene
			// expression will be used
			String geneExp = request.getString("geneExp");

			// LIST OF GENES SELECTION -> Gets all the listed genes from the
			// website
			String genesFromSite = request.getString("geneText");
			String[] genes = genesFromSite.split("\n");
			List<String> probes = null;

			if (genes != null) {
				if (genes.length == 0) {
					System.out
							.println("There are no genes in the inputfield. Try again.");
				} else {
					probes = selectProbesWithGenes(db, genes);
				}
			} else {
				System.out
						.println("There are no genes specified. Please try again");
			}

			// SAMPLE SELECTION BASED ON SAMPLE GROUP NAMES -> Gets the selected
			// groups from the website and retrieves the associated samples
			List<String> groups = request.getStringList("sampleGroups");
			List<String> sampleNames = selectSamplesFromSampleGroups(db, groups);

			// SUBMIT BUTTON
			if (getModel().getAction().equals("submitInfoQ1")) {
				System.out
						.println("we handled the information on the site submitted via the submit button");

				// get the data set
				Data dataSet = db.query(Data.class).eq(Data.NAME, geneExp)
						.find().get(0);

				// get the Browser which is in the other plugin
				MatrixManagerHemodb matrixManager = (MatrixManagerHemodb) this
						.getApplicationController().get("QuestionsSub");

				// the matrix we want to show
				DataMatrixInstance m = this.getMatrixInstance(db, dataSet,
						sampleNames, probes);

				Browser br = new Browser(dataSet, m,
						this.getApplicationController());
				MatrixManagerModel model = (MatrixManagerModel) matrixManager
						.getMyModel();
				model.setBrowser(br);

				// set nice label
				matrixManager.setLabel("Results of your selection");

				// refresh attributes, operators, filter
				model.setRowHeaderAttr(null);
				model.setColHeaderAttr(null);
				model.setAllOperators(null);
				model.setValueOperators(null);
				model.setFilter(null);

				model.setUploadMode(false);

				model.setSelectedData(dataSet);

				matrixManager.createOverLibText(db);

				matrixManager.createHeaders();

				if (model.getRowHeaderAttr() == null
						|| model.getColHeaderAttr() == null) {
					matrixManager.setHeaderAttr(db);
				}

				if (model.getAllOperators() == null) {
					matrixManager.setAllOperators();
				}

				if (model.getValueOperators() == null) {
					matrixManager.setValueOperators();
				}

				getModel().setState(QuestionState.QUESTION1_RESULT);

			} else {
				System.out.println("Something went wrong, try again.");
			}
		}

		// TODO show selected data
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Show.SHOW_MAIN;
	}

	public void selectSampleGroupsForDropdown(Database db)
			throws DatabaseException {
		// SELECTION OF ALL THE SAMPLE GROUPS IN THE DATABASE TO DISPLAY ON THE
		// SITE (MULTIPLE SELECT)
		List<HemoSampleGroup> sampleGroups = db.find(HemoSampleGroup.class);
		if (sampleGroups != null) {
			for (HemoSampleGroup hsg : sampleGroups) {
				String name = hsg.getName();
				getModel().getNames().add(name);
			}
		} else {
			System.out.println("OOPS! SOMETHING WENT WRONG");
		}
	}

	public List<String> selectSamplesFromSampleGroups(Database db,
			List<String> sampleGroups) throws DatabaseException {
		// SELECTION OF THE SAMPLE NAMES WITHIN EACH GROUP THAT IS SPECIFIED
		// TODO: RETURN SAMPLE NAMES TO HANDLEREQUEST
		List<String> sampleNames = new ArrayList<String>();
		int numberGroups = sampleGroups.size();
		if (numberGroups == 0) {
			System.out.println("there is no selection made");
		} else {
			System.out.println("\n" + "number of groups: " + numberGroups);
			for (String hsg : sampleGroups) {
				System.out.println("\n" + "sampleGroup is: " + hsg);
				List<HemoSample> samplesPerGroup = db.find(HemoSample.class,
						new QueryRule(HemoSample.SAMPLEGROUP_NAME,
								Operator.EQUALS, hsg));
				for (HemoSample name : samplesPerGroup) {
					sampleNames.add(name.getName());
				}
			}
			System.out.println("sampleNames for this group: " + sampleNames
					+ "\n");
			// return list with sample names ;
		}
		return sampleNames;
	}

	public List<String> selectProbesWithGenes(Database db, String[] genes)
			throws DatabaseException {
		// GETS A STRING WITH GENE NAMES, CONVERTS THEM TO PROBES AND RETURNS
		// THEM TO THE HANDLEREQUEST

		try {
			List<String> probes = new ArrayList<String>();
			for (String gene : genes) {
				gene = gene.toUpperCase();
				gene = StringUtils.chomp(gene);
				System.out.println("gene is: " + gene);
				List<HemoProbe> probesPerGene = db.find(HemoProbe.class,
						new QueryRule(HemoProbe.REPORTSFOR_NAME,
								Operator.EQUALS, gene));

				for (HemoProbe probe : probesPerGene) {
					// System.out.println("name of gene: " + probe);
					System.out.println("name of probe: " + probe.getName());
					probes.add(probe.getName());
				}
			}
			if (!probes.isEmpty()) {
				System.out.println("probes found with this gene(s): " + probes);
				return probes;
			} else if (probes.isEmpty()) {
				System.out
						.println("This gene cannot be found in this database.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;

	}

	public DataMatrixInstance getMatrixInstance(Database db, Data dataSet,
			List<String> sampleNames, List<String> probeNames) throws Exception {
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		BinaryDataMatrixInstance m = (BinaryDataMatrixInstance) dmh
				.createInstance(dataSet, db);

		// filter in sampleNames and on probeNames
		return m.getSubMatrix(probeNames, sampleNames);
	}
}

// gene = gene.replace(System.getProperty("line.separator"), "");