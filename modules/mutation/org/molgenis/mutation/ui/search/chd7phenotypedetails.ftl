<#assign patientSummaryVO = vo.patientSummaryVO>
<h4><a name="phenotype">Characteristics</a></h4>
<table class="listtable" cellpadding="4">
<#--
<tr class="form_listrow1"><th width="50%">Age</th><td>${patientSummaryVO.patient.getAge()}</td></tr>
<tr class="form_listrow0"><th width="50%">Gender</th><td>${patientSummaryVO.patient.getGender()}</td></tr>
<tr class="form_listrow1"><th width="50%">Ethnicity</th><td>${patientSummaryVO.patient.getEthnicity()}</td></tr>
<tr class="form_listrow0"><th width="50%">Deceased</th><td>${patientSummaryVO.patient.getDeceased()}</td></tr>
<tr class="form_listrow1"><th width="50%">Cause of death</th><td><#if patientSummaryVO.patient.getDeath_Cause()??>${patientSummaryVO.patient.getDeath_Cause()}</#if></td></tr>
<tr class="form_listrow0"><th width="50%">MMP1 allele 1</th><td><#if patientSummaryVO.patient.getMmp1_Allele1()??>${patientSummaryVO.patient.getMmp1_Allele1()}</#if></td></tr>
<tr class="form_listrow1"><th width="50%">MMP1 allele 2</th><td><#if patientSummaryVO.patient.getMmp1_Allele2()??>${patientSummaryVO.patient.getMmp1_Allele2()}</#if></td></tr>
</table>
-->
<#assign listrow = "0">
<#list details.values.fields as key>
<#if key != "id">
<tr class="form_listrow${listrow}"><th>${key}</th><td><#if details.values.getObject(key)??>${details.values.getObject(key)}</#if></td></tr>
<#if listrow == "0"><#assign listrow = "1"><#else><#assign listrow = "0"></#if>
</#if>
</#list>
</table>
<p>
[<a href="javascript:back();">Back to patient</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>