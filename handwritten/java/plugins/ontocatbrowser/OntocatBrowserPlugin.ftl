<#--this you need in every plugin. 
Make sure the macro name is changed to match your plugin.-->
<#macro OntocatBrowser screen>
<#assign model = screen.model>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">

	<#assign headerBgcolor = "#e5e5e5">
	<#assign borderStyle = "solid">
	<#assign mainBorderColor = "#800000">
	<#assign controlBorderColor = "#000080">
	<#assign borderWidthSpacing = "1px">
	<#assign picSize = "15">
	<#assign padding = "5px">


<table style="border-collapse: separate;">
	<tr>
		<td>
				<#if model.message?exists>
					<#if model.message.success>
						<p class="successmessage">${model.message.text}</p>
					<#else>
						<p class="errormessage">${model.message.text}</p>
					</#if>
				</#if>
		</td>
		<td colspan="2">
		</td>
	</tr>
	<tr>
		<td align="center" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: ${headerBgcolor};">
			<b>OntoCAT Browser</b>
		</td>
		<td align="center" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: ${headerBgcolor};">
			<b>Explore term</b>
		</td>
		<td align="center" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: ${headerBgcolor};">
			<b>Stored terms</b>
		</td>
	</tr>
	
	<tr>
		<#-- Dual use: both in 'OntoCAT Browser' as in 'Result'-->
		<#if model.getSelectedExploreTerm()?exists>
			<#assign selectedExploreTerm = model.getSelectedExploreTerm()>
		<#else>
			<#assign selectedExploreTerm = "EMPTY">
		</#if>
	
		<#-- BEGIN OntoCAT Browser -->
		<td style="padding: ${padding}; width: 30%; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: #ffffff;">
		<div style="width: 100%; height: 300px; overflow-y: auto;">

			<#if model.getBrowserTerms()?exists>
				<#if model.getSelectedBrowserTerm()?exists>
					<#if model.getBrowserTerms()?keys?seq_contains(model.getSelectedBrowserTerm())>
						<#assign selectedBrowserTerm = model.getSelectedBrowserTerm()>
					<#else>
						<#assign selectedBrowserTerm = model.getBrowserTerms()?keys[0]>
					</#if>
				<#else>
					<#assign selectedBrowserTerm = model.getBrowserTerms()?keys[0]>
				</#if>

				<input type="hidden" name="selectedBrowserTerm" value="${selectedBrowserTerm}">
				<input type="hidden" name="selectedExploreTerm" value="${selectedExploreTerm}">
				
				<#list model.getBrowserTerms()?keys as key>
					<#if model.getBrowserTermsState()?exists>
						<#if key_index == 0>
							<#if model.getBrowserTermsState()[key] == "MIN">
								<input type="image" src="res/img/close.png" onclick="document.forms.${screen.name}.selectedBrowserTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'BrowseBack';"/> ${model.getBrowserTerms()[key]}<br>
							<#elseif model.getBrowserTermsState()[key] == "PLUS">
								<input type="image" src="res/img/open.png" onclick="document.forms.${screen.name}.selectedBrowserTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'BrowseForth';"/> ${model.getBrowserTerms()[key]}<br>
							<#else>
								error!
							</#if>
						</#if>
						<#if model.getBrowserTermsState()[key] == "MIN">
							<#assign res = model.getBrowserTerms()[key]?matches("([--]+)(.+)")>
								<#list res as m>
									${m?groups[1]}<input type="image" src="res/img/close.png" onclick="document.forms.${screen.name}.selectedBrowserTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'BrowseBack';"/>&nbsp;<div style="display: inline;<#if key == selectedExploreTerm> background-color: blue; color: white;<#else></#if>" onclick="document.forms.${screen.name}.selectedExploreTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'ExploreFromOLS'; document.forms.${screen.name}.submit();">${m?groups[2]}</div><br>
								</#list>
						<#elseif model.getBrowserTermsState()[key] == "PLUS">
							<#assign res = model.getBrowserTerms()[key]?matches("([--]+)(.+)")>
								<#list res as m>
									${m?groups[1]}<input type="image" src="res/img/open.png" onclick="document.forms.${screen.name}.selectedBrowserTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'BrowseForth';"/>&nbsp;<div style="display: inline;<#if key == selectedExploreTerm> background-color: blue; color: white;<#else></#if>" onclick="document.forms.${screen.name}.selectedExploreTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'ExploreFromOLS'; document.forms.${screen.name}.submit();">${m?groups[2]}</div><br>
								</#list>
						<#else>
							<#assign res = model.getBrowserTerms()[key]?matches("([--]+)(.+)")>
								<#list res as m>
									${m?groups[1]}--&nbsp;<div style="display: inline;<#if key == selectedExploreTerm> background-color: blue; color: white;<#else></#if>" onclick="document.forms.${screen.name}.selectedExploreTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'ExploreFromOLS'; document.forms.${screen.name}.submit();">${m?groups[2]}</div><br>
								</#list>
						</#if>
					<#else>
							<input type="image" src="res/img/open.png" onclick="document.forms.${screen.name}.selectedBrowserTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'BrowseForth';"/> ${model.getBrowserTerms()[key]}<br>
					</#if>
				</#list>
			</#if>
		</div>
		</td>
		<#-- END OntoCAT Browser -->
				
		<#-- BEGIN Explore term -->
		<td style="padding: ${padding}; width: 30%; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: #ffffff;">
		<div style="width: 100%; height: 300px; overflow-y: auto;">
		<#if model.getExplored()?exists>
			<table>
				<tr>
					<td valign="top">
						<b>Accession<font color="#FF0000">*</font></b>
					</td>
					<td>
						<#-- dit zou de makeLookupHyperlink() kunnen doen.. maar dan word de HTML ook gebruikt voor database acties met explored.getAccession! argh -->
						<a href="http://www.ebi.ac.uk/ontology-lookup/?termId=${model.getExplored().getAccession()}" target="_blank">${model.getExplored().getAccession()}</a>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Term<font color="#FF0000">*</font></b>
					</td>
					<td>
						${model.getExplored().getTerm()}
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Name<font color="#FF0000">*</font></b>
					</td>
					<td>
						<#if model.getExploreMode() == "db">
							<input type="text" name="UserDefName" value="${model.getExplored().getName()}"> <input type="image" width="10" height="10" src="res/img/save.png" onclick="document.forms.${screen.name}.__action.value = 'ChangeName';"/>
						<#else>
							${model.getExplored().getName()}
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Metadata<font color="#0000FF">*</font></b>
					</td>
					<td>
						<#if (model.getExplored().getMetaData()?size > 0)>
							<#list model.getExplored().getMetaData() as n>
								${n}<br>
							</#list>
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>References<font color="#0000FF">*</font></b>
					</td>
					<td>
						<#if (model.getExplored().getXRefs()?size > 0)>
							<#list model.getExplored().getXRefs() as n>
								${n}<br>
							</#list>
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Parents<font color="#0000FF">*</font></b>
					</td>
					<td>
						<#if (model.getExplored().getParents()?size > 0)>
							<#list model.getExplored().getParents() as n>
								${n}<br>
							</#list>
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Relations<font color="#0000FF">*</font></b>
					</td>
					<td>
						<#if (model.getExplored().getRelations()?size > 0)>
							<#list model.getExplored().getRelations() as n>
								${n}<br>
							</#list>
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Path<font color="#FF0000">*</font></b>
					</td>
						<td>
						<#if model.getExplored().getPath()?exists>
							${model.getExplored().getPath()}
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Category<font color="#00FF00">*</font></b>
					</td>
					<td>
					<#if model.getExploreMode() == "db">
						<#if (model.getExplored().getCategory()?exists)>
							<#assign currentCategory = model.getExplored().getCategory()>
							<#assign addEmpty = "no">
						<#else>
							<#assign currentCategory = -1>
							<#assign addEmpty = "yes">
						</#if>
						<select DISABLED name="UserDefCategory">
						<#if addEmpty == "yes"><option value="-1" selected>Choose category:</option><#else></#if>
							<#list model.getCategories() as n>
								<option title="${n.getDescription()}" value="${n.getId()}" <#if currentCategory == n.getId()>selected<#else></#if>>${n.getName()}</option>
							</#list>
						</select>
						<input type="image" width="10" height="10" src="res/img/save.png" onclick="document.forms.${screen.name}.__action.value = 'ChangeCategory';"/>
					<#else>
						N/A
					</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Investigation<font color="#00FF00">*</font></b>
					</td>
					<td>
						<#if model.getExploreMode() == "db">
							${model.getExplored().getInvestigation().getName()}
						<#else>
							N/A
						</#if>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<b>Graph<font color="#FF0000">*</font></b>
					</td>
					<td>
						${model.getExplored().getGraphURI()}
					</td>
				</tr>
			</table>
		<#else>
			<table>
				<tr>
				<td>
				 
				</td>
				</tr>
			</table>
		</#if>
		</div>
		</td>
		<#-- END Explore term-->
		
		<#-- BEGIN Stored terms -->
		<td valign="top" style="padding: ${padding}; width: 30%; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: #ffffff;">
			<#if model.getSelectedStoredTerm()?exists>
				<#assign selectedStoredTerm = model.getSelectedStoredTerm()>
			<#else>
					<#assign selectedStoredTerm = -1>
			</#if>
			
			<input type="hidden" name="selectedStoredTerm" value="${selectedStoredTerm?c}">
			
			<#if model.getStoredTerms()?exists>
				<#if model.getStoredTerms()?size != 0>
					<#list model.getStoredTerms() as n>
					<#if n.getTermPath()?exists>
						<#assign termPath = n.getTermPath()>
					<#else>
						<#assign termPath = "No termpath">
					</#if>
					
						<div title="${termPath}" style="display: inline;<#if n.getId() == selectedStoredTerm> background-color: blue; color: white;<#else></#if>" onclick="document.forms.${screen.name}.selectedStoredTerm.value = '${n.getId()?c}'; document.forms.${screen.name}.__action.value = 'ExploreFromDB'; document.forms.${screen.name}.submit();"> ${n.getName()} (<i><#if termPath?length gt 25>${termPath?substring(0, 25)}...<#else>${termPath}</#if></i>)</div><br>
					</#list>
				<#else>
						Database is empty.
				</#if>
			<#else>
				An error has occurred.
			</#if>
		</td>
		<#-- END Stored terms -->
		
	</tr>
	
	<#-- CONTROLS -->
	<tr>
	
		<td valign="top" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${controlBorderColor}; background-color: ${headerBgcolor};">
			<#-- OntoCAT Browser-->
			<#if model.getPath()?exists>
				<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
				<i>Click '+' and '-' to navigate, and a term to explore.</i><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/recordview.png" onclick="document.forms.${screen.name}.__action.value = 'search';"/>
				<i>Search: </i><input type="text" name="searchThis" value=<#if model.getSearchThis()?exists>"${model.getSearchThis()}"<#else>""</#if>> <select name="searchSpace"><#if model.getSearchSpace()?exists><#if model.getSearchSpace() == "this"><option value="this" selected>in this ontology</option><option value="all">in all ontologies</option><#else><option value="this">in this ontology</option><option value="all" selected>in all ontologies</option></#if><#else><option value="this" selected>in this ontology</option><option value="all">in all ontologies</option></#if></select><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/last.png" onclick="document.forms.${screen.name}.__action.value = 'jump';"/>
				<i>Jump to accession: </i><input type="text" name="jumpToAccession" value=<#if model.getJumpToAccession()?exists>"${model.getJumpToAccession()}"<#else>""</#if>><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/reset.png" onclick="document.forms.${screen.name}.__action.value = 'Reset';"/>
				<i>Reset OntoCAT Browser.</i><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/listview.png" onclick="document.forms.${screen.name}.__action.value = 'ReturnToOntolOverview';"/>
				<i>Return to ontology overview.</i><br>
				<#if model.getSearchResult()?exists>
					<b>Result</b><br>
					<div style="width: 95%; height: 100px; overflow-y: auto; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: #ffffff;">
						<input type="hidden" name="selectedSearchResultTerm" value="">
						<#assign sResult = model.getSearchResult()?values[0]>
						<#if sResult == "empty">
							No results found.
						<#else>
							<#list model.getSearchResult()?keys as key>
								<div style="display: inline;<#if key == selectedExploreTerm> background-color: blue; color: white;<#else></#if>" onclick="document.forms.${screen.name}.selectedSearchResultTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'skipToSearchTerm'; document.forms.${screen.name}.submit();">${model.getSearchResult()[key]}</div><br>
							</#list>
						</#if>
					</div>
					<input type="image" width="${picSize}" height="${picSize}" src="res/img/cancel.png" onclick="document.forms.${screen.name}.__action.value = 'removeSearchResult';"/>
					<i>Remove result.</i><br>
				</#if>
			<#else>
				<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
				<i>Open a specific ontology by clicking '+'.</i><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/recordview.png" onclick="document.forms.${screen.name}.__action.value = 'search';"/>
				<i>Search: </i><input type="text" name="searchThis" value=<#if model.getSearchThis()?exists>"${model.getSearchThis()}"<#else>""</#if>> <select name="searchSpace"><option value="this" disabled="disabled">in this ontology</option><option value="all" selected>in all ontologies</option></select><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/last.png" onclick="document.forms.${screen.name}.__action.value = 'jump';"/>
				<i>Jump to accession: </i><input type="text" name="jumpToAccession" value=<#if model.getJumpToAccession()?exists>"${model.getJumpToAccession()}"<#else>""</#if>><br>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/reset.png" onclick="document.forms.${screen.name}.__action.value = 'Reset';"/>
				<i>Reset OntoCAT Browser.</i><br>
				<#if model.getSearchResult()?exists>
					<b>Result</b><br>
					<div style="width: 95%; height: 100px; overflow-y: auto; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${mainBorderColor}; background-color: #ffffff;">
						<input type="hidden" name="selectedSearchResultTerm" value="">
						<#assign sResult = model.getSearchResult()?values[0]>
						<#if sResult == "empty">
							No results found.
						<#else>
							<#list model.getSearchResult()?keys as key>
								<div style="display: inline;" onclick="document.forms.${screen.name}.selectedSearchResultTerm.value = '${key}'; document.forms.${screen.name}.__action.value = 'skipToSearchTerm'; document.forms.${screen.name}.submit();">${model.getSearchResult()[key]}</div><br>
							</#list>
						</#if>
					</div>
					<input type="image" width="${picSize}" height="${picSize}" src="res/img/cancel.png" onclick="document.forms.${screen.name}.__action.value = 'removeSearchResult';"/>
					<i>Remove result.</i><br>
				</#if>
			</#if>
			
			<#if !model.getBrowserTerms()?exists>
				<input type="image" width="${picSize}" height="${picSize}" src="res/img/reset.png" onclick="document.forms.${screen.name}.__action.value = 'Reset';"/>
				<b>An error has occurred: please reset.</b><br>
			</#if>
		</td>
		<td valign="top" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${controlBorderColor}; background-color: ${headerBgcolor};">
			<#-- Explore term-->
			<#if model.getExploreMode()?exists>
				<#if model.getExploreMode() == "ols">
					<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
					<i>Source: <b>ONLINE</b></i><br>
					<input type="image" width="${picSize}" height="${picSize}" src="res/img/save.png" onclick="document.forms.${screen.name}.__action.value = 'Add';"/>
					<i>Store this term in database.</i><br>
					<i>Under investigation:
					<select name="selectInvestigation">
								<#list model.investigationList as inv>
									<option <#if model.selectedInvestigation?exists && model.selectedInvestigation == inv.name>SELECTED</#if> value="${inv.name}">${inv.name}</option>
								</#list>
							</select>
					</i><br>
				<#else>
					<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
					<i>Source: <b>DATABASE</b>, 'name' and 'category' are modifiable.</i><br>
					<input type="image" width="${picSize}" height="${picSize}" src="res/img/delete.png" onclick="if (confirm('You are about to delete this ontologyterm from the database. Do you wish to proceed?')) { document.forms.${screen.name}.__action.value = 'Remove'; } else { return false; }"/>
					<i>Remove this term from database.</i><br>
				</#if>
			<#else>
				<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
				<i>No term selected.</i><br>
			</#if>
		</td>
		<td valign="top" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${controlBorderColor}; background-color: ${headerBgcolor};">
			<#-- Stored terms-->
			<img src="res/img/info.png" width="${picSize}" height="${picSize}"/>
			<i>Click term to explore.</i><br>
	
			<input type="image" width="${picSize}" height="${picSize}" src="res/img/update.gif" onclick="document.forms.${screen.name}.__action.value = 'RefreshDB';"/>
			<i>Refresh database terms.</i>
			
		</td>
	</tr>
	
	<#-- PATH -->
	<tr>
		<td colspan="3" style="padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: ${controlBorderColor}; background-color: ${headerBgcolor};">
		Current browser path: <font color="#0000FF">
			<#if model.getPath()?exists>
					${model.getPath()}
			<#else>
				Currently browsing list of available ontologies.
			</#if>
			</font>
		</td>
	</tr>
	
	<#-- LEGEND -->
	<tr>
		<td style="vertical-align: top; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: dotted; border-color: ${controlBorderColor};">
			<font color="#000000"><b>@</b></font> = Remote term currently present in the molgenis database<br>
		</td>
		
		<td style="vertical-align: top; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: dotted; border-color: ${controlBorderColor};">
			<font color="#FF0000">*</font> = Retrieved from database or online, depending on explore mode<br>
			<font color="#0000FF">*</font> = Retrieved from online source<br>
			<font color="#00FF00">*</font> = Retrieved from molgenis database<br>
		</td>
		
		<td style="vertical-align: bottom; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: solid; border-color: #008000;">
			<b>Ontology Browser Plugin</b><br>
			Based on Ontology Common API Tasks or <a href="http://ontocat.sourceforge.net/">OntoCAT</a>.<br>
			Queries the EBI ontology database, see: <a href="http://www.ebi.ac.uk/ontology-lookup/">http://www.ebi.ac.uk/ontology-lookup/</a>.
		</td>
	</tr>
	
</table>

<br><br><br>
	
</form>
</#macro>