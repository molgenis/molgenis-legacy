<#macro org_molgenis_minigui_MiniGUI screen>
<#assign model = screen.VO>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<input type="hidden" name="__selectName">
	<input type="hidden" name="__selectFieldEntity">
	<input type="hidden" name="__selectFieldIndex">
	
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
		<td width="33%">
			<!--left flank-->
		</td>
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
									<li>
										<a href="#aa">menu item that is quite long</a>
									</li>
									<li class="current">
										<a href="#ab">menu item</a>
										<ul>
											<li class="current"><a href="#">menu item</a></li>
											<li><a href="#aba">menu item</a></li>
											<li><a href="#abb">menu item</a></li>
											<li><a href="#abc">menu item</a></li>
											<li><a href="#abd">menu item</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
								</ul>
							</li>
							<li>
								<a href="#">Help</a>
							</li>
							<li>
								<a href="#">Tools</a>
								<ul>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">short</a></li>
											<li><a href="#">short</a></li>
											<li><a href="#">short</a></li>
											<li><a href="#">short</a></li>
											<li><a href="#">short</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
									<li>
										<a href="#">menu item</a>
										<ul>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
											<li><a href="#">menu item</a></li>
										</ul>
									</li>
								</ul>
							</li>
							<li>
								<a href="#">APIs</a>
							</li>
							<li>
								<a href="#">Admin</a>
							</li>	
						</ul>
						
					</td>
				</tr>
			</table>
			
		</td>
		<td width="33%">
			<!--right flank-->
		</td>
	</tr>
</table>



	
			</div>
		</div>
	</div>
</form>
</#macro>