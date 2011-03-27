<#macro org_molgenis_news_ui_News screen>
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
<br/>
<#if screen.action == "entry">

<#assign newsItem = screen.newsItem>
<p class="news_title">${newsItem.getTitle()}</p>
<p class="news_subtitle">${newsItem.getSubtitle()}</p>
<p>${newsItem.getText()}</p>
<p>${newsItem.getDate()}</p>
<p><a href="molgenis.do?__target=NewsPlugin&__action=all">All News</a></p>

<#else>

<div>
<#list screen.news as newsItem>
<div class="news_title">${newsItem.getTitle()}</div>
<div class="news_subtitle">${newsItem.getSubtitle()}</div>
<div>${newsItem.getDate()}</div>
<div><a href="molgenis.do?__target=${screen.name}&__action=entry&id=${newsItem.getId()}">More</a></div>
<br/><br/>
</#list>
</div>

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
			</div>
		</div>
	</div>
</#macro>
