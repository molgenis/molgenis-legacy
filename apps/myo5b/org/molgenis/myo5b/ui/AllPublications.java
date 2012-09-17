package org.molgenis.myo5b.ui;

import org.molgenis.framework.ui.ScreenController;

public class AllPublications extends org.molgenis.core.ui.AllPublications
{
	private static final long serialVersionUID = -2834309899314411451L;

	public AllPublications(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setPublicationPager("generated-res/mvid/publicationPager.jsp");
	}

}
