<#setting number_format="#"/>
<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/*
 * Created by: ${generator}
 * Date: ${date}
 */

package ${package}.servlet;
import java.util.ArrayList;
import java.util.Arrays;
import org.molgenis.MolgenisOptions;

public class UsedMolgenisOptions extends MolgenisOptions
{
	private static final long serialVersionUID = 8564678947243535356L;

	public UsedMolgenisOptions(){
	<#list options.optionsAsMap?keys as key>
		<#if options.optionsAsMap[key]?is_enumerable>
			this.${key} = new ArrayList<String>(Arrays.asList(new String[]{<#list options.optionsAsMap[key] as val>"${val}"<#if val_has_next> ,</#if></#list>}));
		<#elseif options.optionsAsMap[key]?is_boolean>
			this.${key} = <#if options.optionsAsMap[key] == true>true<#else>false</#if>;
		<#elseif options.optionsAsMap[key]?is_number>
			this.${key} = ${options.optionsAsMap[key]};
		<#elseif key == 'mapper_implementation'>
			this.${key} = MapperImplementation.valueOf("${options.optionsAsMap[key]}");
		<#elseif options.optionsAsMap[key]?is_string>
			this.${key} = "${options.optionsAsMap[key]}";
		<#else>
			this.${key} = ${options.optionsAsMap[key]}; //UNKNOWN TYPE, PLEASE EDIT UsedMolgenisOptionsGen.ftl !
		</#if>
	</#list>
	}
}


