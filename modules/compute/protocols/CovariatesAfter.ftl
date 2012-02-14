#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=00:45:00

###### Renaming because we call another protocol:

#input:
<#assign matefixedbam=sortedrecalbam />

#output:
<#assign matefixedcovariatecsv=sortedrecalcovariatecsv />

<#include "covariates.ftl" />
