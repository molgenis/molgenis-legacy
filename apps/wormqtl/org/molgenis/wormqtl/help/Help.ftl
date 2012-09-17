<#macro Help screen>
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

<h2>Help</h2>

<h3>For biologists:</h3>

<ul>
	<li>
		Download the <a href="walkthrough.docx">walkthrough of WormQTL.org</a>.
	</li>
</ul>

<h3>For bioinformaticians:</h3>

<h4>API's:</h4>
<ul>
	<li>
		<a href="api/find/">Scriptable interface</a> which you can use to download annotations programmatically via the web, including the use of filters.
	</li>
	<li>
		<a href="api/R/">R API</a> to query via R. Copy-paste this into an R terminal to connect. The first time you may need to install Bitops and RCurl. (see instructions)
	</li>
</ul>

<h4>Downloaders:</h4>
<ul>
	<li>
		<a href="downloadmatrixascsv">Matrix CSV downloader</a> to download all experimental data programmatically. Includes options for subsets and streaming.
		<br>
		Some concrete xamples:
		<ul>
			<li>
				Genotypes: <a href="downloadmatrixascsv?id=24&download=all&stream=false">CBxN2_WUR_RILs</a> (Panel x Marker) / <a href="downloadmatrixascsv?id=22&download=all&stream=false">CBxN2_USA_RILs</a> (Marker x Panel)
			</li>
			<li>
				Classical phenotypes: <a href="downloadmatrixascsv?id=31&download=all&stream=false">nil_pump_phe</a> (Measurement x Panel) / <a href="downloadmatrixascsv?id=27&download=all&stream=false">gut_phe</a> (Panel x Measurement)
			</li>
			<li>
				Expression phenotypes: <a href="downloadmatrixascsv?id=20&download=all&stream=false">age3_exp_norm_ril</a> (Sample x Probe) / <a href="downloadmatrixascsv?id=35&download=some&stream=false&coff=0&roff=0&clim=100&rlim=1000">rock_exp_norm</a> (Sample x Probe)
			</li>
		</ul>
	</li>
	<li>
		<a href="downloadmatrixasexcel">Matrix Excel downloader</a> to download all experimental data as Excel files. Similar to the CSV downloader, except no streaming options. Example: <a href="downloadmatrixasexcel?id=24&download=all">CBxN2_WUR_RILs</a>
	</li>
	<li>
		<a href="downloadfile">File downloader</a> to get the matrices as <a href="https://raw.github.com/joerivandervelde/molgenis_apps/master/modules/xgap/matrix/implementations/binary/etc/design.txt">xQTL binaries</a>. This goes by name, e.g. <a href="downloadfile?name=CBxN2_USA_RILs">CBxN2_USA_RILs</a>. You can get a list of all names of matrices by <a href="api/find/Data">via the scriptable interface</a>. In addition, you can also download all <a href="api/find/InvestigationFile">GFF files</a> with QTL results via this downloader.
	</li>
</ul>

<h4>Documentation:</h4>
<ul>
	<li>
		 <a href="generated-doc/fileformat.html">Exchange format</a>: a full description of the datamodel.
	</li>
	<li>
		<a href="http://molgenis02.target.rug.nl/download/wormqtl/wormqtl_set1_annotations_minusUSAprobes.xls">WormQTL annotation data</a> (the largest part) in a single Excel file, as a resource and to help people understand the format.
	</li>
	<li>
		<a target="_blank" href="http://www.molgenis.org/wiki/xQTL">xQTL wiki</a> with all general documentation regarding installing and using <a href="http://xqtl.org/">xQTL workbench</a>.
	</li>
</ul>



<br>

	</div>
</form>
</#macro>
