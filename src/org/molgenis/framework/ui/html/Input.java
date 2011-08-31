package org.molgenis.framework.ui.html;

/**
 * Input is the base-class for all the input classes. It provides the
 * common interface as well as some convenience methods for processing the
 * inputs.
 * 
 * An Input has:
 * <ul>
 * <li>Name: unique name within a form
 * <li>Id: a unique id of this input. FIXME remove? Name is also unique?
 * <li>Value: the object value of the data for this input
 * <li>Label: a pretty label to show for this input on screen
 * <li>Readonly: indicating whether this input can be edited
 * <li>Hidden: indicating whether this input is shown.
 * <li>Required: whether input is required for this field.
 * <li>Tooltip: a short title describing the input.
 * <li>Description: a short title describing the input. FIXME: what is the
 * difference with tooltip?
 * <li>Style: css sentence for this input. FIXME: is this still used?
 * </ul>
 */
public interface Input<E>
{

	/**
	 * Retrieve the label of this input.
	 * 
	 * @return the label
	 */
	public abstract String getLabel();

	/**
	 * Retrieve allowed size of this input
	 */
	public abstract Integer getSize();
	
	/**
	 * Set the label for this input
	 * 
	 * @param label
	 *        a user understandeable label.
	 */
	public abstract void setLabel( String label );

	/**
	 * Retrieve the name of this input. The name is unique within a form.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Set the name of this input. The name is unique within a form.
	 * 
	 * @param name
	 */
	public abstract void setName( String name );

	/**
	 * Retrieve the value of this input as an Object.
	 * 
	 * @return Object value
	 */
	public abstract E getObject();
	
	/**
	 * Retrieve the toString version of getObject(). In case getObject() == null then the result =="
	 */
	public String getObjectString();
	

	/**
	 * Retrieve the value of this input as a String.
	 * Note: this name is confusing because setValue() works with an Object.
	 * 
	 * @return String value
	 */
	public abstract String getValue();

	/**
	 * Set the value of this input as an Object.
	 * 
	 * @param Object value
	 */
	public abstract void setValue( E value );

	/**
	 * Retrieve wether this input is readonly.
	 * 
	 * @return isReadonly.
	 */
	public abstract boolean isReadonly();

	/**
	 * Set wether this input is readonly.
	 * 
	 * @param readonly
	 *        true if readonly, else false. Default: false.
	 */
	public abstract void setReadonly( boolean readonly );

	/**
	 * Retrieve wether this input is hidden.
	 * 
	 * @return isHidden.
	 */
	public abstract boolean isHidden();

	/**
	 * Set wether this input is to be hidden.
	 * 
	 * @param hidden
	 *        true if hidden, else false. Default: false.
	 */
	public abstract void setHidden( boolean hidden );

	/**
	 * Retrieve the unique id of this input.
	 * 
	 * @return id
	 */
	public abstract String getId();

	/**
	 * Set the unique id of this input.
	 * 
	 * @param id
	 */
	public abstract void setId( String id );

	/** FIXME: what is the use? */
	public abstract String getStyle();

	/** FIXME: what is the use? */
	public abstract void setStyle( String style );

	/**
	 * Retrieve a tooltip describing this input
	 * 
	 * @return a tooltip
	 */
	public abstract String getTooltip();

	/**
	 * Set a tooltip describing this input.
	 * 
	 * @param tooltip
	 */
	public abstract void setTooltip( String tooltip );

	/** FIXME why is this here? */
	public abstract String getTarget();

	/** FIXME why is this here? */
	public abstract void setTarget( String target );

	/** FIXME is this not doubleup with tooltip? */
	public abstract String getDescription();

	/** FIXME is this not doubleup with tooltip? */
	public abstract void setDescription( String description );

	/**
	 * Retrieve wether input in this input is required
	 * 
	 * @return true if required, else false. Default: true.
	 */
	public abstract boolean isNillable();

	/**
	 * Set wether input in this input is required
	 * 
	 * @param required
	 *        true if required, else false. Default: false.
	 */
	public abstract void setNillable( boolean required );

}