
/* File:        col7a1/model/Gene.java
 * Copyright:   GBIC 2000-2,010, all rights reserved
 * Date:        April 12, 2010
 * Generator:   org.molgenis.generators.DataTypeGen 3.3.2-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.mutation.dto;

import java.io.Serializable;

public class GeneDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 8448620183887771179L;
	private Integer id;
	private String name;
	private String chromosome;
	private String position;
	private String nuclSequence;
	private String aaSequence;
	private Integer bpStart;
	private Integer bpEnd;
	private Integer length;
	private String genbankId;
	private String genomeBuild;
	private String orientation;
	private String symbol;

	//constructors
	public GeneDTO()
	{
	
	}

	//getters and setters
	
	public Integer getBpStart() {
		return bpStart;
	}

	public void setBpStart(Integer bpStart) {
		this.bpStart = bpStart;
	}

	public Integer getBpEnd() {
		return bpEnd;
	}

	public void setBpEnd(Integer bpEnd) {
		this.bpEnd = bpEnd;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * Get the Primary key..
	 * @return id.
	 */
	public Integer getId()
	{
		return this.id;
	}
	
	/**
	 * Set the Primary key..
	 * @param _id
	 */
	public void setId(Integer _id)
	{
		this.id = _id;
	}
	

	/**
	 * Get the Name of the gene..
	 * @return name.
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Set the Name of the gene..
	 * @param _name
	 */
	public void setName(String _name)
	{
		this.name = _name;
	}
	

	/**
	 * Get the Chromosome of gene..
	 * @return chromosome.
	 */
	public String getChromosome()
	{
		return this.chromosome;
	}
	
	/**
	 * Set the Chromosome of gene..
	 * @param _chromosome
	 */
	public void setChromosome(String _chromosome)
	{
		this.chromosome = _chromosome;
	}
	

	/**
	 * Get the Position on the chromosome..
	 * @return position.
	 */
	public String getPosition()
	{
		return this.position;
	}
	
	/**
	 * Set the Position on the chromosome..
	 * @param _position
	 */
	public void setPosition(String _position)
	{
		this.position = _position;
	}
	

	/**
	 * Get the Sequences (bases) of the gene..
	 * @return sequence.
	 */
	public String getNuclSequence()
	{
		return this.nuclSequence;
	}
	
	/**
	 * Set the Sequences (bases) of the gene..
	 * @param _sequence
	 */
	public void setNuclSequence(String _nuclSequence)
	{
		this.nuclSequence = _nuclSequence;
	}


	/**
	 * Get the Sequences (bases) of the gene..
	 * @return sequence.
	 */
	public String getAaSequence()
	{
		return this.aaSequence;
	}
	
	/**
	 * Set the Sequences (bases) of the gene..
	 * @param _sequence
	 */
	public void setAaSequence(String _aaSequence)
	{
		this.aaSequence = _aaSequence;
	}


	/**
	 * Get the Genbank identifier..
	 * @return genbankId.
	 */
	public String getGenbankId()
	{
		return this.genbankId;
	}
	
	/**
	 * Set the Genbank identifier..
	 * @param _genbankId
	 */
	public void setGenbankId(String _genbankId)
	{
		this.genbankId = _genbankId;
	}
	

	/**
	 * Get the Genbank genome build..
	 * @return genomeBuild.
	 */
	public String getGenomeBuild()
	{
		return this.genomeBuild;
	}
	
	/**
	 * Set the Genbank genome build..
	 * @param _genomeBuild
	 */
	public void setGenomeBuild(String _genomeBuild)
	{
		this.genomeBuild = _genomeBuild;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}

