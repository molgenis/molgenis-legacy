package org.molgenis.mutation.vo;

import java.io.Serializable;

public class PhenotypeSearchCriteriaVO implements Serializable
{
	private static final long serialVersionUID = 1321046268960970013L;
	Integer phenotypeId;
	String name;

	public Integer getPhenotypeId() {
		return phenotypeId;
	}
	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
