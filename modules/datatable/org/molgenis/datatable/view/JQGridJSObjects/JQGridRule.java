package org.molgenis.datatable.view.JQGridJSObjects;

//{"field":"Country.Code","op":"eq","data":"AGO"}
public class JQGridRule {
	public enum JQGridOp {
		eq, ne, lt, le, gt, ge, bw, bn, in, ni, ew, en, cn, nc
	}

	public JQGridRule(String field, JQGridOp op, String data) {
		this.field = field;
		this.op = op;
		this.data = data;
	}

	public String field;
	public JQGridOp op;
	public String data;
}
