
package org.molgenis.ngs.ui;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.TupleTable;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class AnalysisWorksheet extends EasyPluginController<AnalysisWorksheetModel>
{
	List<Tuple> sheet = new ArrayList<Tuple>();
	public AnalysisWorksheet(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AnalysisWorksheetModel(this)); //the default model
		this.setView(new FreemarkerView("AnalysisWorksheetView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	@Override
	public void reload(Database db) throws Exception
	{	
		String sql = "select study.identifier as project, ngsstudy.seqType, s.identifier as externalSampleId, machine.machine as sequencer, flowcell.runDate as sequencingStartDate, " +
				"flowcell.run, f.identifier as flowcell, l.lane, b.barcode, c.capturing as capturingKit " +
				"from study natural join ngsstudy " +
				"left join ngssample natural join characteristic s on ngssample.study=study.id " +
				"left join libraryLane l on l.sample=ngssample.id " +
				"left join flowcell natural join characteristic f on l.flowcell=f.id " +
				"left join libraryBarcode b on b.id=l.barcode " +
				"left join libraryCapturing c on c.id=l.capturing " +
				"left join machine on flowcell.machine=machine.id";
		
		List<Tuple> result = db.sql(sql);
		
		//postprocessing, sadly
		sheet = new ArrayList<Tuple>();
		int index = 0;
		for(Tuple t: result)
		{
			Tuple row = new SimpleTuple();
			row.set("select", "<input name=\"selectIndex\" type=\"checkbox\" value=\""+index+++"\"");
			row.set("project", t.getString("project"));
			row.set("seqType", t.getString("seqType"));
			row.set("externalSampleId", t.getString("externalSampleId"));
			row.set("sequencer", t.getString("sequencer"));
			row.set("sequencingStartDate", t.getString("sequencingStartDate"));
			row.set("flowcell", t.getString("flowcell"));
			row.set("lane", t.getString("lane"));
			row.set("barcode", t.getString("barcode"));
			row.set("capturingKit", t.getString("capturingKit"));
			sheet.add(row);
		}
	}
	
	@Override
	public String render()
	{
		MolgenisForm f = new MolgenisForm(this);
		
		f.add(new TupleTable("AnalysisWorksheetTable",sheet));
		
		f.add(new Paragraph(""));
		
		f.add(new ActionInput("download_txt_all", "Download all"));
		
		f.add(new ActionInput("download_txt_selected", "Download selected"));
		
		//this plus the strange names forces download, we need to make this better!
		f.add(new HiddenInput(FormModel.INPUT_SHOW, ScreenModel.Show.SHOW_DOWNLOAD));

		return f.render();
	}
	
	public void download_txt_all(Database db, Tuple request, OutputStream out)
	{
		this.download(db, request, out, null);
	}
	
	public void download_txt_selected(Database db, Tuple request,  OutputStream out)
	{
		this.download(db, request, out, request.getList("selectedIndex"));
	}
	
	private void download(Database db, Tuple request,  OutputStream out, List<?> indexes)
	{
		//skip first column
		List<String> headers = sheet.get(0).getFields().subList(1, sheet.get(0).getFields().size());
		
		CsvWriter writer = new CsvWriter(out);
		writer.setHeaders(headers);
		writer.writeHeader();
		for(int i = 0; i < sheet.size(); i++)
		{
			if(indexes == null || indexes.contains(""+i))
			{
				writer.writeRow(sheet.get(i));
			}
		}
		writer.close();
	}
}