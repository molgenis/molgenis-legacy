<h3>Submission wizard: Multiple patients</h3>
<p>
For submission, we strongly encourage to use the provided <a href="res/mutation/col7a1/col7a1_template.xls">excel file</a> in order to standardize the data. You can download this file, copy-paste your data into the worksheet, and upload the file again to our server. You can use this sheet to upload single patients or more patients in the same file. If you wish to upload a single patient without using the excel sheet, select �Submit single patient�. Submitted data will be curated by the curator before insertion. In case of missing or unclear data, we will contact you. Therefore, please provide a valid email address. Unpublished data will be marked as such in the database.
</p>
<p>
<i>Patient consent</i><br/>
You need to indicate that the patient consented for inclusion in the database, even if the data in the database are anonymised. If no consent is given, only a phenotype, genotype, and results of immunofluorescence and electron microscopy analyses will be shown, without any details.
</p>
<p>
For obtaining consent, you can download the provided <a href="res/mutation/col7a1/consent_form.pdf">consent form</a>. We do currently not require you to upload consent forms to our system. By indicating that the patient consented, you confirm that a signed consent form is in your possession.
</p>
<hr/>
<br/>
<table border="0" cellpadding="4" cellspacing="4">
<tr>
	<td>File name:</td>
	<td><input type="file" name="upload"/><input type="submit" value="Upload" onclick="__action.value='insertBatch';return true;"/></td>
	<td><a href="res/mutation/col7a1/col7a1_template.xls">Example template</a></td>

	<td><a href="molgenis.do?__target=${screen.name}&__action=newPatient">Submit single patient</a></td>

</tr>
</table>