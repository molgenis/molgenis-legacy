#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$ 
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#
# README for the MOLGENIS/compute script generator (command line version)
#

WARNING: Please remove any previous version of molgenis_compute before unzipping a next version. Simply unzipping a new version on top of another version will disable its execution. Reason unknown.  

To run the MOLGENIS/compute script generator

1. You may want to change the permissions of the script to start MOLGENIS/compute:

	$> chmod 755 molgenis_compute.sh

2. Simply execute molgenis_compute.sh without parameters to get an up to date listing of command line options:

	$> commandline_generator.sh

3. As an example, one can generate generate a Next Generation Sequencing (NGS) workflow on a small test set with real data by executing the following:   

sh molgenis_compute.sh \
-parametersfile=workflows/in-house_parameters.csv \
-workflowfile=workflows/workflow_inhouse.csv \
-worksheet=workflows/worksheetTest.csv \
-protocoldir=protocols \
-outputscriptsdir=scripts \
-cluster=dummy \
-templatesdir=dummy dummy
	
N.B. Please note that blank spaces before and after the '=' are not allowed when specifying the parameters.

	
Questions, bug reports or feature requests? Please contact the MOLGENIS/compute developers via gcc-ngs@googlegroups.com or visit http://molgenis.org.