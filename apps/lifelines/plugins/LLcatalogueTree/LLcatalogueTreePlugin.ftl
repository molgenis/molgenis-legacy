<#macro plugins_LLcatalogueTree_LLcatalogueTreePlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_LLcatalogueTree_LLcatalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="measurementId" id="measureId" value="">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<script type="text/javascript">
			HashMap = function(){
			  this._dict = [];
			}
			HashMap.prototype._get = function(key){
			  for(var i=0, couplet; couplet = this._dict[i]; i++){
			    if(couplet[0] === key){
			      return couplet;
			    }
			  }
			}
			HashMap.prototype.put = function(key, value){
			  var couplet = this._get(key);
			  if(couplet){
			    couplet[1] = value;
			  }else{
			    this._dict.push([key, value]);
			  }
			  return this; // for chaining
			}
			HashMap.prototype.get = function(key){
			  var couplet = this._get(key);
			  if(couplet){
			    return couplet[1];
			  }
			}
		</script>
		
		<script>
			
			
			function createHashMap(key, content)
			{
				map.put(key, content);
				
			}
			
			function getHashMapContent(key){
				
				var value = map.get(key);
				$('#details').empty();
				$('#details').append(value);
				
			}
			
		</script>
		
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
			${screen.getName()}
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
				<h4> Choose an investigation</h4> 
				<select name="investigation" id="investigation"> 
					<#list screen.arrayInvestigations as inv>
						<#assign invName = inv.name>
						<option value="${invName}" <#if screen.selectedInvestigation??><#if screen.selectedInvestigation == invName>selected="selected"</#if></#if> >${invName}</option>			
					</#list>
				</select>
				<input type="submit" name="chooseInvestigation" value="refresh tree" onclick="__action.value='chooseInvestigation';"/>
				
			   <#if screen.isSelectedInv() == true>
					
					<div id="leftSideTree">
						${screen.getTreeView()}
					</div><br/>
					
					<div id="details"></div>
					
					<div id="ShoopingCartButton">
						<input type="submit" name="DownloadMeasurementsSubmit" value="Download" onclick="__action.value='DownloadMeasurements';"/>
					</div>
					
				</#if>
					
			</div>
		</div>
	</div>

</form>

</#macro>
