<#macro screens_AskParametersScreen screen>
<#assign default=screen.getDefaultParameters()/>
<div class="parameters">
	<form method="post" enctype="multipart/form-data" name="askparameters">
		<input type="hidden" name="__target" value="${screen.name}"/>
<table>
	<tr>
		<td style="text-align: center">
		<table border="0" width="100%">
		<#if !screen.isBArgumentsOK() >
			<tr>
				<td><font color="red"> Required parameter missing: </font><b>${screen.argMissing}</b></td>
			</tr>
		</#if>
			<tr>
				<td>
				<fieldset><legend>1. Define analysis platform <a href="designgg/help.html"><img src="res/img/designgg/helpicon.png" height="24" align="top" border="0"/></a>
				</legend>
				<table width="100%">
					<tr>
						<td align="center"><input type="radio" name="twocolorarray"
							value="false" <#if !default.twocolorarray>checked</#if>><label>Single channel </label><br />
						<img src="res/img/designgg/singlechannel150px.jpg" height="150" /></td>
						<td align="center"><input type="radio" name="twocolorarray"
							value="true" <#if default.twocolorarray>checked</#if>><label>Dual channel</label><br />
						<img src="res/img/designgg/twochannel150px.jpg" height="150" /></td>
					</tr>
				</table>
				</fieldset>
				</td>
			</tr>
			<tr>
				<td align="left">
				<fieldset><legend>2. Define individual genotypes <a href="designgg/help.html"> <img src="res/img/designgg/helpicon.png" height="24" align="top" border="0"/></a>
						  </legend><label><br/><i>Upload tab delimited file, e.g.</i></label> <img
					src="res/img/designgg/rils150px.gif" valign="bottom"/>
					<input type="file" name="genotype" style="display: inline" />
					<a href="designgg/genotype_example.txt" style="text-decoration: none"> 
						<p style="font-size:small;display: inline;color: black;">
						&nbsp;&nbsp;&nbsp;<u>Example file</u>&nbsp;</p></a>
					<script type="text/javascript">
					//add new input to ranges list
					function addRange(nameOfContainer)
					{
						var br = document.createTextNode(' and ');
						var dash = document.createTextNode(' - ');
					
						var newinput = document.createElement('input');
						newinput.setAttribute('name','range_start');
						newinput.setAttribute('size',3);
						
						var newinput2 = document.createElement('input');
						newinput2.setAttribute('name','range_end');
						newinput2.setAttribute('size',3);
						
						var container = document.getElementById(nameOfContainer);
						buttons = container.getElementsByTagName('button');
						
						container.insertBefore(br,buttons[0]);
						container.insertBefore(newinput,buttons[0]);
						container.insertBefore(dash,buttons[0]);
						container.insertBefore(newinput2,buttons[0]);
						return false;						
					}
					function removeRange(nameOfContainer)
					{
						var container = document.getElementById(nameOfContainer);
						var inputs = container.childNodes;
						//skipt the buttons, remove rest if any
						//alert(inputs.length);
						if(inputs.length > 7)
						{
							container.removeChild(inputs[inputs.length-3]);
							container.removeChild(inputs[inputs.length-3]);
							container.removeChild(inputs[inputs.length-3]);
							container.removeChild(inputs[inputs.length-3]);
						}
					}
					</script>
					<div style="text-align: right"><input type="submit" value="show advanced options" onclick="options_div = document.getElementById('options1'); if(options_div.style.display == 'none') { options_div.style.display = 'block'; this.value = 'hide advanced options';} else {options_div.style.display = 'none'; this.value = 'show advanced options';} return false;" align="right"/></div>
					<div id="options1" class="optional" align="left" style="display: none">
					<div id="options1_buttons">
					<label>Optionally, select marker range(s) of interest: </label><br/>
					<input type="text" size="3" name="range_start"/> - <input type="text" size="3" name="range_end"/>
					<button type="button" onclick="addRange('options1_buttons');">+</button><button type="button" onclick="removeRange('options1_buttons')">-</button></div>
					<div style="font: menu"><i>valid marker numbers are in range 1 .. count(markers)</i></div>
					</div>
				</fieldset>
				</td>
			</tr>
			<tr>
				<script type="text/javascript">
					//add new input to a named list
					function addInput(name, nameOfContainer)
					{
						var newinput = document.createElement('input');
						newinput.setAttribute('name',name);
						newinput.setAttribute('size',6);
						newinput.setAttribute('value','editme');
						
						//before button
						var container = document.getElementById(nameOfContainer);
						buttons = container.getElementsByTagName('button');
						container.insertBefore(newinput,buttons[0]);
						return false;
					}
					function removeInput(name, nameOfContainer)
					{
						var container = document.getElementById(nameOfContainer);
						var inputs = container.getElementsByTagName('input');
						container.removeChild(inputs[inputs.length-1]);
					}
					function toggleEnabled(nameOfInput, nameOfContainer, isDisabled)
					{
						document.getElementById(nameOfInput).disabled = isDisabled;//label
						//cannot join nodelists, so therefore two for loops
						var container = document.getElementById(nameOfContainer);
						
						buttons = container.getElementsByTagName('button');//buttons
						for(var j in buttons)
						{
							buttons[j].disabled = isDisabled;
						}
						
						var inputs = container.childNodes;//inputs 
						for(var i in inputs)
						{
							//alert(inputs[i].name + " " + inputs[i].value + inputs[i].tagname);
							inputs[i].disabled = isDisabled;
						}
						
						//update the model too
						toggleModel();
						return false;
					}
					function toggleModel()
					{
						var factorN = 0;
						if( document.getElementById('factor1label').disabled != true ) factorN++;
						if( document.getElementById('factor2label').disabled != true  ) factorN++;
						if( document.getElementById('factor3label').disabled != true ) factorN++;
						
						//alert(factorN);
						
						//first disable everything, then enable
						document.getElementById('model1div').style.display = "none";
						document.getElementById('model2div').style.display = "none";
						document.getElementById('model3div').style.display = "none";

						if(factorN >= 1) 
						{
							document.getElementById('model1div').style.display = "inline";
							if(factorN >= 2) 
							{
								document.getElementById('model2div').style.display = "inline";
								if(factorN >= 3) 
								{
									document.getElementById('model3div').style.display = "inline";
								}
							}
						}					
					}
				</script>
				<td align="center">
				<fieldset><legend>3. Define experimental factors <a href="designgg/help.html"> <img src="res/img/designgg/helpicon.png" height="24" align="top" border="0"/></a>
						  </legend>
				<table class="darkblue" cellspacing="0">
					<tr>
						<td>&nbsp;</td>
						<td><label>Factors</label></td>
						<td><label>Levels</label></td>
					</tr>
					<tr>
						<td><input name="factor1active" value="true" type="checkbox" checked onclick="toggleEnabled('factor1label','factor1div', !this.checked);"/>
						<td><div style="float: left;"><input value="TempCelsius" id="factor1label" name="factor1label"/></div></td>
						<td><div style="float: left;" id="factor1div"><input name="factor1level" value="15" size="6" /><input name="factor1level" value="24" size="6" /><input name="factor1level" value="29" size="6" />
						<button type="button" onclick="addInput('factor1level','factor1div');">+</button><button type="button" onclick="removeInput('factor1level','factor1div')">-</button></div>
					</tr>
					<tr>
						<td><input name="factor2active" value="true" type="checkbox" checked  onclick="toggleEnabled('factor2label','factor2div', !this.checked);"/>
						<td><div style="float: left;"><input value="Tissue" id="factor2label" name="factor2label"/></div></td>
						<td><div style="float: left;" id="factor2div"><input name="factor2level" value="Brain" size="6" /><input name="factor2level" value="Liver" size="6" /><input name="factor2level" value="Kidney" size="6" />
						<button type="button" onclick="addInput('factor2level','factor2div');">+</button><button type="button" onclick="removeInput('factor2level','factor2div')">-</button></div>
					</tr>
					<tr>
						<td><input name="factor3active" value="false" type="checkbox" onclick="toggleEnabled('factor3label','factor3div', !this.checked);"/>
						<td><div style="float: left;"><input value="Factor 3" id="factor3label" name="factor3label" disabled /></div></td>
						<td><div style="float: left;" id="factor3div"><input name="factor3level" value="1" size="6" disabled /><input name="factor3level" value="2"
							size="6" disabled /><input name="factor3level" value="3" size="6" disabled />
							<button type="button" onclick="addInput('factor3level','factor3div');" disabled>+</button><button disabled type="button" onclick="removeInput('factor3level','factor3div')">-</button></div>	
					</tr>
				</table>
				<br/>
				<div style="text-align: right"><input type="submit" value="show advanced options" onclick="options_div = document.getElementById('options2'); if(options_div.style.display == 'none') { options_div.style.display = 'block'; this.value = 'hide advanced options';} else {options_div.style.display = 'none'; this.value = 'show advanced options';} return false;" align="right"/></div>
				<div id="options2" class="optional" align="left" style="display: none">
				<label style="font: menu">Optionally, change model weights on genetic or environmental factors and/or interactions:</label><br/>
				<table>
				<tr><td style="vertical-align: top"><input type="text" value="1" size="2" name="weight"/>*Q <br/>
				<div id="model1div" style="display: inline;">+ <input type="text" value="1" size="2" name="weight"/>*F1  + <input type="text" value="1" size="2" name="weight"/>*Q*F1</div>
				<div id="model2div" style="display: inline;"><br/>+ <input type="text" value="1" size="2" name="weight"/>*F2  + <input type="text" value="1" size="2" name="weight"/>*Q*F2  + <input type="text" value="1" size="2" name="weight"/>*F1*F2  + <input type="text" value="1" size="2" name="weight"/>*Q*F1*F2</div>
				<div id="model3div" style="display: none;"><br/>+ <input type="text" value="1" size="2" name="weight"/>*F3  + <input type="text" value="1" size="2" name="weight"/>*Q*F3  + <input type="text" value="1" size="2" name="weight"/>*F1*F3  + <input type="text" value="1" size="2" name="weight"/>*F2*F3 
				<br/>+ <input type="text" value="1" size="2" name="weight"/>*Q*F1*F3  + <input type="text" value="1" size="2" name="weight"/>*Q*F2*F3  + <input type="text" value="1" size="2" name="weight"/>*F1*F2*F3  + <input type="text" value="1" size="2" name="weight"/>*QF1*F2*F3</div>
				</td></tr></table>
				
				</div>
				
				
				<!--<p align="right" style="margin: 0px;">Show advanced options:<input type="submit" value="+" disabled></p>-->
				</fieldset>
				</td>
			</tr>
			<tr>
				<td>
				<fieldset><legend>4. Set constraints <a href="designgg/help.html"><img src="res/img/designgg/helpicon.png" height="24" align="top" border="0"/></a></legend> 
				<table>
				<tr><td>
				<input
					type="radio" name="constraint" value="" onclick="document.askparameters.noslides.disabled=false;document.askparameters.norilsperlevel.disabled=true;">Total number of slides</td><td><input size="2" name="noslides" value="60" disabled>
				</td></tr>
				<tr><td>	
				<input type="radio" name="constraint" value="norilsperlevel" checked onclick="document.askparameters.noslides.disabled=true;document.askparameters.norilsperlevel.disabled=false;">Number of strains per level</td><td><input name="norilsperlevel" size="2" value="4"></td></tr></table>
				</fieldset>
				</td>
			</tr>
		</table>
		<input type="submit" value="Optimize Experiment Design"	style="font: 14pt Arial">
		<input type="submit" value="Test" name="test" style="font: 14pt Arial">
		</td>
		<td>
		<div class="description">Understanding the functioning of biological systems is 
		a major challenge in light of the complex interplay of genes and environment. 
		<p/>Combining biomolecular profiling of genetic divergent individuals under one or 
		more different environmental conditions can provide the data for unraveling this 
		puzzle, a strategy known as genetical genomics. 
		<p/>Here we present an R package and web tool to optimally design such 
		genetical genomics experiments. 
		</p>
		<p>Help:
		<ul>
		<li><a href="designgg/help.html" style="text-decoration: underline">manual of this web tool</a>
		<li><a href="designgg/parameters.html" style="text-decoration: underline">explanation of designGG parameters</a> 
		<li><a href="designgg/examples.html" ref="help.html" style="text-decoration: underline">example inputs/outputs</a>
		<li><a href="designgg/references.html" style="text-decoration: underline">literature references</a> 
		</ul>
		<p/>		
		Credits:<br/><i> Yang Li, <a href='http://www.molgenis.org' target='_morris'>Morris Swertz</a>, Gonzalo Vera, <a href='http://www.dannyarends.nl' target='_danny'>Danny Arends</a>, 
		Rainer Breitling, Ritsert Jansen.</i><br>
		<br>
		Download: <br/><a href="designgg/designGG_1.0-02.zip">designGG R package</a> and <a href="designgg/designGG.pdf">package manual</a><br/><br/> 
		<center><a href="http://www.rug.nl" target="_rug"><img src="res/img/designgg/rug_logo.png"></a></center>
		</div>
		</td>
	</tr>
</table>
	</form> 
</div>	
</#macro>