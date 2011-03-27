<h3>Submission wizard: New mutation</h3>
<p>
This wizard will guide you through the submission of a mutation that is not yet known in the database.
</p>
<p>
You only need to fill in 4 fields
<ol>
<li>enter position of first nucleotide affected by the mutation</li>
<li>select an event that occurs at position entered</li>
<li>enter substituting nucleotide, inserted nucleotide(s), or number of deleted or duplicated nucleotides</li>
<li>select whether the mutation acts dominantly or recessively in your patient/family</li>
</ol>
</p>
<p>
Press "Assign values".
</p>
<p>
Mutation details will be calculated automatically and displayed below. Please check the results. If you encounter an error, please overwrite or use the comment box. Data will be inserted after curation by the curator.
</p>
<hr/>
<p>* Field is required.</p>
<#assign form = screen.mutationForm>
<script language="JavaScript">
function toggleForm(event)
{
	if (event == "point mutation")
	{
		document.getElementById("substitution_label").style.display = "block";
		document.getElementById("insertion_label").style.display = "none";
		document.getElementById("duplication_label").style.display = "none";
		document.getElementById("deletion_label").style.display = "none";
		document.getElementById("indel_label").style.display = "none";
		document.getElementById("length_input").style.display = "none";
		document.getElementById("indel_label2").style.display = "none";
		document.getElementById("ntchange_input").style.display = "block";
	}
	else if (event == "insertion")
	{
		document.getElementById("substitution_label").style.display = "none";
		document.getElementById("insertion_label").style.display = "block";
		document.getElementById("duplication_label").style.display = "none";
		document.getElementById("deletion_label").style.display = "none";
		document.getElementById("indel_label").style.display = "none";
		document.getElementById("length_input").style.display = "none";
		document.getElementById("indel_label2").style.display = "none";
		document.getElementById("ntchange_input").style.display = "block";
	}
	else if (event == "duplication")
	{
		document.getElementById("substitution_label").style.display = "none";
		document.getElementById("insertion_label").style.display = "none";
		document.getElementById("duplication_label").style.display = "block";
		document.getElementById("deletion_label").style.display = "none";
		document.getElementById("indel_label").style.display = "none";
		document.getElementById("length_input").style.display = "block";
		document.getElementById("indel_label2").style.display = "none";
		document.getElementById("ntchange_input").style.display = "none";
	}
	else if (event == "deletion")
	{
		document.getElementById("substitution_label").style.display = "none";
		document.getElementById("insertion_label").style.display = "none";
		document.getElementById("duplication_label").style.display = "none";
		document.getElementById("deletion_label").style.display = "block";
		document.getElementById("indel_label").style.display = "none";
		document.getElementById("length_input").style.display = "block";
		document.getElementById("indel_label2").style.display = "none";
		document.getElementById("ntchange_input").style.display = "none";
	}
	else if (event == "insertion/deletion")
	{
		document.getElementById("substitution_label").style.display = "none";
		document.getElementById("insertion_label").style.display = "none";
		document.getElementById("duplication_label").style.display = "none";
		document.getElementById("deletion_label").style.display = "none";
		document.getElementById("indel_label").style.display = "block";
		document.getElementById("length_input").style.display = "block";
		document.getElementById("indel_label2").style.display = "block";
		document.getElementById("ntchange_input").style.display = "block";
	}
}
</script>
<table border="0" cellpadding="4" cellspacing="4">
<tr><td>Gene</td><td>${form.gene}</td><td>Reference sequence used</td><td>${form.refseq}</td></tr>
<tr><td>Position *</td><td>${form.position}</td><td>Affected nucleotide(s)</td><td>${form.nt}</td></tr>
<tr>
	<td rowspan="2">Event *</td>
	<td rowspan="2">${form.event}</td>
	<td><div id="duplication_label" style="display:none">Length *</div><div id="deletion_label" style="display:none">Length *</div><div id="indel_label" style="display:none">Length of deletion *</div></td>
	<td>${form.length}</td>
</tr>
<tr>
	<td><div id="substitution_label" style="display:none">Substituted by *</div><div id="insertion_label" style="display:none">Inserted bases *</div><div id="indel_label2" style="display:none">Inserted bases *</div></td>
	<td>${form.ntchange}</td>
</tr>
<tr><td>Founder mutation</td><td>${form.foundermutation}</td><td>Population</td><td>${form.population}</td></tr>
<tr><td>Effect on splicing</td><td>${form.effectonsplicing}</td><td>Dominant or recessive</td><td>${form.inheritance}</td></tr>
<tr><td>Comment</td><td colspan="3">${form.comment}</td></tr>
<tr><td><input type="submit" value="Assign values" onclick="__action.value='assignMutation';return true;"/></td><td></td><td></td><td></td></tr>
<#if screen.action == "assignMutation">
<tr><td colspan="4"><hr/></td></tr>
<tr><td>Position</td><td>${form.readonly_pos}</td><td>Exon</td><td>${form.exon}</td></tr>
<tr><td>Affected nucleotide(s)</td><td>${form.nt_rep}</td><td>changed to</td><td>${form.readonly_ntchange}</td></tr>
<tr><td>Codon number</td><td>${form.codon_number}</td><td>First affected codon number</td><td>${form.codon_number_rep}</td></tr>
<tr><td>First affected codon</td><td>${form.codon}</td><td>changed to</td><td>${form.codonchange}</td></tr>
<tr><td>First affected amino acid</td><td>${form.aa}</td><td>changed to</td><td>${form.aachange}</td></tr>
<tr><td>cDNA notation</td><td>${form.cdna_notation}</td><td>gDNA notation</td><td>${form.gdna_notation}</td></tr>
<tr><td>Amino acid notation</td><td>${form.aa_notation}</td><td>Consequence</td><td>${form.consequence}</td></tr>
<tr><td></td><td></td><td>Type</td><td>${form.type}</td></tr>
<tr><td colspan="4"><hr/></td></tr>
<tr><td></td><td></td><td></td><td align="right"><input type="submit" value="Cancel" onclick="__action.value='newPatient';return true;"/><input type="submit" value="Proceed" onclick="__action.value='insertMutation';return true;"/></td></tr>
</#if>
</table>
<script language="JavaScript">toggleForm(document.getElementsByName("event")[0].value);</script>