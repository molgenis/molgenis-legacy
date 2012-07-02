<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	
<div style="background: white">


<table>
<tr><td colspan="2">

<h1>Welcome at LifeLines biobank variable catalogue</h1>
<p>Use this catalog to explore the data items available in Lifelines and request data.</p>

</td></tr>
<tr><td>
<h2>About LifeLines biobank</h2>

<p>The aim of the LifeLines project is to study universal risk factors and their modifiers for 
multifactorial diseases. </p><p>LifeLines is carried out in a representative sample of 165.000 participants 
from the northern provinces of the Netherlands and employs a three-generation family 
design (proband design with relatives). The LifeLines Biobank is a nation-wide facility and institutes a "Hotel or Host function" for:
standardised isolation and storage of DNA, serum, plasma and urine; linkage of these human tissue samples to high quality medical records and phenotypic databases; and
cross-cohort analyses with other European cohorts by standardised genotyping and biochemical 
sample analysis. For more information on LifeLines, please contact: <a href="mailto:s.scholtens@umcg.nl>">Salome Scholtens</a></p>

<h2>About this software</h2>
<p>
This catalogue software developed jointly by the <a href="http://wiki.gcc.rug.nl">Genomics</a> 
and the <a href="http://www.trailcoordinationcenter.nl">Trial</a> Coordination Centers of the <a href="http://www.umcg.nl">UMC Groningen</a>.
</p>
<p>
This work is part of the larger collaboration on catalogue harmonisation in <a href="http://www.bbmri.nl/">BBMRI-NL bioinformatics rainbow project</a>, 
the <a href="http://www.nbic.nl">NBIC/biobanking task force</a>, 
 <a href="http://p3gobservatory.org">P3G/Obiba</a> and <a href="http://www.bioshare.eu">EU-BioSHArE</a>. 
 The catalogue is structured compatible to the international <a href="http://www.observ-om.org">Observ' data standard for life science observation data</a>, 
 implemented using the <a href="http://www.molgenis.org">MOLGENIS</a> open source software platform and hosted by the <a href="target">Target</a> infrastructure project as part of [BEZWERINGSFORMULE INVULLEN]. 
 For more information on the software, please contact: <a href="mailto:m.a.swertz@rug.nl">Morris Swertz</a>.
</p>
</td>

<td width="400px;">
<h2>&nbsp;</h2>
<div style="line-height: 200%; background-color: #DCE3F7; height: 100%;">
<b>Using this catalogue:</b>
<ul><li><a href="molgenis.do?__target=main&select=catalogueTreePlugin">Search</a> and select variables.</li>
<li><a href="molgenis.do?__target=main&select=investigation">Browse</a> studies and their protocol details.</li>
<li><a href="molgenis.do?__target=main&select=MySelections">Create</a> personal variable selections.</li>
<li><a href="molgenis.do?__target=main&select=Request">Request</a> a data set.</li>
</ul>
</div>

</td>

</tr></table>

</div>

	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
