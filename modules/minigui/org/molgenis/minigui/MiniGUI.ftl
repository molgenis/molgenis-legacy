<#macro org_molgenis_minigui_MiniGUI screen>
<#assign model = screen.VO>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<input type="hidden" name="__selectScreen">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">

<table width="100%">
	<tr>
		<td align="center">
			<table>
				<tr>
					<td align="center">
						<img src="clusterdemo/logos/molgenis_logo.png">
					</td>
				</tr>
				<tr>
					<td align="center">
						<table>
							<tr>
								<td>
									<input type="text" name="query" class="searchBox" value="">
								</td>
								<td>
									<div class="buttons"><button type="submit" id="search" onclick="document.forms.${screen.name}.__action.value = 'search'; document.forms.${screen.name}.submit();"><img src="generated-res/img/recordview.png" alt=""/>Search</button></div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="center">
						
						<ul class="sf-menu">
							<li class="current">
								<a href="#a">Database</a>
								<ul>
									<#list model.uiTree as parent>
										<li><a href="#">${parent.name}</a>
										<@toList parent/>
										</li>
									</#list>
								</ul>
							</li>
							<li>
								<a href="#">Tools</a>
								<ul>
									<li>
										<a href="api/R/">R API</a>
									</li>
									<li>
										<a href="api/find/">Find API</a>
									</li>
									<li>
										<a href="api/add/">Add API</a>
									</li>
									<li>
										<a href="generated-doc/fileformat.html">File format</a>
									</li>
								</ul>
							</li>
							<li>
								<a href="http://www.molgenis.org/wiki/xQTL">Community</a>
								<ul>
									<li>
										<a href="http://www.xgap.org/wiki/xQTL">Help</a>
									</li>
									<li>
										<a href="http://www.molgenis.org">MOLGENIS.org</a>
									</li>
									<li>
										<a href="http://xqtl.nl/">xQTL.nl</a>
									</li>
									<li>
										<a href="https://github.com/molgenis/">GitHub</a>
									</li>
								</ul>
							</li>
						</ul>
						
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
	
			</div>
		</div>
	</div>
</form>
</#macro>


<#macro toList parent>
	<#if parent.children?size gt 0>
		<ul>
		<#list parent.children as child>
			<li><a href="?select=${child.name}">${child.label}</a>
			<@toList child/>
			</li>
		</#list>
		</ul>
	</#if>
</#macro>