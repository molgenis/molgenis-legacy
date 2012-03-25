/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.header;

import org.molgenis.framework.ui.EasyPluginModel;

public class HeaderModel extends EasyPluginModel
{
	// The serial version UID of this class. Needed for serialization.
	private static final long serialVersionUID = 1L;
	private String logo;
	private String title;
	
	public HeaderModel(Header controller)
	{
		super(controller);
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
