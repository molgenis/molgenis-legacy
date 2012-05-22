package org.molgenis.datatable.view;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.model.elements.Field;

public class JQGridColumn extends Field {
	private static final long serialVersionUID = -4066360076845440322L;

	public JQGridColumn(Field field) {
		super(field);
		
	}
	
	public String getColumnType() {
		final FieldTypeEnum fieldType = super.getType().getEnumType();
		switch(fieldType) {
			case DATE: return ",date: true";
			case DATE_TIME: return ",date: true, time: true";
			case DECIMAL: return ",number: 'true'";
			//case ENUM: return "";
			case INT: return ",integer: 'true'";
			case LONG: return ",integer: 'true'";
			default:
				return ""; //handle as text
		}
	}
}