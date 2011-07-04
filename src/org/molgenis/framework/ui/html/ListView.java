package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.Tuple;

/**
 * (in progress) The listview shows a list of InputForm (sets of inputs) in an excel like
 * view:
 * <ul>
 * <li>input labels as column headers
 * <li>each row rending the value of the input.
 * <li>each InputForm is assumed to have the same set of inputs.
 * <li>each InputForm may have getActions(); those are shown in first column rendered as icon
 * <li>if this list isSelectable (default), each row will have checkbox before each row.
 * <li>if this list isReadonly (default) then each row will show only values, otherwise inputs
 * TODO make easier to populate with entity instances.
 */
public class ListView extends HtmlInput<List<HtmlForm>>
{
	List<HtmlForm> rows = new ArrayList<HtmlForm>();
	String sortedBy = null;
	QueryRule.Operator sortOrder = QueryRule.Operator.SORTDESC;
	boolean selectable = true;
	int offset = 0;
	
	public ListView(String name, List<HtmlForm> rows)
	{
		super(name, null);
		this.setRows(rows);
		this.setReadonly(true);
	}

	public ListView(String name)
	{
		super(name, null);
		this.setReadonly(true);
	}

	public void setRows(List<HtmlForm> rows)
	{
		if (rows == null) throw new IllegalArgumentException(
				"GridPanel.setRows cannot be null");
		this.rows = rows;
	}

	public List<HtmlForm> getRows()
	{
		return this.rows;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(List<HtmlForm> value)
	{
		if (value instanceof List)
		{
			this.setRows((List<HtmlForm>) value);
		}
	}

	@Override
	public String getValue()
	{
		return this.toHtml();
	}

	public String getSortedBy()
	{
		return sortedBy;
	}

	public void setSortedBy(String sortedBy)
	{
		this.sortedBy = sortedBy;
	}

	public QueryRule.Operator getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(QueryRule.Operator sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	public void addRow(HtmlForm... forms)
	{
		for (HtmlForm f : forms)
		{
			this.getRows().add(f);
		}

	}
	
	

	public boolean isSelectable()
	{
		return selectable;
	}

	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}
	
	

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	@Override
	public String toHtml()
	{
		// todo: we need to include sortaction in header!!!

		// initial layout based on FormView.ftl@listview\

		// include hidden inputs for the sortaction

		// start table rendering
		String result = "<table class=\"listtable\">";
		int rowNo = 1;
		for (HtmlForm form : this.getRows())
		{
			if (rowNo == 1)
			{
				// todo: add actions on the headers for sorting

				// render header, only first row
				result += "<tr><th><label>&nbsp;</label></th><th><label>&nbsp;</label></th><th><label>&nbsp;</label></th>";
				for (HtmlInput input : form.getInputs())
				{

					if (!input.isHidden())
					{
						// each label has a sort action
						ActionInput sortAction = new ActionInput(
								QueryRule.Operator.SORTASC
										.equals(getSortOrder()) ? "sortDesc"
										: "sortAsc");
						sortAction.setLabel(input.getLabel());
						sortAction.setDescription(input.getDescription());

						// TODO: use central icon map so we can skin it
						sortAction
								.setIcon(QueryRule.Operator.SORTASC
										.equals(getSortOrder()) ? "generated-res/img/sort_desc.gif"
										: "generated-res/img/sort_asc.gif");

						// TODO: pass which input to sort on as additional
						// parameter
						// sortInput.setParameter("__sortattribute",input.getName());

						result += "<th><label class=\"tableheader\">"
								+ sortAction.toLinkHtml()+(input.getName().equals(this.getSortedBy()) ? sortAction.getIconHtml() : "")
								+ "</label></th>";
					}
				}
				result += "</tr>";
			}

			// render each row, using different class to allow for alternating
			// colour
			result += "<tr class=\"form_listrow" + rowNo % 2 + "\">";
			
			//offset
			result += "<td>"+(getOffset() + rowNo)+".</td>";
			
			//checkbox
			OnoffInput checkbox = new OnoffInput("massUpdate","TODO", false);
			result += "<td>"+(isSelectable() ? checkbox.toHtml() : "")+"</td>";
			
			//render action buttons per row
			result += "<td>";
			for (ActionInput action : form.getActions())
			{
				result += action.toIconHtml();
			}
			result += "</td>";
			
			//render other inputs
			for (HtmlInput input : form.getInputs())
			{
				if (!input.isHidden())
				{
					result += "<td title=\"" + input.getDescription() + "\">"
							+ (isReadonly() ? input.getValue() : input.toHtml()) + "</td>";
				}
			}
			result += "</tr>";

			rowNo++;

		}

		// render selectall
		result += "<tr><td></td><td><input title=\"select all visible\" type=\"checkbox\" name=\"checkall\" id=\"checkall\" onclick=\"Javascript:checkAll('TODO','massUpdate')\"/></td></tr>";
		
		
		result += "</table>";

		return result;
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		//TODO
		throw new UnsupportedOperationException();
	}

}
