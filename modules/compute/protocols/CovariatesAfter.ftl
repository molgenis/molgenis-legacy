#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#


###### Renaming because we call another protocol:

#input:
<#assign matefixedbam=sortedrecalbam />

#output:
<#assign matefixedcovariatecsv=sortedrecalcovariatecsv />

<#include "covariates.ftl" />
