<#macro screens_ShowResultsScreen screen>

<#assign parameters=screen.getDesignParameters()/>
<center><table>
<#if parameters.twocolorarray>
<h2>Results for <i><b>two channel</b></i> platform</h2>
<hr>
	<tr><td>
	<fieldset><legend>Individuals per Slide</legend>
	<#if screen.indXSlideLink?exists>
	<a href="tmpfile/${screen.indXSlideLink}" style="font-size:70%;"><b>Download</b></a>
	<div align="center">
	&nbsp;
	<@renderTable screen.indPerSlide/>
	</div>	
	<#else>
	  No 'Individuals per Slide' file
	</#if>
		
	</fieldset>	
	</td></tr>
	
	<tr><td>	
	<fieldset><legend>Individuals per Condition</legend>
	
	<#if screen.indXCondLink?exists>
	<a href="tmpfile/${screen.indXCondLink}" style="font-size:70%;"><b>Download</b></a>
	<div align="center">
	&nbsp;
	<@renderTable screen.indPerCondition/>
	</div>	
	<#else>
		No 'Individuals per Condition' file
	</#if>
	

	</fieldset>
	</td></tr>
	
	<tr><td>	
	<fieldset><legend>R output</legend>
	<#if screen.outputR?exists>
	${screen.outputR}
	<#else>
	No output R file
	</#if>
	</fieldset>
	</td></tr>

<#else>
<h2>Results for <b><i>single channel</b></i> platform</h2>
<hr>
	<tr><td>		
	<fieldset><legend>Individuals per Condition</legend>
	<#if screen.indXCondLink?exists>
	<a href="tmpfile/${screen.indXCondLink}" style="font-size:70%;"><b>Download</b></a>
	<div align="center">
	&nbsp;
	<@renderTable screen.indPerCondition/>
	</div>	
	<#else>
	  No 'Individuals per Condition' file
	</#if>
	</fieldset>
	</td></tr>		
		
	<tr><td>
	<fieldset><legend>R output</legend>
	<#if screen.outputR?exists>
	${screen.outputR}
	<#else>
	No output R file
	</#if>
	</fieldset>
	</td></tr>
</#if>
</table></center>
</#macro>

<#macro renderTable listOfTuples>
<table border="1">
<#list listOfTuples as row>
<#--header-->
<#if row_index = 0>
<tr><#list row.fields as f>
<th>&nbsp;${f}&nbsp;</th>
</#list>
</tr>
</#if>
<#--body-->
<tr><#list row.fields as f>
<td>${row.getString(f)}</td>
</#list>
</tr>
</#list>
</table>

</#macro>