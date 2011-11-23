<#macro plugins_LLcatalogueSplitter_LLcatalogueSplitterPlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
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
						${screen.getSplitter()}
				<div id="splitterContainer">
					 	<div id="leftPane">
					 	 	${screen.getSplitterContents().getLeftPane()}
					 	 	coming from ftl: left pane
					 	</div>
					 	<div id="rightPane">
						 	<div style="height:5%;background:#bac8dc">Toolbar?</div>
						 		<div id="rightSplitterContainer" style="height:95%">
					     			<div id="rightTopPane">
						 				 ${screen.getSplitterContents().getRightTopPane()}
						 				 coming from ftl: right pane	 				 
						 			</div>
						 			<div id="rightBottomPane">
						 				<div>
						 				 ${screen.getSplitterContents().getRightBottomPane()}
						 				  	coming from ftl: rightbottom pane
						 				 </div>
						 			</div>
						 		</div> <!--rightSplitterContainer-->
						</div> <!--<div id="rightPane">-->
				</div> <!--splitterContainer-->
	</div> <!--	<div class="formscreen"-->
	
	
</form>

</#macro>
