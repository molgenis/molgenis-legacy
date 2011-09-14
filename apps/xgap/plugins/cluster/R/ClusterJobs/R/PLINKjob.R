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


run_PLINK <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("inputname","hapmap1")), investigationname="", libraryloc=NULL){
  cat("info: Start by sending a message (so we know we're running)\n")
  
  cat("info: Get your parameters\n")
  inputname <- getparameter("inputname",jobparams)
  
  cat(Generate_Statement(paste("system(paste(\"wget ",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_ped -O ",inputname,".ped\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"PEDDownloaded\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("system(paste(\"wget ",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_map -O ",inputname,".map\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"MAPDownloaded\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("system(paste(\"plink --noweb --file ",inputname," --assoc --out \",outname,\"\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"beforeUpload\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("postForm('",paste(dbpath,"/uploadfile",sep=""),"',investigation_name='",investigationname,"', name='",outname,".assoc', type = 'InvestigationFile', file = fileUpload(filename='",outname,".assoc'), style='HTTPPOST')","\n",sep="")),file=myanalysisfile,append=T)
  cat("report(3,\"PLINKfinished\")\n",file=myanalysisfile,append=T)
}
