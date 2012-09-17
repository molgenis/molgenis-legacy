/*
 * Date: September 5, 2011 Template: EasyPluginModelGen.java.ftl generator:
 * org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.patho.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.SequenceVariant;

/**
 * PathoSearchModel allows the user to choose chromosome, startpos and range.
 * Based on this it will create a matrix with rows = SequenceFeature, column =
 * ObservableFeature. The data is sourced from ObservableFeature(target=Panel,
 * feature=SequenceFeature, relation=ObservableFeature).
 */
public class PathoSearchModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;

	// lookup list
	private List<String> chromosomes = new ArrayList<String>();

	// selected parameters
	private int selectedChrId;
	private int selectedFrom;
	private int selectedTo;
	
	// results
	private int count;
	private List<SequenceVariant> variants;
	private Map<String,ObservedValue> alleleCounts;

	public PathoSearchModel(PathoSearch controller)
	{
		super(controller);
	}

	public List<String> getChromosomes()
	{
		return chromosomes;
	}

	public void setChromosomes(List<String> chromosomes)
	{
		this.chromosomes = chromosomes;
	}

	protected int getSelectedChrId()
	{
		return selectedChrId;
	}

	protected void setSelectedChrId(int selectedChrId)
	{
		this.selectedChrId = selectedChrId;
	}

	protected int getSelectedFrom()
	{
		return selectedFrom;
	}

	protected void setSelectedFrom(int selectedFrom)
	{
		this.selectedFrom = selectedFrom;
	}

	protected int getSelectedTo()
	{
		return selectedTo;
	}

	protected void setSelectedTo(int selectedTo)
	{
		this.selectedTo = selectedTo;
	}

	protected int getCount()
	{
		return count;
	}

	protected void setCount(int count)
	{
		this.count = count;
	}

	protected Map<String,ObservedValue> getAlleleCounts()
	{
		return alleleCounts;
	}

	protected void setAlleleCounts(Map<String,ObservedValue> alleleCounts)
	{
		this.alleleCounts = alleleCounts;
	}

	protected List<SequenceVariant> getVariants()
	{
		return variants;
	}

	protected void setVariants(List<SequenceVariant> variants)
	{
		this.variants = variants;
	}
	
	

}
