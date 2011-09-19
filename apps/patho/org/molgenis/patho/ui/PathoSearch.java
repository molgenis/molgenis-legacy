
package org.molgenis.patho.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.variant.Chromosome;

/**
 * PathoSearchController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>PathoSearchModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>PathoSearchView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class PathoSearch extends EasyPluginController<PathoSearchModel>
{
	public PathoSearch(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new PathoSearchModel(this)); //the default model
		this.setView(new PathoSearchView(getModel())); //<plugin flavor="freemarker"
	}

	
	
	@Override
	public void reload(Database db) throws Exception
	{
		SliceableMatrix m = null;
		
		//this.getModel().setChromosomes(db.query(Chromosome.class).find());
		
		//select chromosome, startpos, endpos
		//m.sliceByRow(SequenceVariant.CHR, QueryRule.Operator.EQUALS,  getModel().getSelectedChrId());
		
		
	}
	

}