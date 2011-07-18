<!--Date:        May 15, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_help_general_GeneralHelpPlugin screen>
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
<h1>Welcome to XGAP 1.4</h1>

<h2>What are the difference with 1.2?</h2>

<ul>
	<li>
	<h3>Alternatives for storage</h3>
	You can choose between several ways to store your experimental data. Each behaves the same, but some will offer you special functionality. For example:
		<ul>
			<li>Database: Suitable for small datasets. Pros: on the fly editing (TODO), relational constraint enforcement. Cons: Uses relatively large amounts of disk space, relatively slow to import/export, can be overly strict.</li>
			<li>Binary: Suitable for large datasets. Pros: etc</li>
			<li>Plain: Suitable for medium datasets. Pros: etc</li>
		</ul>
	</li>
	<li>
	<h3>Strong names</h3>
	The 'name' field for any entity must now adhere to some rules. This will ensure that all your data is usable in the enviromments where you can export it to, for example R. etc
	</li>
</ul>

<h2>What can I do with it?</h2>

<ul>
	<li>
	<h3>Robustize</h3>
	We offer tools and tutorials to make your data consistent and reusable
	</li>
	<li>
	<h3>Store</h3>
	Most of the system is relation but we also offer smart solutions for high throughput data. By choosing the 'Binary' storage, your data is stored efficiently. This whole original file can be downloaded, but you can also export (parts or everything) to Excel or Csv.
	</li>
	<li>
	<h3>View</h3>
	Any value in the system can be viewed in default MOLGENIS interfance or the matrix manager plugin. You can also create simple graphs via the Rplot plugin. (TODO: fix)
	</li>
	<li>
	<h3>Exchange</h3>
	You can export the data manually in many ways, but to export an entire investigation or even all investigations you can use the Archiver plugin. This plugin creates neatly wrapped packages of your data that can be imported again at will. You can ofcourse also unpack the archive and do whatever you want. Its internal format can be Excel or Csv.
	</li>
</ul>

<h2>So.. how do I use it?</h2>
<ul>
	<li>
	<h3>Get your data in XGAP format and import</h3>
		<ul>
			<li>You have existing data. See XGAP tutorials, 'Naming' plugin to help you with the names. Import using "Add in batch/upload CSV". Create data matrices and upload the files. Or, use Wizard (TODO).</li>
			<li>You have no data. Use the regular interface to create and edit records. To get matrix-like data in the system, you can just type it in the text area in the matrix manager plugin.</li>
		</ul>
	</li>
	<li>
	<h3>Analyze the data in your favorite tool</h3>
		<ul>
			<li>Example for R</li>
			<li>Example for R/qtl and helpful links</li>
			<li>Example for Excel ?</li>
			<li>Example for SPSS ?</li>
			<li>etc ?</li>
		</ul>
	</li>
	<li>
	<h3>Share</h3>
		<ul>
			<li>How to get your XGAP system online</li>
			<li>How to create an Archive and how to import them</li>
			<li>How to describe your analysis protocol and how to share it (TODO)</li>
		</ul>
	</li>
</ul>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
