/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.myo5b.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.mutation.ui.search.SearchPlugin;

public class Search extends SearchPlugin
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 4159412082076885902L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setPatientPager("res/mutation/mvid/patientPager.jsp");
		this.getModel().setMutationPager("res/mutation/mvid/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/mutation/ui/search/patient.ftl");
		this.getModel().getMbrowse().setShowNames(false);
	}
	
	@Override
	public void reload(Database db)
	{
		super.reload(db);
	}
}
