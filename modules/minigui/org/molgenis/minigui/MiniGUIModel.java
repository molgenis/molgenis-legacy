/* Date:        March 22, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.minigui;

import java.util.Vector;

import org.molgenis.framework.ui.ScreenController;

public class MiniGUIModel
{
	private Vector<ScreenController<?>> uiTree;

	public Vector<ScreenController<?>> getUiTree()
	{
		return uiTree;
	}

	public void setUiTree(Vector<ScreenController<?>> uiTree)
	{
		this.uiTree = uiTree;
	}

}