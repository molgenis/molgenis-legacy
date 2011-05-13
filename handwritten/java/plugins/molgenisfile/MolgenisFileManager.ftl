<#macro plugins_molgenisfile_MolgenisFileManager screen>
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<#if model.db_path?exists>
	<#assign dbUrl = model.db_path>
<#else>
	ERROR: Could not retrieve db path to localize the applet!
</#if>

<#if model.hasFile == true>
	<#if model.molgenisFile.extension == 'fig'>
		<center>
		<applet 
		  code=jfig.gui.JFigViewerApplet
		  codebase=clusterdemo/
		  archive=jfig-bean.jar
		  width=600
		  height=500
		>
		
		<!--param name="xxxURL" value="http://tams-www.informatik.uni-hamburg.de/applets/jfig/gallery/taurus-antrieb.fig"--> 
		<param name="URL" value="${dbUrl}/downloadfile?name=${model.molgenisFile.name}"> 
		<param name="debug" value="true"> 
		<param name="zoomfit" value="true"> 
		<param name="antialias" value="true"> 
		<param name="renderquality" value="false"> 
		<param name="defaultPopupMenu" value="true"> 
		<param name="defaultKeyHandler" value="true"> 
		<param name="defaultDragHandler" value="true"> 
		<param name="positionAndZoomPanel" value="true"> 
		<param name="showRulers" value="false"> 
		<param name="zoomfit" value="true"> 
		<param name="units" value="inches"> 
		
		If you see this text, your browser does not support Java-applets.
		</applet>
		
		<br />If no picture appears, use IP adress, for example: ${model.getIpURl()}<br />
		If that does not help, your port 8080 is probably blocked and the applet cannot serve its content.
		
	<#elseif model.molgenisFile.extension == 'xxx'>
		<#-- make more exceptions to view certain file extensions here-->
	<#else>
		<iframe width="750px" height="600px" src="downloadfile?name=${model.molgenisFile.name}">
	</#if>
<#else>
	No file found. Please upload it here.<br>
	<input type="file" name="upload"/>
	<input type="submit" value="Upload" onclick="__action.value='upload';return true;"/><br><br>
	Alternatively, use this textarea to upload text data.<br>
	<textarea name="inputTextArea" rows="15" cols="50"><#if model.uploadTextAreaContent?exists>${model.uploadTextAreaContent}</#if></textarea>
	<input type="submit" value="Upload" onclick="__action.value='uploadTextArea';return true;"/><br>
	

</#if>

</center>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
