package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

public class MolgenisForm extends AbstractHtmlElement
{
	private ScreenModel model = null;
	private Layout layout = new FlowLayout();
	
	@Deprecated
	public MolgenisForm(ScreenModel model)
	{
		super(model.getController().getName());
		this.model = model;
	}
	
	public MolgenisForm(ScreenController<?> controller)
	{
		this(controller.getModel());
	}
	
	public MolgenisForm(ScreenController<?> controller, Layout layout)
	{
		this(controller.getModel());
		this.layout = layout;
	}
	
	@Override
	public String render()
	{	
		//use freemarker macros to render form header and footer
		FreemarkerView view = new FreemarkerView(MolgenisForm.class.getPackage().getName().replace(".", "/")+"/MolgenisForm.ftl", getModel());
		view.addParameter("content", layout.render());
		String result = view.render();
		return result;
	}
	
	public ScreenModel getModel()
	{
		return model;
	}

	public void setModel(ScreenModel model)
	{
		this.model = model;
	}

	public void add(HtmlElement element)
	{
		this.layout.add(element);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String render(Tuple params) throws ParseException,
			HtmlInputException
	{
		throw new UnsupportedOperationException("not implemented");
	}
}
