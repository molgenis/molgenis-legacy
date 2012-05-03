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

	$> molgenis_compute.sh

3. Next Generation Sequencing (NGS) workflow example

   You can generate a Next Generation Sequencing (NGS) workflow for a small test set by executing the following:   

	$> sh molgenis_compute.sh \
			-parameters=workflows/in-house_parameters.csv \
			-workflow=workflows/in-house_workflow.csv \
			-worksheet=workflows/in-house_worksheet_test.csv \
			-protocols=protocols \
			-templates=dummy \
			-scripts=scripts \
			-id=run01
	
Please note that:
 * White space before and after the '=' is not allowed when specifying the parameters.
 * The raw data for the samples described in worksheetTest.csv is not packaged with MOLGENIS/compute, 
   so you can use worksheetTest.csv to test the generate of job scripts, but you cannot execute those example scripts
   successfully without real raw data.
 * The -templates param is not functional yet, but you have to specify it anyway (work in progress).
 * The last parameter specified (run01 in the example above) is the ID/name of this set of generated scripts.
   Choose something that suits your needs.
 * All commandline arguments are available as special MOLGENIS/compute parameters, so you can use them in the Freemarker templates for your protocols.
   The syntax for these special params: param name starting with a capital and prefixed with Mc. 
   For example to use the path to the parameters file you specified on the cammandline you can use ${McParameters} in your protocols.
   In addition to the commandline arguments there is one more special MOLGENIScompute param "$McDir}, 
   which points to the dir where MOLGENIS/compute is installed == where molgenis_compute.sh is stored. 
	
Questions, bug reports or feature requests? Please contact the MOLGENIS/compute developers via gcc-ngs@googlegroups.com or visit http://molgenis.org/.