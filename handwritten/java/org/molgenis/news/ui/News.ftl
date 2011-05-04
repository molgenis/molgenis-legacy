<#macro org_molgenis_news_ui_News screen>
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

		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		<div class="screenbody">
			<div class="screenpadding">	
</#if>



<#--begin your plugin-->
${screen.title}
<#if screen.action == "entry">

<#assign newsItem = screen.newsItem>
<p class="news_title">${newsItem.getTitle()}</p>
<p class="news_subtitle">${newsItem.getSubtitle()}</p>
<p>${newsItem.getText()}</p>
<p>${newsItem.getDate()?date}</p>
<p><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=all">All News</a></p>

<#else>

<div>
<#list screen.news as newsItem>
<div class="news_title">${newsItem.getTitle()}</div>
<div class="news_subtitle">${newsItem.getSubtitle()}</div>
<div>${newsItem.getDate()?date}</div>
<div><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=entry&id=${newsItem.getId()}" target="_parent">More</a></div>
<br/><br/>
</#list>
<#if screen.action == "top">
<p align="center"><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=all" target="_parent">All News</a></p>
</#if>
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
<#if show == "popup">
		<@molgenis_footer />
</#if>
</#macro>
