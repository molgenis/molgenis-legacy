package org.molgenis.mutation.util;

import java.util.Comparator;

import org.molgenis.mutation.vo.PatientSummaryVO;


public class PatientSummaryVOComparator implements Comparator<PatientSummaryVO> {

	@Override
	public int compare(PatientSummaryVO o1, PatientSummaryVO o2) {
		Integer gdnaPosition1 = o1.getMutation1().getGdna_Position();
		Integer gdnaPosition2 = o2.getMutation1().getGdna_Position();
		if (gdnaPosition1.compareTo(gdnaPosition2) == 0)
		{
			if (o1.getMutation2() == null)
				return -1;
			else if (o2.getMutation2() == null)
				return 1;
			else
			{
				gdnaPosition1 = o1.getMutation2().getGdna_Position();
				gdnaPosition2 = o2.getMutation2().getGdna_Position();
			}
		}
		return gdnaPosition1.compareTo(gdnaPosition2) * -1; // gDNA position is descending
	}

}
