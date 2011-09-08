package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Tuple;

/**
 * Menu can contain a bunch of action inputs. Optionally, MenuInput can be
 * nested in a submenu.
 * 
 * Based on http://www.nexul.com/prototypes/toolbar/demo.html
 */
public class MenuInput extends AbstractHtmlElement implements HtmlElement
{
	public enum Style
	{
		BREADCRUMPS, FLYOUT
	};

	private List<HtmlElement> menusAndButtons = new ArrayList<HtmlElement>();
	private String label;
	private Style style = Style.FLYOUT;

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

	public void setStyle(Style style)
	{
		this.style = style;
	}

	public Style getStyle()
	{
		return this.style;
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
					items += action.render();
				}
				else
				{
				items += "<li><a href=\"#\" onclick=\""+action.getJavaScriptAction()+"\">" + action.getButtonValue()
						+ "</a></li>";
				}
			}
			else
			{
				MenuInput menu = (MenuInput) item;
				if(this.equals(root))
				{
					items += "<button onclick=\"return false;\">"+menu.getLabel()+"</button>"+ menu.render(root);
				}
				else
				{
					items += "<li><a href=\"#\">" + menu.getLabel() + "</a>"
							+ menu.render(root) + "</li>";
				}
			}
		}

		if (this.equals(root))
		{
			return "<div id=\""+getId()+"\" class=\"ui-widget ui-widget-content ui-corner-all\">"+items+"</div><script>$('#"+getId()+"').menubar();</script>";
			
			
			
//			String result = "<ul id=\""+getId()+"\">" + items
//					+ "</ul>" + "<script>$('#" + this.getId()
//					+ "').menu();</script>";
//			return result;
			
//			String result = "<a href=\"#"
//					+ this.getId()
//					+ "_menuitems\" class=\"fg-button fg-button-icon-right ui-widget ui-state-default ui-corner-all\" id=\""
//					+ this.getId() + "\">"
//					+ "<span class=\"ui-icon ui-icon-triangle-1-s\"></span>"
//					+ getLabel() + "</a>" + "<div id=\"" + this.getId()
//					+ "_menuitems\" class=\"hidden\">" + "<ul>" + items
//					+ "</ul>" + "</div><script>$('#" + this.getId()
//					+ "').menu({content: $('#" + this.getId()
//					+ "').next().html(),";
//			if(Style.BREADCRUMPS.equals(this.getStyle()))
//				result += "backLink: false";
//			if(Style.FLYOUT.equals(this.getStyle()))
//				result += "flyOut: true";
//			result +="});</script>";
//			return result;
		}
		else
			return "<ul>" + items + "</ul>";
	}

	@Override
	public String render(Tuple params) throws ParseException,
			HtmlInputException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
