#
# =====================================================
# $Id: Zipfq.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Zipfq.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=10:00:00 nodes=1 cores=1 mem=10

<#if seqType == "SR">
	# assume single read
	alloutputsexist ${leftbarcodefqgz}
	inputs ${leftbarcodefq}	
<#else>
	# assume paired end
	alloutputsexist ${leftbarcodefqgz} ${rightbarcodefqgz}
	inputs ${leftbarcodefq} ${rightbarcodefq}
</#if>

# The following code gzips files and removes original file
# However, in the case of a symlink, the symlink is removed.
	gzip -f ${leftbarcodefq}
<#if seqType == "PE">
	gzip -f ${rightbarcodefq}
</#if>