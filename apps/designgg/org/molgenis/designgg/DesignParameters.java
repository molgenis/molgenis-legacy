package org.molgenis.designgg;


import java.io.File;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.AbstractEntity;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * DesignParameters: .
 * @version November 9, 2007, 16:40:17 
 * @author MOLGENIS generator
 */
public class DesignParameters extends AbstractEntity  
{
	private static final long serialVersionUID = -3386893658115378318L;
	// member variables
	//Has the design take care of dual channels.
	private Boolean _twocolorarray = false;
	//Tab delimited data with genotypes as markers (rows) and individuals (cols)
	private String _genotype = null;
	private File _genotype_file = null;
	//Is factor 1 active
	private Boolean _factor1active = true;
	//Is factor 1 active
	private Boolean _factor2active = true;
	//Is factor 1 active
	private Boolean _factor3active = false;
	//Label of factor one
	private String _factor1label = null;
	//Label of factor two
	private String _factor2label = null;
	//Label of factor three
	private String _factor3label = null;
	//Levels of factor one, each item can be a number or a string
	private List<?> _factor1level = null;
	//Levels of factor two, each item can be a number or a string
	private List<?> _factor2level = null;
	//Levels of factor three, each item can be a number or a string
	private List<?> _factor3level = null;
	//weights for the model
	private List<?> _weight = null;
	//NTuple, average number of RILS per level (can be decimal)
	private Double _norilsperlevel = null;
	//Total number of slides
	private Double _noslides = null;
	//Number of iterations
	private Integer _noIterations = 1000;
	//Marker ranges
	private List<?> range_start;
	private List<?> range_end;

	//constructors
	public DesignParameters()
	{
	
	}
	
	//getters and setters
	
	/**
	 * Get the Has the design take care of dual channels..
	 * @return twocolorarray.
	 */
	public Boolean getTwoColorArray()
	{
		return this._twocolorarray;
	}
	
	/**
	 * Set the Has the design take care of dual channels..
	 * @param _twocolorarray
	 */
	public void setTwoColorArray(Boolean _twocolorarray)
	{
		this._twocolorarray = _twocolorarray;
	}	
	
	/**
	 * Get tha label for enum TwoColorArray.
	 */

	/**
	 * Get the Tab delimited data with genotypes as markers (rows) and individuals (cols).
	 * @return genotype.
	 */
	public String getGenotype()
	{
		return this._genotype;
	}
	
	/**
	 * Set the Tab delimited data with genotypes as markers (rows) and individuals (cols).
	 * @param _genotype
	 */
	public void setGenotype(String _genotype)
	{
		this._genotype = _genotype;
	}	
	
	/**
	 * Get tha label for enum Genotype.
	 */

	/**
	 * Genotype is a pointer to a file. getGenotypeAttachedFile() can be used to retrieve this file.
	 */
	public File getGenotypeAttachedFile()
	{
		return _genotype_file;
	}
	
	/**
	 * Genotype is a pointer to a file. Use setGenotypeAttachedFile() to attach this file so it can be 
	 * retrieved using getGenotypeAttachedFile().
	 */
	public void setGenotypeAttachedFile(File file)
	{
		_genotype_file = file;
	}
	/**
	 * Get the Is factor 1 active.
	 * @return factor1active.
	 */
	public Boolean getFactor1active()
	{
		return this._factor1active;
	}
	
	/**
	 * Set the Is factor 1 active.
	 * @param _factor1active
	 */
	public void setFactor1active(Boolean _factor1active)
	{
		this._factor1active = _factor1active;
	}	
	
	/**
	 * Get tha label for enum Factor1active.
	 */

	/**
	 * Get the Is factor 1 active.
	 * @return factor2active.
	 */
	public Boolean getFactor2active()
	{
		return this._factor2active;
	}
	
	/**
	 * Set the Is factor 1 active.
	 * @param _factor2active
	 */
	public void setFactor2active(Boolean _factor2active)
	{
		this._factor2active = _factor2active;
	}	
	
	/**
	 * Get tha label for enum Factor2active.
	 */

	/**
	 * Get the Is factor 1 active.
	 * @return factor3active.
	 */
	public Boolean getFactor3active()
	{
		return this._factor3active;
	}
	
	/**
	 * Set the Is factor 1 active.
	 * @param _factor3active
	 */
	public void setFactor3active(Boolean _factor3active)
	{
		this._factor3active = _factor3active;
	}	
	
	/**
	 * Get tha label for enum Factor3active.
	 */

	/**
	 * Get the Label of factor one.
	 * @return factor1label.
	 */
	public String getFactor1Label()
	{
		return this._factor1label;
	}
	
	/**
	 * Set the Label of factor one.
	 * @param _factor1label
	 */
	public void setFactor1Label(String _factor1label)
	{
		this._factor1label = _factor1label;
	}	
	
	/**
	 * Get tha label for enum Factor1Label.
	 */

	/**
	 * Get the Label of factor two.
	 * @return factor2label.
	 */
	public String getFactor2Label()
	{
		return this._factor2label;
	}
	
	/**
	 * Set the Label of factor two.
	 * @param _factor2label
	 */
	public void setFactor2Label(String _factor2label)
	{
		this._factor2label = _factor2label;
	}	
	
	/**
	 * Get tha label for enum Factor2Label.
	 */

	/**
	 * Get the Label of factor three.
	 * @return factor3label.
	 */
	public String getFactor3Label()
	{
		return this._factor3label;
	}
	
	/**
	 * Set the Label of factor three.
	 * @param _factor3label
	 */
	public void setFactor3Label(String _factor3label)
	{
		this._factor3label = _factor3label;
	}	
	
	/**
	 * Get tha label for enum Factor3Label.
	 */

	/**
	 * Get the Levels of factor one, each item can be a number or a string.
	 * @return factor1level.
	 */
	public List<?> getFactor1Level()
	{
		return this._factor1level;
	}
	
	/**
	 * Set the Levels of factor one, each item can be a number or a string.
	 * @param _factor1level
	 */
	public void setFactor1Level(List<?> _factor1level)
	{
		this._factor1level = _factor1level;
	}	
	
	/**
	 * Get tha label for enum Factor1Level.
	 */

	/**
	 * Get the Levels of factor two, each item can be a number or a string.
	 * @return factor2level.
	 */
	public List<?> getFactor2Level()
	{
		return this._factor2level;
	}
	
	/**
	 * Set the Levels of factor two, each item can be a number or a string.
	 * @param _factor2level
	 */
	public void setFactor2Level(List<?> _factor2level)
	{
		this._factor2level = _factor2level;
	}	
	
	/**
	 * Get tha label for enum Factor2Level.
	 */

	/**
	 * Get the Levels of factor three, each item can be a number or a string.
	 * @return factor3level.
	 */
	public List<?> getFactor3Level()
	{
		return this._factor3level;
	}
	
	/**
	 * Set the Levels of factor three, each item can be a number or a string.
	 * @param _factor3level
	 */
	public void setFactor3Level(List<?> _factor3level)
	{
		this._factor3level = _factor3level;
	}	
	
	/**
	 * Get tha label for enum Factor3Level.
	 */

	/**
	 * Get the NTuple, average number of RILS per level (can be decimal).
	 * @return norilsperlevel.
	 */
	public Double getNoRilsPerLevel()
	{
		return this._norilsperlevel;
	}
	
	/**
	 * Set the NTuple, average number of RILS per level (can be decimal).
	 * @param _norilsperlevel
	 */
	public void setNoRilsPerLevel(Double _norilsperlevel)
	{
		this._norilsperlevel = _norilsperlevel;
	}	
	
	/**
	 * Get tha label for enum NoRilsPerLevel.
	 */

	/**
	 * Get the Total number of slides.
	 * @return noslides.
	 */
	public Double getNoSlides()
	{
		return this._noslides;
	}
	
	/**
	 * Set the Total number of slides.
	 * @param _noslides
	 */
	public void setNoSlides(Double _noslides)
	{
		this._noslides = _noslides;
	}	
	
	/**
	 * Get tha label for enum NoSlides.
	 */

	/**
	 * @return the _noIterations
	 */
	public Integer getNoIterations() {
		return _noIterations;
	}

	/**
	 * @param iterations the _noIterations to set
	 */
	public void setNoIterations(Integer iterations) {
		_noIterations = iterations;
	}	

	/**
	 * Generic getter. Get the property by using the name.
	 */
	public Object get(String name)
	{
		name = name.toLowerCase();
		if (name.equals("twocolorarray"))
			return getTwoColorArray();
		if (name.equals("genotype"))
			return getGenotype();
		if (name.equals("factor1active"))
			return getFactor1active();
		if (name.equals("factor2active"))
			return getFactor2active();
		if (name.equals("factor3active"))
			return getFactor3active();
		if (name.equals("factor1label"))
			return getFactor1Label();
		if (name.equals("factor2label"))
			return getFactor2Label();
		if (name.equals("factor3label"))
			return getFactor3Label();
		if (name.equals("factor1level"))
			return getFactor1Level();
		if (name.equals("factor2level"))
			return getFactor2Level();
		if (name.equals("factor3level"))
			return getFactor3Level();
		if (name.equals("norilsperlevel"))
			return getNoRilsPerLevel();
		if (name.equals("noslides"))
			return getNoSlides();
		if (name.equals("noiterations"))
			return getNoIterations();		
		return "";
	}	
	
	public void validate() throws DatabaseException
	{
		if(this.getTwoColorArray() == null) throw new DatabaseException("required field twocolorarray is null");
		if(this.getGenotype() == null) throw new DatabaseException("required field genotype is null");
		if(this.getFactor1active() == null) throw new DatabaseException("required field factor1active is null");
		if(this.getFactor2active() == null) throw new DatabaseException("required field factor2active is null");
		if(this.getFactor3active() == null) throw new DatabaseException("required field factor3active is null");
		if(this.getFactor1Label() == null) throw new DatabaseException("required field factor1label is null");
		if(this.getFactor2Label() == null) throw new DatabaseException("required field factor2label is null");
		if(this.getFactor3Label() == null) throw new DatabaseException("required field factor3label is null");
		if(this.getFactor1Level() == null) throw new DatabaseException("required field factor1level is null");
		if(this.getFactor2Level() == null) throw new DatabaseException("required field factor2level is null");
		if(this.getFactor3Level() == null) throw new DatabaseException("required field factor3level is null");
		if(this.getNoRilsPerLevel() == null) throw new DatabaseException("required field norilsperlevel is null");
		if(this.getNoSlides() == null) throw new DatabaseException("required field noslides is null");
	}
	
	//@Implements
	public void set( Tuple tuple, boolean strict )  throws ParseException
	{
		if(tuple != null)
		{
			//set TwoColorArray
			if(tuple.getObject("twocolorarray") != null || strict) 
			{
				this.setTwoColorArray(tuple.getBoolean("twocolorarray"));
			}
			//set Genotype
			if(tuple.getObject("genotype") != null || strict) 
			{
				this.setGenotype(tuple.getString("genotype"));
				this.setGenotypeAttachedFile(tuple.getFile("filefor_genotype"));
			}
			//set Factor1active
			if(tuple.getObject("factor1active") != null || strict) 
			{
				this.setFactor1active(tuple.getBoolean("factor1active"));
			}
			//set Factor2active
			if(tuple.getObject("factor2active") != null || strict) 
			{
				this.setFactor2active(tuple.getBoolean("factor2active"));
			}
			//set Factor3active
			if(tuple.getObject("factor3active") != null || strict) 
			{
				this.setFactor3active(tuple.getBoolean("factor3active"));
			}
			//set Factor1Label
			if(tuple.getObject("factor1label") != null || strict) 
			{
				this.setFactor1Label(tuple.getString("factor1label"));
			}
			//set Factor2Label
			if(tuple.getObject("factor2label") != null || strict) 
			{
				this.setFactor2Label(tuple.getString("factor2label"));
			}
			//set Factor3Label
			if(tuple.getObject("factor3label") != null || strict) 
			{
				this.setFactor3Label(tuple.getString("factor3label"));
			}
			//set Factor1Level
			if(tuple.getObject("factor1level") != null || strict) 
			{
				this.setFactor1Level(tuple.getList("factor1level"));
			}
			//set Factor2Level
			if(tuple.getObject("factor2level") != null || strict) 
			{
				this.setFactor2Level(tuple.getList("factor2level"));
			}
			//set Factor3Level
			if(tuple.getObject("factor3level") != null || strict) 
			{
				this.setFactor3Level(tuple.getList("factor3level"));
			}
			//set Weights
			if(tuple.getObject("weight") != null || strict) 
			{
				this.setWeight(tuple.getList("weight"));
			}
			
			//set NoRilsPerLevel
			if(tuple.getObject("norilsperlevel") != null || strict) 
			{
				this.setNoRilsPerLevel(tuple.getDouble("norilsperlevel"));
			}
			//set NoSlides
			if(tuple.getObject("noslides") != null || strict) 
			{
				this.setNoSlides(tuple.getDouble("noslides"));
			}
			//set NoIterations
			if(tuple.getObject("noiterations") != null || strict) 
			{
				this.setNoIterations(tuple.getInt("noiterations"));
			}
			//set start_range
			if(tuple.getObject("range_start") != null || strict) 
			{
				this.setRangeStart(tuple.getList("range_start"));
			}
			if(tuple.getObject("range_end") != null || strict) 
			{
				this.setRangeEnd(tuple.getList("range_end"));
			}			
		}
		//org.apache.log4j.Logger.getLogger("test").debug("set "+this);
	}	

	@Override
	public String toString()
	{
		return "DesignParameters("
			+ "twocolorarray='" + getTwoColorArray()+"' "
			+ "genotype='" + getGenotype()+"' "
			+ "factor1active='" + getFactor1active()+"' "
			+ "factor2active='" + getFactor2active()+"' "
			+ "factor3active='" + getFactor3active()+"' "
			+ "factor1label='" + getFactor1Label()+"' "
			+ "factor2label='" + getFactor2Label()+"' "
			+ "factor3label='" + getFactor3Label()+"' "
			+ "factor1level='" + getFactor1Level()+"' "
			+ "factor2level='" + getFactor2Level()+"' "
			+ "factor3level='" + getFactor3Level()+"' "
			+ "norilsperlevel='" + getNoRilsPerLevel()+"' "
			+ "noslides='" + getNoSlides()+"'"
			+ "noiterations='" + getNoIterations()+"'"
			+ ");";
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!DesignParameters.class.equals(other.getClass()))
			return false;
		DesignParameters e = (DesignParameters) other;
		
		if ( getTwoColorArray() == null ? e.getTwoColorArray()!= null : !getTwoColorArray().equals( e.getTwoColorArray()))
			return false;
		if ( getGenotype() == null ? e.getGenotype()!= null : !getGenotype().equals( e.getGenotype()))
			return false;
		if ( getFactor1active() == null ? e.getFactor1active()!= null : !getFactor1active().equals( e.getFactor1active()))
			return false;
		if ( getFactor2active() == null ? e.getFactor2active()!= null : !getFactor2active().equals( e.getFactor2active()))
			return false;
		if ( getFactor3active() == null ? e.getFactor3active()!= null : !getFactor3active().equals( e.getFactor3active()))
			return false;
		if ( getFactor1Label() == null ? e.getFactor1Label()!= null : !getFactor1Label().equals( e.getFactor1Label()))
			return false;
		if ( getFactor2Label() == null ? e.getFactor2Label()!= null : !getFactor2Label().equals( e.getFactor2Label()))
			return false;
		if ( getFactor3Label() == null ? e.getFactor3Label()!= null : !getFactor3Label().equals( e.getFactor3Label()))
			return false;
		if ( getFactor1Level() == null ? e.getFactor1Level()!= null : !getFactor1Level().equals( e.getFactor1Level()))
			return false;
		if ( getFactor2Level() == null ? e.getFactor2Level()!= null : !getFactor2Level().equals( e.getFactor2Level()))
			return false;
		if ( getFactor3Level() == null ? e.getFactor3Level()!= null : !getFactor3Level().equals( e.getFactor3Level()))
			return false;
		if ( getNoRilsPerLevel() == null ? e.getNoRilsPerLevel()!= null : !getNoRilsPerLevel().equals( e.getNoRilsPerLevel()))
			return false;
		if ( getNoSlides() == null ? e.getNoSlides()!= null : !getNoSlides().equals( e.getNoSlides()))
			return false;
		if ( getNoIterations() == null ? e.getNoIterations() != null : !getNoIterations().equals( e.getNoIterations()))
			return false;
		
		return true;
	}
	
	/**
	 * Get the names of all public properties of DesignParameters.
	 */
	public Vector<String> getFields()
	{
		Vector<String> fields = new Vector<String>();
		fields.add("twocolorarray");
		fields.add("genotype");
		fields.add("factor1active");
		fields.add("factor2active");
		fields.add("factor3active");
		fields.add("factor1label");
		fields.add("factor2label");
		fields.add("factor3label");
		fields.add("factor1level");
		fields.add("factor2level");
		fields.add("factor3level");
		fields.add("norilsperlevel");
		fields.add("noslides");
		fields.add("noiterations");
		return fields;
	}	

	@Deprecated
	public String getFields(String sep)
	{
		return (""
		+ "twocolorarray" +sep
		+ "genotype" +sep
		+ "factor1active" +sep
		+ "factor2active" +sep
		+ "factor3active" +sep
		+ "factor1label" +sep
		+ "factor2label" +sep
		+ "factor3label" +sep
		+ "factor1level" +sep
		+ "factor2level" +sep
		+ "factor3level" +sep
		+ "norilsperlevel" +sep
		+ "noslides"+sep
		+ "noiterations"
		);
	}

	@Deprecated
	public String getValues(String sep)
	{
		StringWriter out = new StringWriter();
		{
			Object valueO = getTwoColorArray();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getGenotype();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor1active();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor2active();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor3active();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor1Label();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor2Label();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor3Label();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor1Level();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor2Level();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getFactor3Level();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getNoRilsPerLevel();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getNoSlides();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS);
		}
		{
			Object valueO = getNoIterations();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS);
		}
		return out.toString();
	}

	@Override
	public String getIdField() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<?> getWeight() {
		return _weight;
	}

	public void setWeight(List<?> _weights) {
		this._weight = _weights;
	}

	public List<?> getRangeStart()
	{
		return range_start;
	}

	public void setRangeStart(List<?> range_start)
	{
		this.range_start = range_start;
	}

	public List<?> getRangeEnd()
	{
		return range_end;
	}

	public void setRangeEnd(List<?> range_end)
	{
		this.range_end = range_end;
	}

	@Override
	public Entity create(Tuple tuple) throws ParseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<String> getFields(boolean skipAutoIds)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getIdValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getLabelFields()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getXrefIdFieldName(String fieldName)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

