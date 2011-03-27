package org.molgenis.mutation.vo;


public class ProteinDomainVO
{
	private Integer _id = null;
	private String _name = null;
	private String _superDomain = null;
	
	/**
	 * Get the Primary key..
	 * @return id.
	 */
	public Integer getId()
	{
		return this._id;
	}
	
	/**
	 * Set the Primary key..
	 * @param _id
	 */
	public void setId(Integer _id)
	{
		this._id = _id;
	}

	/**
	 * Get the Name of the protein domain. E.g. NC1 domain..
	 * @return name.
	 */
	public String getName()
	{
		return this._name;
	}
	
	/**
	 * Set the Name of the protein domain. E.g. NC1 domain..
	 * @param _name
	 */
	public void setName(String _name)
	{
		this._name = _name;
	}

	/**
	 * Get the superDomain.
	 * @return superDomain.
	 */
	public String getSuperDomain()
	{
		return this._superDomain;
	}
	
	/**
	 * Set the superDomain.
	 * @param _superDomain
	 */
	public void setSuperDomain(String _superDomain)
	{
		this._superDomain = _superDomain;
	}
}
