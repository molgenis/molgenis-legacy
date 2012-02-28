#####################################################################
#
# PLINKjob.R
#
# copyright (c) 2009-2011, Danny Arends
# last modified Sep, 2011
# first written Sep, 2011
# 
# Part of the ClusterJobs package
# Contains: run_PLINK
#
######################################################################

######################################################################
#
# run_PLINK: Download Plink data files, run simple association analysis, upload results.
#
######################################################################


run_PLINK <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("inputname","hapmap1")), investigationname="", libraryloc=NULL){
  cat("info: Start by sending a message (so we know we're running)\n")
  cat("report(2,\"Starting\")\n",file=myanalysisfile,append=T)
  cat("info: Get your parameters\n")
  inputname <- getparameter("inputname",jobparams)
  
  cat(Generate_Statement(paste("system(paste(\"wget ",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_ped -O ",inputname,".ped\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"PEDDownloaded\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("system(paste(\"wget ",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_map -O ",inputname,".map\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"MAPDownloaded\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("system(paste(\"plink --noweb --file ",inputname," --assoc --pheno phenotypes.txt --mpheno ",item," --out \",outname",item,",\"\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"beforeUpload\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("postForm('",paste(dbpath,"/uploadfile",sep=""),"',Investigation_name='",investigationname,"', name='",paste(outname,item,sep=""),".assoc', type = 'InvestigationFile', file = fileUpload(filename='",outname,".assoc'), style='HTTPPOST')","\n",sep="")),file=myanalysisfile,append=T)
  cat("report(3,\"PLINKfinished\")\n",file=myanalysisfile,append=T)
}
