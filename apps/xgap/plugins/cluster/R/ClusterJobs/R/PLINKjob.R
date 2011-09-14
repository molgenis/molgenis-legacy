#####################################################################
#
# QTLjob.R
#
# copyright (c) 2009-2011, Danny Arends
# last modified Jan, 2011
# first written Feb, 2010
# 
# Part of the ClusterJobs package
# Contains: run_QTL
#
######################################################################

######################################################################
#
# run_QTL: Generates a QTL file which is executed (R CMD BATCH <qtlfile>) by a cluster
#
######################################################################


run_PLINK <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("map","scanone"),c("method","hk"),c("model","normal"),c("stepsize","0")), investigationname="", libraryloc=NULL){
  cat("info: Start by sending a message (so we know we're running)\n")
  
  cat("info: Get your parameters\n")
  inputname <- getparameter("inputname",jobparams)
  outputname <- getparameter("outputname",jobparams)
  
  cat(Generate_Statement(paste("shell(paste(\"wget http://vm7.target.rug.nl/xqtl_lifelines/downloadfile?name=\",inputname,\"_ped \",inputname,\".ped\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("shell(paste(\"wget http://vm7.target.rug.nl/xqtl_lifelines/downloadfile?name=\",inputname,\"_map \",inputname,\".map\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("shell(paste(\"plink --noweb --file \",inputname,\" --assoc --out \",outputname,\"\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("postForm('",paste(dbpath,"/uploadfile",sep=""),"',investigation_name='",investigationname,"', name='",outputname,"', type = 'InvestigationFile', file = fileUpload(filename='",outputname,".assoc'), style='HTTPPOST')","\n",sep="")),file=myanalysisfile,append=T)
}
