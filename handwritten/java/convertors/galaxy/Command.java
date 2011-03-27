package convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD) //so use fields bypassing get/set
public class Command
{
	@XmlAttribute
	String interpreter;
	
	@XmlValue
	String value;

	public String getInterpreter()
	{
		return interpreter;
	}

	public void setInterpreter(String interpreter)
	{
		this.interpreter = interpreter;
	}
	
	public String toString()
	{
		return String.format("Command(interpreter='%s', value='%s')",interpreter,value);
	}
	
}
