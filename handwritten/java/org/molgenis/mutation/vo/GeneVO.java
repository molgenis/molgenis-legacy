
/* File:        col7a1/model/Gene.java
 * Copyright:   GBIC 2000-2,010, all rights reserved
 * Date:        April 12, 2010
 * Generator:   org.molgenis.generators.DataTypeGen 3.3.2-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.mutation.vo;

public class GeneVO
{
	private Integer _id = null;
	private String _name = null;
	private String _chromosome = null;
	private String _position = null;
	private String _nuclSequence = null;
	private String _aaSequence = null;
	private String _genbankId = null;
	private String _genomeBuild = null;

	//constructors
	public GeneVO()
	{
	
	}

	//getters and setters
	
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
	 * Get the Name of the gene..
	 * @return name.
	 */
	public String getName()
	{
		return this._name;
	}
	
	/**
	 * Set the Name of the gene..
	 * @param _name
	 */
	public void setName(String _name)
	{
		this._name = _name;
	}
	

	/**
	 * Get the Chromosome of gene..
	 * @return chromosome.
	 */
	public String getChromosome()
	{
		return this._chromosome;
	}
	
	/**
	 * Set the Chromosome of gene..
	 * @param _chromosome
	 */
	public void setChromosome(String _chromosome)
	{
		this._chromosome = _chromosome;
	}
	

	/**
	 * Get the Position on the chromosome..
	 * @return position.
	 */
	public String getPosition()
	{
		return this._position;
	}
	
	/**
	 * Set the Position on the chromosome..
	 * @param _position
	 */
	public void setPosition(String _position)
	{
		this._position = _position;
	}
	

	/**
	 * Get the Sequences (bases) of the gene..
	 * @return sequence.
	 */
	public String getNuclSequence()
	{
		return this._nuclSequence;
	}
	
	/**
	 * Set the Sequences (bases) of the gene..
	 * @param _sequence
	 */
	public void setNuclSequence(String _nuclSequence)
	{
		this._nuclSequence = _nuclSequence;
	}


	/**
	 * Get the Sequences (bases) of the gene..
	 * @return sequence.
	 */
	public String getAaSequence()
	{
		return this._aaSequence;
	}
	
	/**
	 * Set the Sequences (bases) of the gene..
	 * @param _sequence
	 */
	public void setAaSequence(String _aaSequence)
	{
		this._aaSequence = _aaSequence;
	}


	/**
	 * Get the Genbank identifier..
	 * @return genbankId.
	 */
	public String getGenbankId()
	{
		return this._genbankId;
	}
	
	/**
	 * Set the Genbank identifier..
	 * @param _genbankId
	 */
	public void setGenbankId(String _genbankId)
	{
		this._genbankId = _genbankId;
	}
	

	/**
	 * Get the Genbank genome build..
	 * @return genomeBuild.
	 */
	public String getGenomeBuild()
	{
		return this._genomeBuild;
	}
	
	/**
	 * Set the Genbank genome build..
	 * @param _genomeBuild
	 */
	public void setGenomeBuild(String _genomeBuild)
	{
		this._genomeBuild = _genomeBuild;
	}
}

