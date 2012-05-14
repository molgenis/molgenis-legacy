#                                                                                                                       
# A script to make testing of MOLGENIS/COMPUTE easier
#                                                                                                                       
# =====================================================                                                                 
# $Id$                                            
# $URL: http://www.bbmriwiki.nl/svn/ngs_scripts/trunk/extract_samples_from_GAF_list.pl $                                
# $LastChangedDate$                                                      
# $LastChangedRevision$                                                                                           
# $LastChangedBy: mdijkstra $                                                                                           
# =====================================================                                                                 
#

sh $(dirname -- "$0")/molgenis_compute.sh \
-worksheet=$(dirname -- "$0")/workflows/in-house_worksheet_test.csv \
-parameters=$(dirname -- "$0")/workflows/in-house_parameters.csv \
-workflow=$(dirname -- "$0")/workflows/create_in-house_ngs_projects_workflow.csv \
-protocols=$(dirname -- "$0")/protocols \
-scripts=$(dirname -- "$0")/generatedscripts \
-templates=$(dirname -- "$0")/templates \
-id=testRun