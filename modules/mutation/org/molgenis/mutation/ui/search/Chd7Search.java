/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;

public class Chd7Search extends SearchPlugin
{
	private static final long serialVersionUID = 4159412082076885902L;

	public Chd7Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new SearchModel(this));
		this.setView(new FreemarkerView("SearchPlugin.ftl", this.getModel()));
		this.getModel().setGeneName("CHD7");
		this.getModel().setPatientPager("res/mutation/chd7PatientPager.jsp");
		this.getModel().setMutationPager("res/mutation/chd7MutationPager.jsp");
	}
	
	@Override
	public void reload(Database db)
	{
		super.reload(db);

		try
		{
			MutationService mutationService = MutationService.getInstance(db);
			PatientService patientService   = PatientService.getInstance(db);
	
			StrBuilder text = new StrBuilder();
			text.appendln("<h3>");
			text.appendln("Welcome to the <b>open-access database on CHD7-mutations</b>.");
			text.appendln("</h3>");
			text.appendln("<p>");
			text.appendln("The CHD7 mutation database contains anonymised data on both published and unpublished CHD7 variations and phenotype.");
			text.appendln("</p>");
			text.appendln("<p>");
			text.appendln("The database currently contains " + mutationService.getNumMutationsByPathogenicity("pathogenic") + " pathogenic mutations in " + patientService.getNumPatientsByPathogenicity("pathogenic") + " patients, " + mutationService.getNumMutationsByPathogenicity("unclassified variant") + " unclassified variants in " + patientService.getNumPatientsByPathogenicity("unclassified variant") + " patients, and " + mutationService.getNumMutationsByPathogenicity("polymorphism") + " polymorphism.");
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
			text.appendln("<li>Mutations are numbered according to the current reference sequence (<a href=\"http://www.ncbi.nlm.nih.gov/nuccore/157389010\" target=\"_new\">GenBank Accession no. NM_017780.2</a>)</li>");
			text.appendln("<li>Mutation nomenclature is according to the <a href=\"http://www.hgvs.org/mutnomen/\" target=\"_new\">HGVS recommendations</a></li>");
			text.appendln("</ol>");
			
			this.getModel().setTextRemarks(text.toString());
			
			text = new StrBuilder();
			text.appendln("<h4>Collaborators and supporters</h4>");
			text.appendln("<table width=\"100%\">");
			text.appendln("<tr>");
			text.appendln("<td><a href=\"http://www.umcg.nl/NL/UMCG/overhetumcg/organisatie/Specialismen/dermatologie/Pages/default.aspx\" target=\"_new\"><img src=\"res/img/col7a1/umcg.jpg\" width=\"200\"/></a></td>");
	//		text.appendln("<td><a href=\"http://www.idi.it/web/idi/home\" target=\"_new\"><img src=\"res/img/col7a1/idi.jpg\" height=\"75\"/></a></td>");
	//		text.appendln("<td><a href=\"http://www.eb-haus.eu/index.php?id=21&L=1\" target=\"_new\"><img src=\"res/img/col7a1/ebhaus.png\" height=\"75\"/></a></td>");
	//		text.appendln("<td><a href=\"http://www.guysandstthomas.nhs.uk/services/dash/dermatology/dermatology.aspx\" target=\"_new\"><img src=\"res/img/col7a1/stjohns.jpg\" height=\"75\"/></a></td>");
	//		text.appendln("<td><a href=\"http://www.uniklinik-freiburg.de/ims/live/hospital/dermatology_en.html\" target=\"_new\"><img src=\"res/img/col7a1/ukl-logo.jpg\" width=\"200\"/></a></td>");
	//		text.appendln("<td><a href=\"http://www.debra-international.org/\" target=\"_new\"><img src=\"res/img/col7a1/debra_international.png\" height=\"75\"/></a></td>");
			text.appendln("</tr>");
			text.appendln("</table>");
			
			this.getModel().setTextCollaborations(text.toString());
		}
		catch (Exception e)
		{
			
		}
	}
}
