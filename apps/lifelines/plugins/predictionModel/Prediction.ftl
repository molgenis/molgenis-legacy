<#macro plugins_predictionModel_Prediction screen>

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
		
		<div class="screenbody">
		
		<script type="text/javascript">
			
			var count = 0;
			var dataTypeOptions = new Array();
			var fieldName = "";
			var fieldNameOptions = new Array();;
			
			function changeFieldContent(id)
			{
				
				var classType = document.getElementsByName(id);
				
				if(classType[1].value.toString() == "Measurement:dataType")
				{
					makeTable(id);
				}else{
					destroyTable(id);
				}
				
				if(classType[0].value.toString() == "ObservedValue")
				{
					
					var select = document.getElementsByName(id);
					
					select = select[1];
					
					var observedValue = document.createElement('option');
					
					observedValue.innerHTML = "ObservedValue";
					
					var length = select.length;
					
					for(var i = 0; i < length; i++){
						select.options[0] = null
					}
					
					select.add(observedValue, 0);
					
				}else{
					
					var select = document.getElementsByName(id);
					
					select = select[1];
					
					var option = document.createElement('option');
					
					for(var i = 0; i < fieldNameOptions.length; i++)
					{
						var option = document.createElement('option');
						option.innerHTML = fieldNameOptions[i];
						select.add(fieldNameOptions[i], i);
					}
				}
				
			}
			
			function destroyTable(id) {

				document.getElementById(id).innerHTML = "data type";
			}
			
			function greeting(){

				var table = document.getElementById(fieldName);
				makeTable(fieldName);
				
			}
			
			function makeTable(id) {
				
				var tab;
				
				tab = document.createElement('table');
				tab.setAttribute('id',id);
				tab.setAttribute('border','1');
				tableExisting = false;
					
				var row = new Array();
				var cell = new Array();
				
				row[0]=document.createElement('tr');
				
				cell[0]=document.createElement('td');
				
				cell[1]=document.createElement('td');
				
				cell[2]=document.createElement('td');
				
				var selection = document.createElement('select');
				
				selection.setAttribute("name", id + "_options_" + count);
				
				for(var index = 0; index < dataTypeOptions.length; index++)
				{
					var option = document.createElement('option');
					
					option.innerHTML = dataTypeOptions[index];
				
					selection.appendChild(option);
				}
				
				var textInput=document.createElement('input');
				
				textInput.setAttribute('type','text');
				
				textInput.setAttribute("name", id + "_input_" + count);
				
				textInput.setAttribute('size','15');
				
				var addButton = document.createElement('button');
				
				addButton.setAttribute('name','add');
				
				addButton.setAttribute('type','button');
				
				addButton.innerHTML = "add";
				
				fieldName = id;
				
				addButton.setAttribute("onclick", "greeting();");
				
				cell[0].appendChild(selection);
				
				cell[1].appendChild(textInput);
				
				cell[2].appendChild(addButton);
				
				row[0].appendChild(cell[0]);
				
				row[0].appendChild(cell[1]);
				
				row[0].appendChild(cell[2]);
				
				tab.appendChild(row[0]);
				
				var oldTable = document.getElementById('table');
				
				var cellActivated = document.getElementsByName(id);
				
				var cell = cellActivated[3];
				
				document.getElementById(id).appendChild(tab);
				
				count++;
			}
			
			function createSelection(option)
			{
				
				if(contains(option, dataTypeOptions) == false){
					
					dataTypeOptions.push(option);
				}
			}
			
			function fieldName(option)
			{
				
				if(contains(option, fieldNameOptions) == false){
					
					fieldNameOptions.push(option);
				}
			}
			
			function contains(option, array){
				
				for(var i = 0; i < array.length; i++)
				{
					if(array[i] == option)
					{
						
						return true;
					}		
				}
				return false;	
			}
			
		</script>
			<div class="screenpadding" id = "screenpadding">	
			    <h3 id="test"> Import dataShaper data to pheno model  </h3>
		        <input type="submit" value="Import" onclick="__action.value='UploadFile';return true;"/>
		        <input type="submit" value="Next Step" onclick="__action.value='ImportLifelineToPheno';return true;"/><br /><br />
				<!-- <input type="submit" value="Empty Database" onclick="__action.value='fillinDatabase';return true;"/>-->
 				
				<#list screen.getDataTypeOptions() as dataTypeOptions>
					<script type="text/javascript">
						createSelection("${dataTypeOptions}");
					</script>
				</#list>
				
				<#list screen.getChooseFieldName() as dataTypeOptions>
					<script type="text/javascript">
						fieldName("${dataTypeOptions}");
					</script>
				</#list>
				
				<#if screen.isImportingFinished() == false>
				Please enter your investigation: <input type="text" name="investigation" size="15" value=""><br/><br/>
				<table id="table" border="1">
					<tr>
						<#list screen.getSpreadSheetHeanders() as header>
							<th>${header}</th>
						</#list>
					</tr>
					<tr><div id='1'>
						<#list screen.getSpreadSheetHeanders() as header>
							<td><select id='1' name='${header}' onchange="changeFieldContent('${header}');">
								<#list screen.getChooseClassType() as options>
								  <option id="">${options}</option>
								</#list>
								</select>
							</td>
						</#list>
					</div></tr>
					<tr><div id='1'>
						<#list screen.getSpreadSheetHeanders() as header>
							<td><select id='2' name='${header}' onchange="changeFieldContent('${header}');">
								<#list screen.getChooseFieldName() as options>
								  <option id="">${options}</option>
								</#list>
								</select>
							</td>
						</#list>
					</div></tr>
					<tr>
						<#list screen.getSpreadSheetHeanders() as header>
							<td><select id='3' name='${header}'>
								<#list screen.getColumnIndex() as options>
								  <option id="">${options}</option>
								</#list>
								</select>
							</td>
						</#list>
					</tr>
					
					<tr>
						<#list screen.getSpreadSheetHeanders() as header>
							<td><div id='${header}'>data type</div>
							</td>
						</#list>
					</tr>
				</table>
				<#else>
					<label> <#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	
				</#if> 
				<div id="mytable">
				</div>				
			</div>
		</div>
	</div>
	
	
	
</form>
</#macro>

