package org.molgenis.mutation.ui.upload.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.ValueLabel;

public class MutationForm extends Container
{
	public MutationForm()
	{
		Vector<ValueLabel> conservedaaOptions = new Vector<ValueLabel>();
		conservedaaOptions.add(new ValueLabel("conservedaa", ""));
		
		List<ValueLabel> splicingOptions      = new ArrayList<ValueLabel>();
		splicingOptions.add(new ValueLabel("No", "No"));
		splicingOptions.add(new ValueLabel("Unknown", "Yes: Unkown effect"));
		splicingOptions.add(new ValueLabel("Alternative protein", "Yes: Alternative protein"));
		splicingOptions.add(new ValueLabel("Premature termination codon", "Yes: Premature termination codon"));

		Vector<ValueLabel> founderOptions     = new Vector<ValueLabel>();
		founderOptions.add(new ValueLabel("foundermutation", ""));

		Vector<ValueLabel> snpOptions         = new Vector<ValueLabel>();
		snpOptions.add(new ValueLabel("reportedsnp", ""));

		this.add(new StringInput("gene"));
		((StringInput) this.get("gene")).setReadonly(true);
		this.add(new StringInput("refseq"));
		this.add(new StringInput("position")); // add onchange
		this.add(new StringInput("nt"));
		((StringInput) this.get("nt")).setReadonly(true);
		this.add(new SelectInput("event", "")); // add onchange
		this.add(new StringInput("length")); // TODO: does JavaScript work?
		((StringInput) this.get("length")).setStyle("display:none");
		((StringInput) this.get("length")).setId("length_input");
		this.add(new StringInput("ntchange")); // TODO: does JavaScript work?
		((StringInput) this.get("ntchange")).setStyle("display:none");
		((StringInput) this.get("ntchange")).setId("ntchange_input");
		this.add(new CheckboxInput("conservedaa", "", "Conserved amino acid?", conservedaaOptions, new Vector<String>()));
		this.add(new SelectInput("effectonsplicing", ""));
		((SelectInput) this.get("effectonsplicing")).setOptions(splicingOptions);
//		this.add(new CheckboxInput("effectonsplicing", "", "Effect on splicing?", splicingOptions, new Vector<String>()));
		this.add(new CheckboxInput("foundermutation", "", "Founder mutation?", founderOptions, new Vector<String>()));
		this.add(new StringInput("population"));
		this.add(new CheckboxInput("reportedsnp", "", "Reported as SNP?", snpOptions, new Vector<String>()));
		this.add(new SelectInput("inheritance", "recessive"));
		this.add(new StringInput("comment"));
		
		this.add(new StringInput("readonly_pos"));
		((StringInput) this.get("readonly_pos")).setReadonly(true);
		this.add(new SelectInput("exon", ""));
		this.add(new StringInput("nt_rep"));
		((StringInput) this.get("nt_rep")).setReadonly(true);
		this.add(new StringInput("readonly_ntchange"));
		((StringInput) this.get("readonly_ntchange")).setReadonly(true);
		this.add(new StringInput("codon_number"));
		((StringInput) this.get("codon_number")).setReadonly(true);
		this.add(new StringInput("codon_number_rep")); //TODO
		((StringInput) this.get("codon_number_rep")).setReadonly(true); //TODO
		this.add(new StringInput("codon"));
		((StringInput) this.get("codon")).setReadonly(true);
		this.add(new StringInput("codonchange"));
		this.add(new StringInput("aa"));
		((StringInput) this.get("aa")).setReadonly(true);
		this.add(new StringInput("aachange"));
		this.add(new StringInput("cdna_notation"));
		this.add(new StringInput("gdna_notation"));
		this.add(new StringInput("aa_notation"));
		this.add(new SelectInput("consequence", ""));
		this.add(new SelectInput("type", ""));
	}
}
