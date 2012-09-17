package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class QueryParametersVO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1381420122055698282L;
	private Boolean expertSearch    = false;
	private Integer mutationId      = 0;
	private String mutationName     = "";
	private String mutationPosition = "";
	private String mutationType     = "";
	private String genbankAccNo     = "";
	private String aaNo             = "";
	private Integer exonId          = 0;
	private String exonNumber       = "";
	private Integer proteinDomainId = 0;
	private String searchTerm       = "";
	private Boolean showSNP         = true;
	private Boolean showIntrons     = true;
	private Boolean showNames       = true;
	private Boolean showNumbering   = true;
	private Boolean showMutations   = true;

	public void init()
	{
		this.mutationId       = 0;
		this.mutationName     = "";
		this.mutationPosition = "";
		this.mutationType     = "";
		this.genbankAccNo     = "";
		this.aaNo             = "";
		this.exonId           = 0;
		this.exonNumber       = "";
		this.proteinDomainId  = 0;
		this.searchTerm       = "";
	}

	public Boolean getExpertSearch()
	{
		return this.expertSearch;
	}
	
	public void setExpertSearch(Boolean expertSearch)
	{
		this.expertSearch = expertSearch;
	}

	public Integer getMutationId()
	{
		return this.mutationId;
	}
	
	public void setMutationId(Integer mutationId)
	{
		this.mutationId = mutationId;
	}
	
	public String getMutationName()
	{
		return this.mutationName;
	}
	
	public void setMutationName(String mutationName)
	{
		this.mutationName = mutationName;
	}
	
	public String getMutationPosition()
	{
		return this.mutationPosition;
	}
	
	public void setMutationPosition(String mutationPosition)
	{
		this.mutationPosition = mutationPosition;
	}
	
	public String getMutationType()
	{
		return this.mutationType;
	}
	
	public void setMutationType(String mutationType)
	{
		this.mutationType = mutationType;
	}
	
	public String getGenbankAccNo()
	{
		return this.genbankAccNo;
	}
	
	public void setGenbankAccNo(String genbankAccNo)
	{
		this.genbankAccNo = genbankAccNo;
	}
	
	public String getAaNo()
	{
		return this.aaNo;
	}
	
	public void setAaNo(String aaNo)
	{
		this.aaNo = aaNo;
	}
	
	public Integer getExonId()
	{
		return this.exonId;
	}
	
	public void setExonId(Integer exonId)
	{
		this.exonId = exonId;
	}
	
	public String getExonNumber()
	{
		return this.exonNumber;
	}
	
	public void setExonNumber(String exonNumber)
	{
		this.exonNumber = exonNumber;
	}
	
	public Integer getProteinDomainId()
	{
		return this.proteinDomainId;
	}
	
	public void setProteinDomainId(Integer proteinDomainId)
	{
		this.proteinDomainId = proteinDomainId;
	}
	
	public String getSearchTerm()
	{
		return this.searchTerm;
	}
	
	public void setSearchTerm(String searchTerm)
	{
		this.searchTerm = searchTerm;
	}

	public Boolean getShowSNP()
	{
		return this.showSNP;
	}
	
	public void setShowSNP(Boolean showSNP)
	{
		this.showSNP = showSNP;
	}
	
	public Boolean getShowIntrons()
	{
		return this.showIntrons;
	}
	
	public void setShowIntrons(Boolean showIntrons)
	{
		this.showIntrons = showIntrons;
	}
	
	public Boolean getShowNames()
	{
		return this.showNames;
	}
	
	public void setShowNames(Boolean showNames)
	{
		this.showNames = showNames;
	}
	
	public Boolean getShowNumbering()
	{
		return this.showNumbering;
	}
	
	public void setShowNumbering(Boolean showNumbering)
	{
		this.showNumbering = showNumbering;
	}
	
	public Boolean getShowMutations()
	{
		return this.showMutations;
	}
	
	public void setShowMutations(Boolean showMutations)
	{
		this.showMutations = showMutations;
	}
	
	public String printSearchOptions()
	{
		List<String> result = new ArrayList<String>();
		
		if (StringUtils.isNotEmpty(this.mutationName))
			result.add("variation = '" + this.mutationName + "'");
		if (StringUtils.isNotEmpty(this.mutationPosition))
			result.add("nucleotide number = '" + this.mutationPosition + "'");
		if (StringUtils.isNotEmpty(this.mutationType))
			result.add("mutation type = '" + this.mutationType + "'");
		if (StringUtils.isNotEmpty(this.aaNo))
			result.add("amino acid number = '" + this.aaNo + "'");
		if (StringUtils.isNotEmpty(this.exonNumber))
			result.add("exon/intron = '" + this.exonNumber + "'");
		if (StringUtils.isNotEmpty(this.searchTerm))
			result.add("search term = '" + this.searchTerm + "'");
		
		return StringUtils.join(result, " or ");
	}
}
