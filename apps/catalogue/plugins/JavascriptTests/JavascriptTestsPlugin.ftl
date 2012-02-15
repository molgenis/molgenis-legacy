<#macro plugins_JavascriptTests_JavascriptTestsPlugin screen>
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
			<div class="screenpadding">	
			
				${screen.getJavascriptTest()}
				
				<h1>Javascripts test lemeee</h1>
				<p id="demo">This is a paragraph.</p>
				
				<button type="button" onclick="displayDate()">Display Date</button>
				
				<h3>This is from inline js.</h3>
				
				<script type="text/javascript">
					document.write("<p>" + Date() + "</p>");
				</script>
				
				
				<h4> The example below writes the current date into an existing <p> element: </h4>
				<p id="demo2"></p>
				
				<script type="text/javascript">
				document.getElementById("demo2").innerHTML=Date();
				</script>
				
				<button type="button" onclick="simpleStatements()">JS teSTs</button>
				<button type="button" onclick="simpleStatementsonSAMEPAGE()">JS teSTs On SAmE page</button>
				<button type="button" onclick="AlgOnSAMEPAGE()">Alg teSTs On SAmE page</button>
				<button type="button" onclick="goodmorningCondition()">press</button>
				<button type="button" onclick="anoTherTEst()">press</button>
				<input type="button" onclick="show_alert()" value="Show alert box" />
				
				document.write(product(4,3));
				
				
				
				
				
			</div> <!--<div class="screenpadding"-->	
		</div> <!--		<div class="screenbody"-->		
	</div> <!--	<div class="formscreen"-->

	
</form>

</#macro>
