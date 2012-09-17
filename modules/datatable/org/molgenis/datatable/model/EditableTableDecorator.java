package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/** Will change all row values to become edit boxes */
public class EditableTableDecorator implements FilterableTupleTable {
	FilterableTupleTable decoratedTable;

	public EditableTableDecorator(FilterableTupleTable decoratedTable) {
		this.decoratedTable = decoratedTable;
	}

	@Override
	public List<Field> getColumns() throws TableException {
		return decoratedTable.getColumns();
	}

	@Override
	public List<Field> getAllColumns() throws TableException {
		return decoratedTable.getAllColumns();
	}

	@Override
	public List<Tuple> getRows() throws TableException {
		try {
			List<Tuple> editableRows = new ArrayList<Tuple>();

			for (Tuple t : this.decoratedTable.getRows()) {
				Tuple editable = new SimpleTuple();

				for (String field : t.getFieldNames()) {
					editable.set(field,
							"<input style=\"width:100%; padding:0px \" type=\"text\" value=\""
									+ t.getObject(field) + "\">");

				}

				// for (Field field : t.getFieldTypes()) {
				// HtmlInput<Object> i = (HtmlInput<Object>) field.getType()
				// .createInput(field.getName());
				// i.setValue(t.getObject(field.getName()));
				//
				// editable.set(field.getName(), i);
				// }
				editableRows.add(editable);
			}

			return editableRows;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TableException(e);
		}
	}

	@Override
	public Iterator<Tuple> iterator() {
		return decoratedTable.iterator();
	}

	@Override
	public void close() throws TableException {
		decoratedTable.close();
	}

	@Override
	public int getCount() throws TableException {
		return decoratedTable.getCount();
	}

	@Override
	public int getColCount() throws TableException {
		return decoratedTable.getColCount();
	}

	@Override
	public int getLimit() {
		return decoratedTable.getLimit();
	}

	@Override
	public int getColLimit() {
		return decoratedTable.getColLimit();
	}

	@Override
	public void setLimit(int limit) {
		decoratedTable.setLimit(limit);
	}

	@Override
	public void setColLimit(int limit) {
		decoratedTable.setColLimit(limit);
	}

	@Override
	public int getOffset() {
		return decoratedTable.getOffset();
	}

	@Override
	public int getColOffset() {
		return decoratedTable.getColOffset();
	}

	@Override
	public void setOffset(int offset) {
		decoratedTable.setOffset(offset);
	}

	@Override
	public void setColOffset(int offset) {
		decoratedTable.setColOffset(offset);
	}

	@Override
	@Deprecated
	public void setDb(Database db) {
		decoratedTable.setDb(db);
	}

	@Override
	public void reset() {
		decoratedTable.reset();
	}

	@Override
	public void setLimitOffset(int limit, int offset) {
		decoratedTable.setLimitOffset(limit, offset);

	}

	@Override
	public void setFilters(List<QueryRule> rules) throws TableException {
		decoratedTable.setFilters(rules);
	}

	@Override
	public List<QueryRule> getFilters() {
		return decoratedTable.getFilters();
	}

	@Override
	public QueryRule getSortRule() {
		return decoratedTable.getSortRule();
	}
}
