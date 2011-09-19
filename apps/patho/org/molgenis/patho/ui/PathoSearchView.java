package org.molgenis.patho.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.matrix.component.PhenoMatrix;

public class PathoSearchView implements ScreenView
{
	PathoSearchModel model;
	
	public PathoSearchView(PathoSearchModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException 
	{
		MolgenisForm form = new MolgenisForm(this.model);
		
		//provide a search box 
		SelectInput chromosomes = new SelectInput("chromosome");
		chromosomes.setOptions(model.getChromosomes(), model.getChromosomes());
		form.add(chromosomes);
		
		//provide a 'from' box
		form.add( new IntInput("from") );
		
		//provide a 'range' box
		form.add(new IntInput("range"));
		
		//provide a box with the current results
		
	
		return form.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}

}
