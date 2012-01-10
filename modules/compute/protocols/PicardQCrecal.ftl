#MOLGENIS walltime=20:00:00 mem=5

#inputs:
<#assign sortedbam=sortedrecalbam />

#outputs:
<#assign alignmentmetrics=recalalignmentmetrics />
<#assign gcbiasmetrics=recalgcbiasmetrics />
<#assign gcbiasmetricspdf=recalgcbiasmetricspdf />
<#assign insertsizemetrics=recalinsertsizemetrics />
<#assign insertsizemetricspdf=recalinsertsizemetricspdf />
<#assign meanqualitybycycle=recalmeanqualitybycycle />
<#assign meanqualitybycyclepdf=recalmeanqualitybycyclepdf />
<#assign qualityscoredistribution=recalqualityscoredistribution />
<#assign qualityscoredistributionpdf=recalqualityscoredistributionpdf />
<#assign hsmetrics=recalhsmetrics />
<#assign bamindexstats=recalbamindexstats />

<#include "picardQC.ftl">