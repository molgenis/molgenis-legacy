/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.chd7.ui;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.ui.search.SearchModel;
import org.molgenis.mutation.ui.search.SearchPlugin;

public class Search extends SearchPlugin
{
	private static final long serialVersionUID = 4159412082076885902L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new SearchModel(this));
		this.setView(new FreemarkerView("init.ftl", this.getModel()));
		this.getModel().setGeneName("CHD7");
		this.getModel().getmBrowseVO().getGenePanel().setShowNames(false);
		this.getModel().setPatientPager("res/mutation/patientPager.jsp");
		this.getModel().setMutationPager("res/mutation/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/mutation/ui/search/patient.ftl");
	}
	
	@Override
	public void reload(Database db)
	{
		super.reload(db);

		try
		{
			MutationService mutationService = new MutationService();
			mutationService.setDatabase(db);
			PatientService patientService   = new PatientService();
			patientService.setDatabase(db);
	
			StrBuilder text = new StrBuilder();
			text.appendln("<h3>");
			text.appendln("Welcome to the <b>open-access database on CHD7-mutations</b>.");
			text.appendln("</h3>");
			text.appendln("<p>");
			text.appendln("The CHD7 mutation database contains anonymised data on both published and unpublished CHD7 variations and phenotype. The CHD7 mutation database is under construction. Additions and improvements are still being made.");
			text.appendln("</p>");
			text.appendln("<p>");
			text.appendln("The database currently contains " + mutationService.getNumMutationsByPathogenicity("pathogenic") + " pathogenic mutations in " + patientService.getNumPatientsByPathogenicity("pathogenic") + " patients, " + mutationService.getNumMutationsByPathogenicity("unclassified variant") + " unclassified variants in " + patientService.getNumPatientsByPathogenicity("unclassified variant") + " patients, and " + mutationService.getNumMutationsByPathogenicity("benign") + " benign variants.");
			text.appendln("</p>");
			text.appendln("<p>");
			text.appendln("You can search or browse below.");
			text.appendln("</p>");
			text.appendln("<br/>");
			
			this.getModel().setTextWelcome(text.toString());
	
			text = new StrBuilder();
			text.appendln("<h3>Search database</h3>");
			text.appendln("<p>");
			text.appendln("Search by typing any search term in the search field, like cDNA (e.g. \"160del\", \"232C>T\") or protein (e.g. \"Lys643fs\", \"Arg2024X\") notations of mutations, or specific phenotypes (e.g. \"coloboma\"). Search results are shown at bottom of page.");
			text.appendln("</p>");
			
			this.getModel().setTextSearch(text.toString());
			
			text = new StrBuilder();
			text.appendln("<h4>General remarks</h4>");
			text.appendln("<ol>");
			text.appendln("<li>Mutations are numbered according to the current reference sequence (<a href=\"http://www.ncbi.nlm.nih.gov/nuccore/NM_017780.3\" target=\"_new\">GenBank Accession no. NM_017780.3</a>)</li>");
			text.appendln("<li>Mutation nomenclature is according to the <a href=\"http://www.hgvs.org/mutnomen/\" target=\"_new\">HGVS recommendations</a></li>");
			text.appendln("</ol>");
			
			this.getModel().setTextRemarks(text.toString());
			
			text = new StrBuilder();
			text.appendln("<h4>Collaborators and supporters</h4>");
			text.appendln("<table width=\"100%\">");
			text.appendln("<tr>");
			text.appendln("<td><a href=\"http://www.rug.nl/umcg/faculteit/disciplinegroepen/medischegenetica/research/chargesyndrome/index\" target=\"_new\"><img src=\"res/img/col7a1/umcg.jpg\" width=\"200\"/></a></td>");
			text.appendln("<td><a href=\"http://www.humangenetics.nl/en/index_en.php\" target=\"_new\"><img src=\"http://www.umcn.nl/_layouts/IMAGES/radboud/logo.jpg\"/></a></td>");
			text.appendln("</tr>");
			text.appendln("</table>");
			
			this.getModel().setTextCollaborations(text.toString());
		}
		catch (Exception e)
		{
			
		}
	}
}
