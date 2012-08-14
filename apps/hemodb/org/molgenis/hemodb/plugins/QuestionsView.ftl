<#-- Return button in the Questions plugin -->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<input type="hidden" name="__target" value="${model.name}">
	<input type="hidden" name="__action">
	<input type='submit' id='back' value='Return' onclick="__action.value='back'" />
</form>

<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<input type="hidden" name="__target" value="${model.name}">
	<input type="hidden" name="__action">
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
			${model.label}
		</div>
		
		<#list model.getMessages() as message>
			<#if message.success>	
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
	
		<div class="screenbody">
			<div class="screenpadding">	
	
			<#if model.state == "BEGINNING">
			<#-- This is the start page of the Questions plugin.-->
				<div id="askingQuestions">
					Which question do you want to ask?<br />
					<input type="radio" id="Q1" name="questions" value="questionOne" /> Search for expression of gene(s) in subsets of samples<br />
					<input type="radio" id="Q2" name="questions" value="questionTwo"  /> Make comparisons: what are significantly differentially expressed genes between group A and group B <br />
					<input type="radio" id="Q3" name="questions" value="questionThree" /> Convert between probes and genes <br />
				</div>
				
				<div id="submit">
					<input type='submit' id='submitInfo' value='Submit' onclick="__action.value='submitInformation'" />
				</div>
			
			<#elseif model.state == "QUESTION1">
			<br/>Search for expression of gene(s) in subsets of samples.<br/><br/>
				<div id="geneExpression">
					Choose the type of gene expression:<br />
					<input type="radio" id="geneExpRaw" name="geneExp" value="expDataRaw" /> Raw expression<br />
					<input type="radio" id="geneExpLog" name="geneExp" value="expDataLog2Quan" /> Quantile normalized & log2 transformed expression<br />
				</div>
	
				<div id="geneList">
					<br />Supply the gene(s) you want to select (comma seperated):<br />
					<p style="font-size=8px">Please be aware that if you supply a large list of genes it will take a while to calculate</p>
					<textarea rows="10" cols="51" name="geneText"value="genes"></textarea>
				</div>
		
				<div id="groupSelection">
					<br/>Make a selection of the groups you are interested in. Hold down the Ctrl (windows) / Command (Mac) button to select multiple groups.<br />
					<select multiple="multiple" name="sampleGroups" id="sGroups" style="margin-right:10px">
											<option value="none" selected="selected">no selection made</option>
					<#list model.names as samplenames>
						<option value="${samplenames}">${samplenames}</option>
					</#list>
					</select>
				</div>
		
				<div id="submit">
					<input type='submit' id='submitInfo' value='Submit' onclick="__action.value='submitInfoQ1'" />
				</div>
			</div>
		</div>
	</div>
</form>

<#elseif model.state == "QUESTION1_RESULT">
</form>
			<#--matrix sub-->
			<#assign screen = model.getController().get("QuestionsSub")/>
			<#include screen.getViewTemplate()>
      		<#assign templateSource = "<@"+screen.getViewName() + " screen/>">
      		<#assign inlineTemplate = templateSource?interpret>
      		<@inlineTemplate screen />  
      		
      		
<#elseif model.state == "QUESTION2">
	<br/>Make comparisons: what are significantly differentially expressed genes between group A and group B. <br/><br/>
	<div id="geneExpression">
		Choose the type of gene expression:<br />
		<input type="radio" id="geneExpRaw" name="geneExp" value="expDataRaw" /> Raw expression<br />
		<input type="radio" id="geneExpLog" name="geneExp" value="expDataLog2Quan"  /> Quantile normalized & log2 transformed expression<br />
	</div>

	<div id="sampleCombiningMethod">
		<br/> Choose the method which you want to use to combine the samples within each group:<br/>
		<input type="radio" id="sampleCombineMean" name="sampleCombine" value="sampleCombineMean" /> Mean<br />
		<input type="radio" id="sampleCombineMedian" name="sampleCombine" value="sampleCombineMed" /> Median<br />
	</div>

	<div id="groupSelection">
		<br/>Make a selection of the groups you are interested in. Hold down the Ctrl (windows) / Command (Mac) button to select multiple groups.<br />
		<select multiple="multiple" name="sampleGroups" id="sGroups" style="margin-right:10px">
			<option value="none" selected="selected">no selection made</option>
			<#list model.names as samplenames>
				<option value="${samplenames}">${samplenames}</option>
			</#list>
		</select>
	</div>
	
	<div>
	<br/>What is the significance cutoff you want to use? (Gene Y must be minimal x times higher or lower expressed than gene Z to be signifficant (Non log2 values))<br/>
	<textarea rows="2" cols="5" name="signifCutoff" value="cutoff"></textarea>
	</div>

	<div id="submit">
		<input type='submit' id='submitInfo' value='Submit' onclick="__action.value='submitInfoQ2'" />
	</div>

	<#elseif model.state== "QUESTION2_RESULT">
	<p>
		The resulting list of genes will be send to you by email within a few minutes. Please check your spam folder if you don't get an email.
	</p>


<#elseif model.state == "QUESTION3">
	You can convert between probes and genes here.
	<div id="convertGenesProbes">
		Choose the input:<br/>
		<input type="radio" id="convertGenesToProbes" name="convertGP" value="convertGenes" /> Genes<br />
		<input type="radio" id="convertProbesToGenes" name="convertGP" value="convertProbes" /> Probes<br />
	</div>
	
	<div id="geneList">
		<br />Supply the probes/genes you want to convert (comma seperated):<br />
		<textarea rows="10" cols="51" name="gpText" value="convertThese"></textarea>
	</div>
	
	<div id="submit">
		<input type='submit' id='submitInfo' value='Submit' onclick="__action.value='submitInfoQ3'" />
	</div>
	
	<#elseif model.state == "QUESTION3_RESULT">
	<p>
		<#list model.results as convertingResults>
			${convertingResults}<br />
		</#list>
	</p>
	
<#else>
UNKNOWN STATE ${model.state}
</#if>