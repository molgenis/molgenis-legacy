package org.molgenis.framework.ui;

/**
 * A ScreenView contains the <i>layout</i> of a part of the user-interface.
 */
public interface ScreenView
{	
	/** This methods produces an html representation of the view */
	public String render();

	/** Produces any custom html headers needed, e.g. to load css or javascript */
	public String getCustomHtmlHeaders();
}
