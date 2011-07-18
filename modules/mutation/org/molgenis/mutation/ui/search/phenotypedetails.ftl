<#-- Patient details that are not considered to be phenotypic details -->
<#assign patientSummaryVO = vo.patientSummaryVO>
<h4><a name="phenotype">Characteristics</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Age</th><td>${patientSummaryVO.patientAge}</td></tr>
<tr class="form_listrow0"><th width="50%">Gender</th><td>${patientSummaryVO.patientGender}</td></tr>
<tr class="form_listrow1"><th width="50%">Ethnicity</th><td>${patientSummaryVO.patientEthnicity}</td></tr>
<tr class="form_listrow0"><th width="50%">Deceased</th><td>${patientSummaryVO.patientDeceased}</td></tr>
<tr class="form_listrow1"><th width="50%">Cause of death</th><td><#if patientSummaryVO.patientDeathCause??>${patientSummaryVO.patientDeathCause}</#if></td></tr>
<tr class="form_listrow0"><th width="50%">MMP1 allele 1</th><td><#if patientSummaryVO.patientMmp1Allele1??>${patientSummaryVO.patientMmp1Allele1}</#if></td></tr>
<tr class="form_listrow1"><th width="50%">MMP1 allele 2</th><td><#if patientSummaryVO.patientMmp1Allele2??>${patientSummaryVO.patientMmp1Allele2}</#if></td></tr>
</table>

<#-- Observable features -->
<#assign phenotypeDetailsVO = vo.phenotypeDetailsVO>
<#list phenotypeDetailsVO.protocolNames as protocolName>
<h4>${protocolName}</h4>
<table class="listtable" cellpadding="4">
<#assign even = 1>
<#assign observedValueVOs = phenotypeDetailsVO.observedValues[protocolName]>
<#list observedValueVOs as observedValueVO>
<#if even == 1>
  <#assign class = "form_listrow0">
  <#assign even = 0>
<#else>
  <#assign class = "form_listrow1">
  <#assign even = 1>
</#if>
<tr class="${class}"><th width="50%">${observedValueVO.featureName}</th><td>${observedValueVO.value}</td></tr>
</#list>
</table>
</#list>

<#-- Patient material -->
<h4><a name="material">Patient material</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Patient material available?</th><td><#if patientSummaryVO.patientMaterialList?size &gt; 0><#list patientSummaryVO.patientMaterialList as material>${material}<br/></#list><#else>unknown</#if></td></tr>
</table>

<#--
<#assign patientSummaryVO = vo.patientSummaryVO>
<h4><a name="phenotype">Characteristics</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Age</th><td>${patientSummaryVO.patient.getAge()}</td></tr>
<tr class="form_listrow0"><th width="50%">Gender</th><td>${patientSummaryVO.patient.getGender()}</td></tr>
<tr class="form_listrow1"><th width="50%">Ethnicity</th><td>${patientSummaryVO.patient.getEthnicity()}</td></tr>
<tr class="form_listrow0"><th width="50%">Deceased</th><td>${patientSummaryVO.patient.getDeceased()}</td></tr>
<tr class="form_listrow1"><th width="50%">Cause of death</th><td><#if patientSummaryVO.patient.getDeath_Cause()??>${patientSummaryVO.patient.getDeath_Cause()}</#if></td></tr>
<tr class="form_listrow0"><th width="50%">MMP1 allele 1</th><td><#if patientSummaryVO.patient.getMmp1_Allele1()??>${patientSummaryVO.patient.getMmp1_Allele1()}</#if></td></tr>
<tr class="form_listrow1"><th width="50%">MMP1 allele 2</th><td><#if patientSummaryVO.patient.getMmp1_Allele2()??>${patientSummaryVO.patient.getMmp1_Allele2()}</#if></td></tr>
</table>
<h4>Cutaneous</h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Blistering</th><td>${patientSummaryVO.phenotypeDetails.getBlistering()}</td></tr>
<tr class="form_listrow0"><th width="50%">Location</th><td>${patientSummaryVO.phenotypeDetails.getLocation()}</td></tr>
<tr class="form_listrow1"><th width="50%">Hands</th><td>${patientSummaryVO.phenotypeDetails.getHands()}</td></tr>
<tr class="form_listrow0"><th width="50%">Feet</th><td>${patientSummaryVO.phenotypeDetails.getFeet()}</td></tr>
<tr class="form_listrow1"><th width="50%">Arms</th><td>${patientSummaryVO.phenotypeDetails.getArms()}</td></tr>
<tr class="form_listrow0"><th width="50%">Legs</th><td>${patientSummaryVO.phenotypeDetails.getLegs()}</td></tr>
<tr class="form_listrow1"><th width="50%">Proximal body flexures</th><td>${patientSummaryVO.phenotypeDetails.getProximal_Body_Flexures()}</td></tr>
<tr class="form_listrow0"><th width="50%">Trunk</th><td>${patientSummaryVO.phenotypeDetails.getTrunk()}</td></tr>
<tr class="form_listrow1"><th width="50%">Mucous membranes</th><td>${patientSummaryVO.phenotypeDetails.getMucous_Membranes()}</td></tr>
<tr class="form_listrow0"><th width="50%">Skin atrophy</th><td>${patientSummaryVO.phenotypeDetails.getSkin_Atrophy()}</td></tr>
<tr class="form_listrow1"><th width="50%">Milia</th><td>${patientSummaryVO.phenotypeDetails.getMilia()}</td></tr>
<tr class="form_listrow0"><th width="50%">Nail dystrophy</th><td>${patientSummaryVO.phenotypeDetails.getNail_Dystrophy()}</td></tr>
<tr class="form_listrow1"><th width="50%">Albopapuloid papules</th><td>${patientSummaryVO.phenotypeDetails.getAlbopapuloid_Papules()}</td></tr>
<tr class="form_listrow0"><th width="50%">Pruritic papules</th><td>${patientSummaryVO.phenotypeDetails.getPruritic_Papules()}</td></tr>
<tr class="form_listrow1"><th width="50%">Alopecia</th><td>${patientSummaryVO.phenotypeDetails.getAlopecia()}</td></tr>
<tr class="form_listrow0"><th width="50%">Squamous cell carcinoma(s)</th><td>${patientSummaryVO.phenotypeDetails.getSquamous_Cell_Carcinomas()}</td></tr>
<tr class="form_listrow1"><th width="50%">Revertant skin patch</th><td>${patientSummaryVO.phenotypeDetails.getRevertant_Skin_Patch()}</td></tr>
<tr class="form_listrow0"><th width="50%">Mechanism</th><td>${patientSummaryVO.phenotypeDetails.getMechanism()}</td></tr>
</table>
<h4>Extracutaneous</h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Flexion contractures</th><td>${patientSummaryVO.phenotypeDetails.getFlexion_Contractures()}</td></tr>
<tr class="form_listrow0"><th width="50%">Pseudosyndactyly (hands)</th><td>${patientSummaryVO.phenotypeDetails.getPseudosyndactyly_Hands()}</td></tr>
<tr class="form_listrow1"><th width="50%">Microstomia</th><td>${patientSummaryVO.phenotypeDetails.getMicrostomia()}</td></tr>
<tr class="form_listrow0"><th width="50%">Ankyloglossia</th><td>${patientSummaryVO.phenotypeDetails.getAnkyloglossia()}</td></tr>
<tr class="form_listrow1"><th width="50%">Swallowing difficulties/dysphagia/oesophagus strictures</th><td>${patientSummaryVO.phenotypeDetails.getDysphagia()}</td></tr>
<tr class="form_listrow0"><th width="50%">Growth retardation</th><td>${patientSummaryVO.phenotypeDetails.getGrowth_Retardation()}</td></tr>
<tr class="form_listrow1"><th width="50%">Anemia</th><td>${patientSummaryVO.phenotypeDetails.getAnemia()}</td></tr>
<tr class="form_listrow0"><th width="50%">Renal failure</th><td>${patientSummaryVO.phenotypeDetails.getRenal_Failure()}</td></tr>
<tr class="form_listrow1"><th width="50%">Dilated cardiomyopathy</th><td>${patientSummaryVO.phenotypeDetails.getDilated_Cardiomyopathy()}</td></tr>
<tr class="form_listrow0"><th width="50%">Other</th><td><#if patientSummaryVO.phenotypeDetails.getOther()??>${patientSummaryVO.phenotypeDetails.getOther()}</#if></td></tr>
</table>
<#if patientSummaryVO.if_??>
<h4><a name="if">Immunofluorescence antigen mapping</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="25%"><#if patientSummaryVO.if_.getAntibody_Name()??>${patientSummaryVO.if_.getAntibody_Name()}</#if></th><th width="25%">Amount of type VII collagen</th><td>${patientSummaryVO.if_.getValue()}</td></tr>
<tr class="form_listrow0"><th width="50%" colspan="2">Retention of type VII Collagen in basal cells</th><td>${patientSummaryVO.if_.getRetention()}</td></tr>
<tr class="form_listrow1"><th width="50%" colspan="2">Comments</th><td><#if patientSummaryVO.if_.getDescription()??>${patientSummaryVO.if_.getDescription()}</#if></td></tr>
</table>
</#if>
<#if patientSummaryVO.em_??>
<h4><a name="em">Electron Microscopy</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="25%">Anchoring fibrils</th><th width="25%">Number</th><td>${patientSummaryVO.em_.getNumber()}</td></tr>
<tr class="form_listrow0"><th width="25%"></th><th width="25%">Ultrastructure</th><td>${patientSummaryVO.em_.getAppearance()}</td></tr>
<tr class="form_listrow1"><th width="50%" colspan="2">Retention of type VII Collagen in basal cells</th><td>${patientSummaryVO.em_.getRetention()}</td></tr>
<tr class="form_listrow0"><th width="50%" colspan="2">Comments</th><td><#if patientSummaryVO.em_.getDescription()??>${patientSummaryVO.em_.getDescription()}</#if></td></tr>
</table>
</#if>
<h4><a name="material">Patient material</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Patient material available?</th><td><#if patientSummaryVO.material?size &gt; 0><#list patientSummaryVO.material as material>${material}<br/></#list><#else>unknown</#if></td></tr>
</table>
-->

<#--
<#assign listrow = "0">
<#list details.values.fields as key>
<#if key != "id">
<tr class="form_listrow${listrow}"><th>${key}</th><td><#if details.values.getObject(key)??>${details.values.getObject(key)}</#if></td></tr>
<#if listrow == "0"><#assign listrow = "1"><#else><#assign listrow = "0"></#if>
</#if>
</#list>
</table>
-->
<p>
[<a href="javascript:back();">Back to patient</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>