
<#if show == "popup">
		<@molgenis_header />
<div class="formscreen">
<div class="form_header" id="SearchPlugin">News</div>
<div class="screenpadding">
<#else>
	<div class="formscreen">
		<div class="form_header" id="${screen.name}">
		${screen.label}
		</div>
<#--
Commented out since unwanted messages appear when used
as subplugin and "action" does not exist in this one.
Default is to show a predefined number of newest news.
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
-->
		<div class="screenbody">
			<div class="screenpadding">	
</#if>

<#if model.action == "entry">

<#assign newsItem = model.newsItem>
<p class="news_title">${newsItem.getTitle()}</p>
<p class="news_subtitle">${newsItem.getSubtitle()}</p>
<p>${newsItem.getText()}</p>
<p>${newsItem.getDate()?date}</p>
<p><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=all">All News</a></p>

<#elseif model.action == "all">

Updates of the database, both user features and insertion of new data, will be announced on this page. All news items are stored in the news archive.
<hr/>
<br/>
<div>
<#list model.allNews as newsItem>
<div class="news_title">${newsItem.getTitle()}</div>
<div class="news_subtitle">${newsItem.getSubtitle()}</div>
<div>${newsItem.getDate()?date}</div>
<div><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=entry&id=${newsItem.getId()}" target="_parent">More</a></div>
<br/><br/>
</#list>
</div>

<#else>

<div>
<#list model.topNews as newsItem>
<div class="news_title">${newsItem.getTitle()}</div>
<div class="news_subtitle">${newsItem.getSubtitle()}</div>
<div>${newsItem.getDate()?date}</div>
<div><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=entry&id=${newsItem.getId()}" target="_parent">More</a></div>
<br/><br/>
</#list>
<p align="center"><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=all" target="_parent">All News</a></p>
</div>



</#if>

			</div>
		</div>
	</div>
<#if show == "popup">
		<@molgenis_footer />
</#if>

