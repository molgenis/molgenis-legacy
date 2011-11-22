<#-- drag and drop with help from http://www.html5rocks.com/tutorials/dnd/basics/#toc-creating-dnd-content 
and http://www.html5laboratory.com/drag-and-drop.php -->

<#-- render each of the yellow sticky notes -->
<#macro org_molgenis_scrum_plugins_Whiteboard_renderTask screen task>
<div class="ScrumWhiteboardTask" 
	draggable="true" 
	ondragstart="this.style.opacity = '0.4'; event.dataTransfer.setData('text/plain','${task.id}'); event.dataTransfer.effectAllowed='move';"
	ondragend="this.style.opacity = '1';">
<#if screen.taskEdit?exists && task == screen.taskEdit>
<textarea name="description" cols="15" rows="5">${task.description}</textarea>
<input type="text" name="storyPoints" value="${task.storyPoints}" size="4" />SP
<br/>
<img class="edit_button" style="float: right;" src="generated-res/img/delete.png" onclick="forms.${screen.name}.__action.value='taskDelete';forms.${screen.name}.__task.value='${task.id}';forms.${screen.name}.submit();"/>
<img class="edit_button" style="float: right;" src="generated-res/img/cancel.png" onclick="forms.${screen.name}.__action.value='taskCancel';forms.${screen.name}.__task.value='${task.id}';forms.${screen.name}.submit();"/>
<img class="edit_button" style="float: right;" src="generated-res/img/save.png" onclick="forms.${screen.name}.__action.value='taskSave';forms.${screen.name}.__task.value='${task.id}';forms.${screen.name}.submit();"/>

<#else>
<#-- old school buttons instead of drag and drop
<#if task.status == "checked">
<input type="submit" value="back"  onclick="__action.value='taskBack';__task.value='${task.id}';return true;">
<input type="submit" value="done" onclick="__action.value='taskDone';__task.value='${task.id}';return true;"/>
<#else>
<input type="submit" value="checkout" onclick="__action.value='taskCheckout';__task.value='${task.id}';return true;"/>
</#if>
-->
<img class="edit_button" style="float: right;" src="generated-res/img/editview.gif" onclick="forms.${screen.name}.__action.value='taskEdit';forms.${screen.name}.__task.value='${task.id}';forms.${screen.name}.submit();"/>
<br/>
${task.description} (${task.storyPoints} SP)
</#if>
</div>
</#macro>

<#macro org_molgenis_scrum_plugins_Whiteboard screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!-- the current task -->
	<input type="hidden" name="__task">
	<!-- the current story -->
	<input type="hidden" name="__story">
	
	
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
<#--begin your plugin-->	

<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<table class="ScrumWhiteboard">
<tr>
	<th>Stories <img align="top" class="edit_button" src="generated-res/img/new.png" onclick="forms.${screen.name}.__action.value='storyNew';forms.${screen.name}.submit();"/></th>
	<th>Scheduled<br/> (sp: ${screen.countSp("scheduled")})</th>
	<th>Checked out<br/> (sp: ${screen.countSp("checked")})</th>
	<th>Done!<br/> (sp: ${screen.countSp("done")})</th>
</tr>
<#list screen.stories as story>
<tr>
<td>
<#if screen.storyEdit?exists && story == screen.storyEdit>
<textarea name="name" cols="40" rows="2">${story.name}</textarea><br/>
<textarea name="howToDemo" cols="40" rows="10">${story.howToDemo}</textarea><br/>
<input type="text" name="importance" value="<#if story.importance?exists>${story.importance}</#if>"/>
<input type="text" name="linkToDemo" value="<#if story.linkToDemo?exists>${story.linkToDemo}</#if>"/><br/>
<img class="edit_button" style="float: right;" src="generated-res/img/delete.png" onclick="forms.${screen.name}.__action.value='storyDelete';forms.${screen.name}.__story.value='${story.id}';forms.${screen.name}.submit();"/>
<img class="edit_button" style="float: right;" src="generated-res/img/cancel.png" onclick="forms.${screen.name}.__action.value='storyCancel';forms.${screen.name}.__story.value='${story.id}';forms.${screen.name}.submit();"/>
<img class="edit_button" style="float: right;" src="generated-res/img/save.png" onclick="forms.${screen.name}.__action.value='storySave';forms.${screen.name}.__story.value='${story.id}';forms.${screen.name}.submit();"/>
<#else>
<img class="edit_button" style="float: right;" src="generated-res/img/editview.gif" onclick="forms.${screen.name}.__action.value='storyEdit';forms.${screen.name}.__story.value='${story.id}';forms.${screen.name}.submit();"/>
<br/>
<h2>${story.name}</h2>
<p>How to demo: ${story.howToDemo}</p>
<p>Importance: <#if story.linkToDemo?exists>${story.importance}<#else>NA</#if></p>
<p>Demo: <#if story.linkToDemo?exists><a href="${story.linkToDemo}">${story.linkToDemo}</a><#else>NA</#if></p>
<p>Total SP: ${screen.countSp(story)}</p>
</#if>
</td>
<td ondragover="event.preventDefault();" 
    ondrop="forms.${screen.name}.__action.value='taskBack';forms.${screen.name}.__task.value=event.dataTransfer.getData('Text');forms.${screen.name}.submit();">
<#list screen.tasks as task><#if task.story = story.id && task.status == "scheduled">
<@org_molgenis_scrum_plugins_Whiteboard_renderTask screen task/>
</#if></#list>
<img style="float:left; margin: 10px;" class="edit_button" src="generated-res/img/new.png" onclick="forms.${screen.name}.__action.value='taskNew';forms.${screen.name}.__story.value='${story.id}';forms.${screen.name}.submit();"/>
</td>
<td ondragover="event.preventDefault();" 
    ondrop="forms.${screen.name}.__action.value='taskCheckout';forms.${screen.name}.__task.value=event.dataTransfer.getData('Text');forms.${screen.name}.submit();">
<#list screen.tasks as task><#if task.story = story.id && task.status == "checked">
<@org_molgenis_scrum_plugins_Whiteboard_renderTask screen task/>
</#if></#list>
</td>
<td ondragover="event.preventDefault();" 
    ondrop="forms.${screen.name}.__action.value='taskDone';forms.${screen.name}.__task.value=event.dataTransfer.getData('Text');forms.${screen.name}.submit();">
<#list screen.tasks as task><#if task.story = story.id && task.status == "done">
<@org_molgenis_scrum_plugins_Whiteboard_renderTask screen task/>
</#if></#list>
</td>
</tr>
</#list>
</table>	
	
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
