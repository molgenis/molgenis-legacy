/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search.col7a1;

import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.FrontEndElement;
import org.molgenis.mutation.ui.search.SearchModel;
import org.molgenis.mutation.ui.search.SearchPlugin;

public class Search extends SearchPlugin
{
	private static final long serialVersionUID = 1162846311691838788L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new SearchModel(this));
		this.setView(new FreemarkerView("SearchPlugin.ftl", this.getModel()));
		this.getModel().setGeneName("COL7A1");
		this.getModel().setPatientPager("res/mutation/col7a1/patientPager.jsp");
		this.getModel().setMutationPager("res/mutation/col7a1/mutationPager.jsp");
	}
	
	@Override
	public void reload(Database db)
	{
		super.reload(db);

		try
		{
			List<FrontEndElement> fee;
	
			StrBuilder text = new StrBuilder();
			text.appendln("<h3>");
			text.appendln("Welcome to the <b>international registry of dystrophic epidermolysis bullosa (DEB) patients and associated COL7A1 mutations</b>.");
			text.appendln("</h3>");
			text.appendln("<p>");
			text.appendln("The International Dystrophic Epidermolysis Bullosa Patient Registry contains anonymised data on both published and unpublished DEB patients, as well as their associated COL7A1 mutations and genotypes, and clinical and molecular phenotypes.");
			text.appendln("</p>");
			text.appendln("<p>");
			text.appendln("The database currently contains " + this.getModel().getNumPatients() + " DEB patients, of which " + this.getModel().getNumUnpublished() + " unpublished, and " + this.getModel().getNumMutations() + " COL7A1 mutations. Search or browse below.");
			text.appendln("</p>");
			text.appendln("<br/>");
			
			this.getModel().setTextWelcome(text.toString());
	
			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "search").find();
			
			if (fee.size() == 1)
			{
				this.getModel().setTextSearch(fee.get(0).getUsertext());
			}
			else
			{
				text = new StrBuilder();
				text.appendln("<h3>Search registry</h3>");
				text.appendln("<p>");
				text.appendln("Search by typing any search term in the search field, like cDNA (e.g. \"3G>T\") or protein (e.g. \"Arg525Ter\") notations of mutations, mode of inheritance (e.g. \"dominant\") or specific phenotypes (e.g. \"severe generalized\"). Search results are shown at bottom of page.");
				text.appendln("</p>");

				this.getModel().setTextSearch(text.toString());
			}

			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "remarks").find();
			
			if (fee.size() == 1)
			{
				this.getModel().setTextRemarks(fee.get(0).getUsertext());
			}
			else
			{
				text = new StrBuilder();
				text.appendln("<h4>General remarks</h4>");
				text.appendln("<ol>");
				text.appendln("<li>Mutations are numbered according to the current reference sequence (<a href=\"http://www.ncbi.nlm.nih.gov/nuccore/157389010\" target=\"_new\">GenBank Accession no. NM_000094.3</a>)</li>");
				text.appendln("<li>Mutation nomenclature is according to the <a href=\"http://www.hgvs.org/mutnomen/\" target=\"_new\">HGVS recommendations</a></li>");
				text.appendln("</ol>");
				
				this.getModel().setTextRemarks(text.toString());
			}

			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "support").find();
			
			if (fee.size() == 1)
			{
				this.getModel().setTextCollaborations(fee.get(0).getUsertext());
			}
			else
			{
				text = new StrBuilder();
				text.appendln("<h4>Collaborators and supporters</h4>");
				text.appendln("<table width=\"100%\">");
				text.appendln("<tr>");
				text.appendln("<td><a href=\"http://www.umcg.nl/NL/UMCG/overhetumcg/organisatie/Specialismen/dermatologie/Pages/default.aspx\" target=\"_new\"><img src=\"res/img/col7a1/umcg.jpg\" width=\"200\"/></a></td>");
				text.appendln("<td><a href=\"http://www.idi.it/web/idi/home\" target=\"_new\"><img src=\"res/img/col7a1/idi.jpg\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.eb-haus.eu/index.php?id=21&L=1\" target=\"_new\"><img src=\"res/img/col7a1/ebhaus.png\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.guysandstthomas.nhs.uk/services/dash/dermatology/dermatology.aspx\" target=\"_new\"><img src=\"res/img/col7a1/stjohns.jpg\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.uniklinik-freiburg.de/ims/live/hospital/dermatology_en.html\" target=\"_new\"><img src=\"res/img/col7a1/ukl-logo.jpg\" width=\"200\"/></a></td>");
				text.appendln("<td><a href=\"http://www.debra-international.org/\" target=\"_new\"><img src=\"res/img/col7a1/debra_international.png\" height=\"75\"/></a></td>");
				text.appendln("</tr>");
				text.appendln("</table>");
				
				this.getModel().setTextCollaborations(text.toString());
			}
		}
		catch (Exception e)
		{
			//TODO: What to do here?
		}
	}
}
