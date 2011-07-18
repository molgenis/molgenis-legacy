<!--Date:        December 3, 2008
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generate.screen.PluginScreenFTLTemplateGen 3.0.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugin_news_News screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" value=""/>
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.name}">
		${screen.label}
		</div>
		<#--messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->
Updates of the database, both user features and insertion of new data, will be announced on this page. All news items are stored in the news archive. 
<hr/>
<table width="100%">
<tr>
<#if screen.action == "entry">

<#assign newsItem = screen.newsItem>
<td>
<div>
<h3>${newsItem.getTitle()}</h3>
<h4>${newsItem.getSubtitle()}</h4>
<p>${newsItem.getText()}</p>
<#if newsItem.getDate()??><p>${newsItem.getDate()}</p></#if>
<p>[<a href="molgenis.do?__target=${screen.name}">Back</a>]</p>
</div>
</td>

<#elseif screen.action == "all">

<td>
<div>
<#list screen.news as newsItem>
<h3>${newsItem.getTitle()}</h3>
<h4>${newsItem.getSubtitle()}</h4>
<p>${newsItem.getText()} [<a href="molgenis.do?__target=${screen.name}&__action=entry&id=${newsItem.getId()}">more</a>]</p>
<#if newsItem.getDate()??><p>${newsItem.getDate()}</p></#if>
<br/><br/>
</#list>
</div>
</td>

<#else>
<#-- same as "all" (copied) -->

<td>
<div>
<#list screen.news as newsItem>
<h3>${newsItem.getTitle()}</h3>
<h4>${newsItem.getSubtitle()}</h4>
<p>${newsItem.getText()} [<a href="molgenis.do?__target=${screen.name}&__action=entry&id=${newsItem.getId()}">more</a>]</p>
<#if newsItem.getDate()??><p>${newsItem.getDate()}</p></#if>
<br/><br/>
</#list>
</div>
</td>

</#if>
<#--
<td width="250">
<div class="newsbox">
<h4 align="center">News</h4>
<#list screen.news as newsItem>
<h5>${newsItem.getTitle()}</h5>
<p>${newsItem.getText_()} [<a href="molgenis.do?__target=${screen.name}&__action=entry&id=${newsItem.id}">more</a>]</p>
<p align="right">${newsItem.getDate_()}</p>
</#list>
<p align="center">[<a href="molgenis.do?__target=${screen.name}&__action=all">All news</a>]</p>
</div>
</td>
-->
</tr>
</table>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
