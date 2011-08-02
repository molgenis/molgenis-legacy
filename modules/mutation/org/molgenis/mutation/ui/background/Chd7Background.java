/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.background;

import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;

public class Chd7Background extends Background
{
	private static final long serialVersionUID = 1L;

	public Chd7Background(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new BackgroundModel(this));
		this.setView(new FreemarkerView("Chd7Background.ftl", getModel()));
	}
}
