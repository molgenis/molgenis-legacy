package org.molgenis.gonl.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.SequenceVariant;

public class GonlSearchView implements ScreenView
{
	GonlSearchModel model;

	public GonlSearchView(GonlSearchModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm form = new MolgenisForm(this.model);

		form.add(new Paragraph(
				"Welcome to the first alpha release of GoNL variants. " 
					+ "This release contains only SNPs and is based on 500 independent Dutch individuals (250 males + 250 females). "
					+ "Future releases will also contain other types of genetic variation and will be based on enhanced filtering using trio-aware variant calling on 250 trios. "
					+ "For more information visit <a href=\"http://www.nlgenome.nl\">www.nlgenome.nl</a>. "
					+ "Use GRCh37 / HG19 genomic coordinates to find matching SNPs."));

		form.add(new Newline());

		// provide a search box
		SelectInput chromosomes = new SelectInput("chromosome",
				model.getSelectedChrName());
		chromosomes.setOptions(model.getChromosomes(), model.getChromosomes());
		chromosomes.setNillable(false);
		form.add(chromosomes);

		// provide a 'from' box
		form.add(new IntInput("from", model.getSelectedFrom()));

		// provide a 'range' box
		form.add(new IntInput("to", model.getSelectedTo()));

		form.add(new ActionInput("search"));

		// provide a box with the current results
		form.add(new Newline());

		if (model.getVariants() != null && model.getAlleleCounts() != null)
		{
			JQueryDataTable table = new JQueryDataTable("Result");
			table.addColumn("Panel");
			table.addColumn("Chr");
			table.addColumn("Pos");
			table.addColumn("Ref");
			table.addColumn("Alt");
			table.addColumn("AlleleCount");

			for (SequenceVariant v : model.getVariants())
			{
				ObservedValue alleleCount = model.getAlleleCounts().get(
						v.getName());
				int row = table.addRow(v.getName());

				table.setCell(0, row, alleleCount.getTarget_Name());
				table.setCell(1, row, v.getChr_Name());
				table.setCell(2, row, v.getStartBP());
				table.setCell(3, row, v.getRef());
				table.setCell(4, row, v.getAlt());
				table.setCell(5, row, alleleCount.getValue());

			}

			form.add(table);
		}

		return form.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return null;
	}

}
