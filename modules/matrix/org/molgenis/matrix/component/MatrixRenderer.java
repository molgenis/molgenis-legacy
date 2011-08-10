/**
 * File: TextInput.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-08, 1.0.0, DI Matthijssen; Creation
 * <li>2006-05-14, 1.1.0, MA Swertz; Refectoring into Invengine.
 * </ul>
 * TODO look at the depreciated functions.
 */

package org.molgenis.matrix.component;

import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.html.HtmlWidget;

/**
 * Renders an editor for freemarker including code higlighting and line numbers.
 * 
 * Based on http://codemirror.net/ (thanks!)
 */
public class MatrixRenderer extends HtmlWidget
{
	
	private RenderableMatrix matrix = null;
	
	public MatrixRenderer(String name, RenderableMatrix matrix)
	{
		this(name, null, matrix);
	}

	public MatrixRenderer(String name, String value, RenderableMatrix matrix)
	{
		super(name, value);
		this.matrix = matrix;
	}

	public String toHtml()
	{
		Map<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("name", this.getName());
		parameters.put("value", this.getObject());
		parameters.put("matrix", this.matrix);

		// delegate to freemarker (sad Java doesn't allow multiline strings).
		return new FreemarkerView(
				"org/molgenis/matrix/component/MatrixRenderer.ftl",
				parameters).render();
	}
}
