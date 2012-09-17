/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.core.ui;

import java.util.List;

import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.framework.ui.EasyPluginModel;

/**
 * AllPublicationsModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class AllPublicationsModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;
	private List<PublicationDTO> publicationDTOList;
	private String publicationPager;
	private String rawOutput = "";

	public AllPublicationsModel(AllPublications controller)
	{
		super(controller);
	}

	public String getPublicationPager() {
		return publicationPager;
	}

	public void setPublicationPager(String publicationPager) {
		this.publicationPager = publicationPager;
	}

	public List<PublicationDTO> getPublicationDTOList() {
		return publicationDTOList;
	}

	public void setPublicationDTOList(List<PublicationDTO> publicationDTOList) {
		this.publicationDTOList = publicationDTOList;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}
}
