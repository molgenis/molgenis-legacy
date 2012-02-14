package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Tuple;

/**
 * Menu can contain a bunch of action inputs. Optionally, MenuInput can be
 * nested in a submenu.
 */
public class MenuInput extends AbstractHtmlElement implements HtmlElement
{
	private List<HtmlElement> menusAndButtons = new ArrayList<HtmlElement>();
	private String label;
	
	public MenuInput(String name, String label)
	{
		super(name);
		this.label = label;
	}

	public void AddAction(ActionInput action)
	{
		menusAndButtons.add(action);
	}

	public void AddMenu(MenuInput menu)
	{
		menusAndButtons.add(menu);
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	public String render()
	{
		return render(this);
	}

	protected String render(MenuInput root)
	{
		String items = "";
		if(root == null) root = this;

		for (HtmlElement item : menusAndButtons)
		{
			if (item instanceof ActionInput)
			{
				ActionInput action = (ActionInput) item;
				if(this.equals(root))
				{
					items += action.render() + "<br />";
				}
				else
				{
					items += "<li><a href=\"#\" onclick=\"" + action.getJavaScriptAction()+"\">" + 
						action.getButtonValue() + "</a></li>";
				}
			}
			else
			{
				MenuInput menu = (MenuInput) item;
				if(this.equals(root))
				{
					items += "<button onclick=\"return false;\">"+menu.getLabel()+"</button>" + 
						menu.render(root);
				}
				else
				{
					items += "<li><a href=\"#\">" + menu.getLabel() + "</a>"
						+ menu.render(root) + "</li>";
				}
			}
		}

		if (this.equals(root)) {
			String result = "<div style=\"vertical-align:middle\"><input type=\"button\" value=\"Download\" " +
					"onclick=\"if (document.getElementById('" + getId() + "').style.display=='none') {document.getElementById('" + getId() + "').style.display='block';} else {document.getElementById('" + getId() + "').style.display='none';} \" " +
					//"onmouseout=\"document.getElementById('" + getId() + "').style.display='none'\" " + 
					"/></div>";
			result += ("<div id=\"" + getId() + "\" style=\"position:absolute; z-index:1; background-color:white; padding:2px; display:none\">" + items + "</div>");
			return result;
		} else {
			return "<ul>" + items + "</ul>";
		}
	}

	@Override
	public String render(Tuple params) throws ParseException,
			HtmlInputException
	{
		MenuInput root = null;
		return render(root);
	}

}
