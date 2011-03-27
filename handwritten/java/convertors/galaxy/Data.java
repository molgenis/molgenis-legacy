package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="data")
@XmlAccessorType(XmlAccessType.FIELD) 
public class Data
{
//	   <outputs>
//	     <data format="input" name="out_file1">
//	       <change_format>
//	         <when input="summary" value="-S" format="tabular" />
//	       </change_format>
//	     </data>
//	   </outputs>
	
	@XmlAttribute
	String format;
	
	@XmlAttribute
	String name;
	
	@XmlElement
	List<When> change_format = new ArrayList<When>();
	
	public String toString()
	{
		return String.format("Data(name='%s' format='%s')", name, format);
	}
}
