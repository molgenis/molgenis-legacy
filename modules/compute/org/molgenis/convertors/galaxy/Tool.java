package org.molgenis.convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Missing: ui hints
 * 
 * 
 * @author Morris Swertz
 * @author Galaxy team
 */

@XmlRootElement(name = "tool")
@XmlAccessorType(XmlAccessType.FIELD)
// so use fields bypassing get/set
public class Tool
{
	@XmlAttribute
	String id;

	@XmlAttribute
	String name;

	@XmlElement
	String description;

	@XmlAttribute
	String version;

	@XmlElement
	String help;

	@XmlElement
	Command command;

	@XmlElementWrapper(name = "inputs")
	@XmlElementRefs(
	{ @XmlElementRef(name = "param", type = Param.class), @XmlElementRef(name = "display", type = Display.class),
			@XmlElementRef(name = "repeat", type = ParamRepeat.class),
			@XmlElementRef(name = "conditional", type = ParamConditional.class) })
	List<Input> inputs;

	@XmlElementWrapper(name = "outputs")
	@XmlElementRefs(
	{ @XmlElementRef(name = "data", type = Data.class) })
	List<Data> outputs;

	@XmlElementWrapper(name = "tests")
	@XmlElement(name = "test")
	List<Test> tests = new ArrayList<Test>();

	// @XmlElement
	// Options options;

	@XmlElement
	Code code;

	@XmlElement
	RequestParamTranslation request_param_translation;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		String command_string = "\t" + command.toString() + "\n";
		String code_string = code != null ? "\t" + code.toString() + "\n" : "";
		String params_string = "";
		for (Input i : inputs)
			params_string += "\t" + i.toString().replace("\n", "\n\t") + "\n";
		String data_string = "";
		for (Data d : outputs)
			data_string += ("\t" + d + "\n");
		String tests_string = "";
		for (Test t : tests)
			tests_string += "\t" + t.toString().replace("\n", "\n\t") + "\n";
		return String.format("Tool(id='%s', name='%s',version='%s')\n%s", id, name, version, command_string
				+ code_string + params_string + data_string + tests_string
				+ (help != "" ? "\n\thelp='" + help.replace("\n", "\n\t") + "'\n" : null)
				+ (description != null ? "\n\tdescription='" + description.replace("\n", "\n\t") + "'\n" : null) + ")");
	}
}
