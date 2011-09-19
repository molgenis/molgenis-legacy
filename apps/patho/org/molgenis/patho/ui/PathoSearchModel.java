/*
 * Date: September 5, 2011 Template: EasyPluginModelGen.java.ftl generator:
 * org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.patho.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.matrix.component.PhenoMatrix;
import org.molgenis.variant.Chromosome;

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
	private int selectedPos;
	private int selectedRange;
	private PhenoMatrix resultMatrix;

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

	public int getSelectedPos()
	{
		return selectedPos;
	}

	public void setSelectedPos(int selectedPos)
	{
		this.selectedPos = selectedPos;
	}

	public int getSelectedRange()
	{
		return selectedRange;
	}

	public void setSelectedRange(int selectedRange)
	{
		this.selectedRange = selectedRange;
	}

	public PhenoMatrix getVisibleMatrix()
	{
		return resultMatrix;
	}

	public void setVisibleMatrix(PhenoMatrix visibleMatrix)
	{
		this.resultMatrix = visibleMatrix;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public int getSelectedChrId()
	{
		return selectedChrId;
	}

	public void setSelectedChrId(int selectedChrId)
	{
		this.selectedChrId = selectedChrId;
	}

}
