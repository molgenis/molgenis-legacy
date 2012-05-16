package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.server.AbstractMolgenisServlet;
import org.molgenis.framework.server.QueryRuleUtil;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;

public abstract class AbstractRefInput<E> extends HtmlInput<E>
{
	private static String DEFAULT_URL = "xref/find";
	
	public static class Builder<E> {
		private final E value;
		
		//private final String name;
		private final Class<? extends Entity> xrefEntityClass;
		
		//optional arguments		
		private String xrefField;
		private List<QueryRule> filters;
		private List<String> xrefLabels = new ArrayList<String>();
		private String error;
		private boolean includeAddButton;
		private boolean nillable = false;
		private String url = DEFAULT_URL;
		private boolean prefill = true;
		
		public Builder(Class<? extends Entity> xrefEntityClass, E value)
		{
			this.xrefEntityClass = xrefEntityClass;
			this.value = value;
		}

		public Builder<E> setXrefField(String xrefField) {
			this.xrefField = xrefField; return this;
		}
		
		public Builder<E> setError(String error) {
			this.error = error; return this;
		}

		public Builder<E> setXrefLabels(List<String> xrefLabels) {
			this.xrefLabels = xrefLabels; return this;
		}

		public Builder<E> setIncludeAddButton(boolean includeAddButton) {
			this.includeAddButton = includeAddButton; return this;
		}
		
		public Builder<E> setNillable(boolean nillable) {
			this.nillable = nillable; return this;
		}
		
		public Builder<E> setFilters(List<QueryRule> queryRules) {
			this.filters = queryRules; return this;
		}

		public Builder<E> setUrl(String url) {
			this.url = url; return this;
		}
	
		public Builder<E> setPrefill(boolean prefill) {
			this.prefill = prefill; return this;
		}
	}
	
	
	private static final String SELECT_SCRIPT;
	private static final String AJAX_CHOSEN;
	private static final String PRELOAD_SCRIPT;
	
	static {
		final ResourceBundle rb = ResourceBundle.getBundle("org.molgenis.framework.ui.html.ajaxchosen");
		SELECT_SCRIPT = rb.getString("select");
		AJAX_CHOSEN = rb.getString("ajaxChosen");
		PRELOAD_SCRIPT = rb.getString("preloadscript");
	}
	
	
	protected abstract String renderOptions();	
	//determines how ajax-chosen renders the select (multiple, search)
	protected abstract String getHtmlRefType();
	
	public static final String XREF_FIELD = "xrefField";
	public static final String XREF_ENTITY = "xrefEntity";
	public static final String XREF_LABELS = "xrefLabels";
	public static final String FILTERS = "filters";	
	public static final String SEARCH_TERM = "searchTerm";
	
	public static final String ERRROR = "error";
	public static final String INCLUDE_ADD_BUTTON = "includeAddButton";
	
	protected final Class<? extends Entity> xrefEntity;
	protected List<String> xrefLabels = new ArrayList<String>();
	protected String xrefField;
	protected String url = DEFAULT_URL;
	protected boolean prefill = true;
	private String placeholder = "";
	
	protected List<QueryRule> filters = new ArrayList<QueryRule>();
	
	protected String error = null;
	protected boolean includeAddButton = false;	
		
	protected AbstractRefInput(final Builder<E> builder, final String name) {
		super(name, builder.value);
		setValue(builder.value);
		xrefEntity = builder.xrefEntityClass;		
		setXrefEntity(builder.xrefEntityClass);
		url = builder.url;
		
		if(StringUtils.isNotEmpty(builder.xrefField)) { xrefField = builder.xrefField; }
		if(CollectionUtils.isNotEmpty(builder.xrefLabels)) { xrefLabels = builder.xrefLabels; }
		
		filters = builder.filters;
		error = builder.error;
		setNillable(builder.nillable);
		includeAddButton = builder.includeAddButton;
		prefill = builder.prefill;		
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	protected AbstractRefInput(String name, Class<? extends Entity> xrefClass, String label, E value, 
			boolean nillable, boolean readonly, String description)
	{
		super(name, label, value, nillable, readonly, description);
		xrefEntity = xrefClass;
	}
	
	protected AbstractRefInput(String name, Class<? extends Entity> xrefClass, E value)
	{
		super(name, value);		
		xrefEntity = xrefClass;
	}
	
	@Deprecated
	protected AbstractRefInput()
	{
		xrefEntity = null;
	}
	
	@SuppressWarnings("unchecked")
	public AbstractRefInput(Tuple t) throws HtmlInputException
	{
		super(t);	
		Class<? extends Entity> klass = null;
		try
		{
			klass = (Class<? extends Entity>) Class.forName(t.getString(XREF_ENTITY));
		}
		catch (Exception e)
		{
			new HtmlInputException(e);
		}
		xrefEntity = klass;
		
		xrefField = t.getString(XREF_FIELD);
		xrefLabels = t.getStringList(XREF_LABELS);
		
		if(!t.isNull(FILTERS)) { filters = (List<QueryRule>) t.getList(FILTERS); }
		if(!t.isNull(ERRROR)) { error = t.getString(ERRROR); }
		if(!t.isNull(INCLUDE_ADD_BUTTON)) { includeAddButton = t.getBool(INCLUDE_ADD_BUTTON); }
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 * @throws HtmlInputException 
	 */
	@Deprecated
	protected void setXrefEntity(Class<? extends Entity> xrefEntity)
	{		
		try
		{
			Entity instance = xrefEntity.newInstance();
			this.xrefField = instance.getIdField();
			this.xrefLabels = instance.getLabelFields();
			this.placeholder = "Choose "+instance.getClass().getSimpleName();
			//this.xrefEntity = xrefEntity;
		}
		catch (Exception e)
		{
			this.error = e.getMessage();
			e.printStackTrace();
		}	
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 * @throws HtmlInputException 
	 */
	@SuppressWarnings({ "unchecked"})
	@Deprecated	
	public void setXrefEntity(String xrefClassname) throws HtmlInputException
	{
		try
		{
			this.setXrefEntity((Class<? extends Entity>) Class.forName(xrefClassname));
		}
		catch (ClassNotFoundException e)
		{
			new HtmlInputException(xrefClassname);
		}
	}

	public String getXrefField()
	{
		return xrefField;
	}

	/**
	 * Set the entity field (i.e. database column) that this xref should get its
	 * values from. For example 'id'.
	 * 
	 * @param xrefField
	 *            field name
	 */
	public void setXrefField(String xrefField)
	{
		this.xrefField = xrefField;
	}
	
	public Class<? extends Entity> getXrefEntity() {
		return xrefEntity;
	}

	public List<String> getXrefLabels()
	{
		return xrefLabels;
	}

	/**
	 * Set the entity field (i.e. database column) that provides the values that
	 * should be shown to the user as options in the xref select box. For
	 * example 'name'.
	 * 
	 * @param xrefLabel
	 *            field name
	 */
	public void setXrefLabel(String xrefLabel)
	{
		assert (xrefLabel != null);
		this.xrefLabels.clear();
		this.xrefLabels.add(xrefLabel);
	}

	/**
	 * In case of entities with multiple column keys you can also have multiple
	 * labels concatenated together. For example 'investigation_name, name'.
	 * 
	 * @param xrefLabels
	 *            a list of field names
	 */
	public void setXrefLabels(List<String> xrefLabels)
	{
		assert (xrefLabels != null);
		this.xrefLabels = xrefLabels;
	}

	public List<QueryRule> getXrefFilters()
	{
		return filters;
	}

	public void setXrefFilters(List<QueryRule> xrefFilter)
	{
		this.filters = xrefFilter;
	}
	
	public String getXrefFilterRESTString()
	{
		return QueryRuleUtil.toRESTstring(filters);
	}

	public String getXrefEntitySimpleName()
	{
		String name = xrefEntity.getSimpleName();
		if (name.contains(".")) return name.substring(name.lastIndexOf(".")+1	);
		return name;
	}

	public ActionInput createAddButton()
	{
		ActionInput addButton = new ActionInput("add", "", "");
		
		addButton.setId(this.getId() + "_addbutton");
		addButton.setButtonValue("Add new " + this.getXrefEntitySimpleName());
		addButton.setIcon("generated-res/img/new.png");
		
		addButton
		.setJavaScriptAction("if( window.name == '' ){ window.name = 'molgenis_"+AbstractMolgenisServlet.getNewWindowId()+"';}document.getElementById('"
				+ this.getId()
				+ "').form.__action.value='"
				+ this.getId()
				+ "';molgenis_window = window.open('','molgenis_edit_new_xref','height=800,width=600,location=no,status=no,menubar=no,directories=no,toolbar=no,resizable=yes,scrollbars=yes');document.getElementById('"
				+ this.getId()
				+ "').form.target='molgenis_edit_new_xref';document.getElementById('"
				+ this.getId()
				+ "').form.__show.value='popup';document.getElementById('"
				+ this.getId()
				+ "').form.submit();molgenis_window.focus();");
		return addButton;
	}

	public void setIncludeAddButton(boolean includeAddButton)
	{
		this.includeAddButton = includeAddButton;
	}	

	public final String toJquery(String htmlOptions, String xrefLabelString)
	{
		if(this.isHidden())
		{
			return "<input type=\"hidden\" value=\"+getValue()+\"/>";
		}
		
		 
		final String cssClasses = String.format("%s %s", 
				this.isReadonly() ? "readonly " : "",
				this.isNillable() ? "" : "required ");
		
		final JSONObject data = new JSONObject();
		try
		{
			data.put(XREF_ENTITY, this.getXrefEntity().getName());
			data.put(XREF_FIELD, this.getXrefField());
			data.put(XREF_LABELS, xrefLabelString);
			data.put(NILLABLE, isNillable());
			
			final boolean hasFilters = getXrefFilters() != null && getXrefFilters().size() > 0;
			if(hasFilters) {
				data.put(FILTERS, new Gson().toJson(getXrefFilters()));
			}			
		}
		catch (JSONException e)
		{
			throw new RuntimeException(e);
		}		
		
//		#arg1 = id
//		#arg2 = title
//		#arg3 = input type (xref=""?? or mref="multiple")
//		#arg4 = dataplaceHolder (is this needed?)
//		#arg5 = class(es) --> add required class or none class or extra classes
//		#arg6 = options for the select
		final String select = String.format(SELECT_SCRIPT, 
				getId(), getDescription(), getHtmlRefType(), placeholder, cssClasses, htmlOptions);

//		#arg1 = id
//		#arg2 = url of service
//		#arg3 = data
//		#arg4 = SEARCH_TERM
//		#arg5 = prefillScript (see below in this file)	
//		#arg6 = htmlRefType determines if it's a xref or mref box		
		final String ajaxChosenScript = String.format(AJAX_CHOSEN, 
			getId(), url, data.toString(), SEARCH_TERM, prefillScript()
		); 
		
//		return "<select data-placeholder=\"Choose some "
//				+ name
//				+ "\" "
//				+ readonly
//				+ " class=\""
//				+ readonly
//				+ required
//				+ "ui-widget-content ui-corner-all\" id=\""
//				+ this.getId()
//				+ "\" name=\""
//				+ this.getName()
//				+ "\" "
//				+ " style=\"width:350px;\" "
//				+ description
//				+ " " +getHtmlRefType() +">\n"
//				+ htmlOptions
//				+ "</select>" 
//		final String ajaxChoosenScript = "\n<script>$(\"#"
//				+ this.getId()
//				+ "\").ajaxChosen("
//				+ "\n{ "
//				+ "\n	method: 'GET', "
//				+ "\n	url: '" +url + "',"
//				+ String.format("\n data: data = %s ,", data.toString()) 
//				+ "\n	dataType: 'json', "
//				+ "\n	minTermLength: 0, "
//				+ "\n	afterTypeDelay: 300, "
//				+ String.format("\n	jsonTermKey: 'searchTerm', ", SEARCH_TERM)
//				+ "\n},"
//				+ "\nfunction (data) {"
//				+ "\n	var terms = {}; "
//				+ "\n	$.each(data, function (i, val) {terms[i] = val;});"
//				+ "\n	return terms;"
//				+ "\n});"
//				+ "\n"
//				+ prefillScript() + "\n"
//				+ "</script>\n"
				
		final String includeButton = includeAddButton && !this.isReadonly() ? this.createAddButton().toString() : "";
		return select + ajaxChosenScript + includeButton;
	}
	
	public String prefillScript() {
		return String.format(PRELOAD_SCRIPT, getId());
	}
	
	@Override
	public String toHtml()
	{
		if (this.error != null) return "ERROR: " + error;

		if ("".equals(getXrefEntity()) || "".equals(getXrefField())
				|| getXrefLabels() == null || getXrefLabels().size() == 0)
		{
			throw new RuntimeException(
					"XrefInput("
							+ this.getName()
							+ ") is missing xrefEntity, xrefField and/or xrefLabels settings");
		}

		final String xrefLabelString = StringUtils.join(getXrefLabels(), ",");
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" "
				: String.format(
						" onfocus=\"showXrefInput(this,'%s','%s','%s','%s'); return false;\" ",
						getXrefEntity(), getXrefField(), xrefLabelString,
						getXrefFilters());

		//Hidden optionsbox doesn't make sense.
		//Hidden fields are used to submit data to server, but which option is selected in a hidden field? 
//		if (this.isHidden())
//		{
//			StringInput input = new StringInput(this.getName(), super.getValue());
//
//			input = new StringInput(this.getName(), super.getObject().getIdValue().toString());
//
//			input.setLabel(this.getLabel());
//			input.setDescription(this.getDescription());
//			input.setHidden(true);
//			return input.toHtml();
//		}

		String optionsHtml = StringUtils.EMPTY;
		if (super.getObject() != null)
		{
			optionsHtml = renderOptions();
		}

		if (this.uiToolkit == UiToolkit.ORIGINAL)
		{
			final String htmlSelect = "<select id=\""
					+ this.getId()
					+ "\" name=\""
					+ this.getName()
					+ "\" "
					+ readonly
					+ ">\n"
					+ renderOptions()
					+ "</select>\n"
					+ (includeAddButton && !this.isReadonly() ? this
							.createAddButton() : "");
			return htmlSelect;
		}
		else if (this.uiToolkit == UiToolkit.JQUERY)
		{
			return toJquery(optionsHtml, xrefLabelString);
		}
		else
		{
			return "NOT IMPLEMENTED FOR LIBRARY " + this.uiToolkit;
		}
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public boolean isPrefill()
	{
		return prefill;
	}
	public void setPrefill(boolean prefill)
	{
		this.prefill = prefill;
	}
	
	public String getPlaceholder()
	{
		return this.placeholder;
	}
	
	public void setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
	}
	
}
