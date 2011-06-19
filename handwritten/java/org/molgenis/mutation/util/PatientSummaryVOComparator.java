package org.molgenis.mutation.util;

import java.util.Comparator;

import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.PatientSummaryVO;


public class PatientSummaryVOComparator implements Comparator<PatientSummaryVO> {

	@Override
	public int compare(PatientSummaryVO o1, PatientSummaryVO o2)
	{
		Integer gdnaPosition1 = o1.getVariantSummaryVOList().get(0).getGdnaPosition();
		Integer gdnaPosition2 = o2.getVariantSummaryVOList().get(0).getGdnaPosition();
			
		if (gdnaPosition1.compareTo(gdnaPosition2) == 0)
		{
			if (o1.getVariantSummaryVOList().get(1) == null)
				return -1;
			else if (o2.getVariantSummaryVOList().get(1) == null)
				return 1;
			else
			{
				gdnaPosition1 = o1.getVariantSummaryVOList().get(1).getGdnaPosition();
				gdnaPosition2 = o2.getVariantSummaryVOList().get(1).getGdnaPosition();
			}
		}
		return gdnaPosition1.compareTo(gdnaPosition2) * -1; // gDNA position is descending
	}

}
