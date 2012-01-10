#
# =====================================================
# $Id: covariatesAfter.ftl 10290 2011-12-23 14:33:50Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/covariatesAfter.ftl $
# $LastChangedDate: 2011-12-23 15:33:50 +0100 (Fri, 23 Dec 2011) $
# $LastChangedRevision: 10290 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=45:59:00 mem=4 cores=1

#input:
<#assign matefixedbam=sortedrecalbam />

#output:
<#assign matefixedcovariatecsv=sortedrecalcovariatecsv />

<#include "covariates.ftl" />
