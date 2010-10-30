<#macro MenuView screen>
<script type="text/javascript"> 
	var ${screen.name}Tabs = new YAHOO.widget.TabView("${screen.name}"); 
</script>  
	 
<!-- layouting Menu '${screen.name}'-->
<#--left menu is a two column table with left navigation, right the information-->
<#--nested menus are automatically merged-->
<#--difficulty is nested menus, then the information should show the selection of subform-->
<div id="${screen.name}" class="yui-navset">
	<ul class="yui-nav">
		<form name="${screen.getName()}" method="get">
			<input type="hidden" name="__target" value="">
			<input type="hidden" name="select" value="">
 <#list screen.getVisibleChildren() as item>
 	<#assign selectedItem = screen.getSelected()/>
	<#assign __target = screen.getName() />
	<#assign select = item.getName() />		
			<li <#if selectedItem == item> class="selected"</#if>>
				<a onClick="document.forms.${screen.name}.__target.value='${__target}';document.forms.${screen.name}.select.value='${select}';document.forms.${screen.name}.submit();"><em>${item.getLabel()}</em></a>
			</li>
</#list>	
		</form>				
	</ul>
	<div class="yui-content">	
 <#list screen.getVisibleChildren() as item>
 	<#assign selectedItem = screen.getSelected()/>
		<div><p>
			<!--subform-->
			<!-- end subform -->
		</p></div>
</#list>	
		<@layout screen.getSelected()/>	
	</div>		
</div>		
</#macro>