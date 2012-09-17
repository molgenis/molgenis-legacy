<#macro ExpTable screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<input type="hidden" name="__shopMeName">
	<input type="hidden" name="__shopMeId">
	
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
		
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<br>
<table border="1" bgcolor="#EAEAEA">
	<tr>
		<td>
			<i>Phenotypes</i>
		</td>
		<td>
			<i>Type of array</i>
		</td>
		<td>
			<i>Sample size</i>
		</td>
		<td>
			<i>Parental strains</i>
		</td>
		<td>
			<i>Reference</i>
		</td>
		<td>
			<i>Pubmed link</i>
		</td>
		<td>
			<i>Growing temperature</i>
		</td>
		<td>
			<i>Stage</i>
		</td>
		<td>
			<i>Food</i>
		</td>
		<td>
			<i>Medium</i>
		</td>
		<td>
			<i>Dataset IDs</i>
		</td>
	</tr>
	<tr>
		<td>
			Gene expression
		</td>
		<td>
			Washington State University
		</td>
		<td>
			2x40 RILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Li et al. 2006;</b> Mapping determinants of gene expression plasticity by genetical genomics in C. elegans.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/17196041">17196041</a>
		</td>
		<td>
			16oC and 24oC
		</td>
		<td>
			(72h at 16 and 40h at 24); L4
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=37">37</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=38">38</a>
		</td>
	</tr>
	<tr>
		<td>
			Gene expression
		</td>
		<td>
			Affymatrix tiling array
		</td>
		<td>
			60 RILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Li et al. 2010;</b> Global genetic robustness of the alternative splicing machinery in Caenorhabditis elegans.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/20610403">20610403</a>
		</td>
		<td>
			24oC
		</td>
		<td>
			(40h) L4
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			n/a
		</td>
	</tr>
	<tr>
		<td>
			Gene expression
		</td>
		<td>
			Washington State University
		</td>
		<td>
			36x3 RILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Vinuela & Snoek et al. 2010;</b> Genome-wide gene expression regulation as a function of genotype and age in C. elegans.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/20488933">20488933</a>
		</td>
		<td>
			24oC
		</td>
		<td>
			(40h, 96h and 214h) L4, Adult, Old
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=3">3</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=5">5</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=6">6</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=7">7</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=8">8</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=9">9</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=10">10</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=11">11</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=12">12</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=13">13</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=14">14</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=15">15</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=16">16</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=17">17</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=18">18</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=19">19</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=20">20</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=21">21</a>
		</td>
	</tr>
	<tr>
		<td>
			Gene expression
		</td>
		<td>
			Agilent 4x44k microarrays
		</td>
		<td>
			208 RIAILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Rockman et al. 2010;</b> Selection at linked sites shapes heritable phenotypic variation in C. elegans. 
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/20947766">20947766</a>
		</td>
		<td>
			20oC
		</td>
		<td>
			YA
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=22">22</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=34">34</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=35">35</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=36">36</a>
		</td>
	</tr>
	<tr>
		<td>
			Feeding curves RNAi exposure
		</td>
		<td>
			n/a
		</td>
		<td>
			56 RILs * 12 RNAi
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Elvin & Snoek et al. 2011;</b> A fitness assay for comparing RNAi effects across multiple C. elegans genotypes.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/22004469">22004469</a>
		</td>
		<td>
			20oC
		</td>
		<td>
			Multi-generational
		</td>
		<td>
			n/a
		</td>
		<td>
			Liquid S-medium
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=24">24</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=32">32</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=33">33</a>
		</td>
	</tr>
	<tr>
		<td>
			Life-history traits
		</td>
		<td>
			n/a
		</td>
		<td>
			80 RILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Gutteling et al. 2007;</b> Mapping phenotypic plasticity and genotype-environment interactions affecting life-history traits in Caenorhabditis elegans.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/16955112">16955112</a>
		</td>
		<td>
			12oC and 24oC
		</td>
		<td>
			Egg, L4, YA
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=25">25</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=26">26</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=27">27</a>
		</td>
	</tr>
	<tr>
		<td>
			Lifespan and pharyngeal-pumping
		</td>
		<td>
			n/a
		</td>
		<td>
			 90 NILs
		</td>
		<td>
			CB4856; N2
		</td>
		<td>
			<b>Doroszuk et al. 2009;</b> A genome-wide library of CB4856/N2 introgression lines of Caenorhabditis elegans.
		</td>
		<td>
			<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pubmed/19542186">19542186</a>
		</td>
		<td>
			20oC
		</td>
		<td>
			All; synchronised
		</td>
		<td>
			OP50
		</td>
		<td>
			NGM Plate
		</td>
		<td>
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=4">4</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=23">23</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=28">28</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=29">29</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=30">30</a>, 
			<a href="?select=Datas&__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=31">31</a>
		</td>
	</tr>
</table>
<br>

	</div>
</form>
</#macro>
