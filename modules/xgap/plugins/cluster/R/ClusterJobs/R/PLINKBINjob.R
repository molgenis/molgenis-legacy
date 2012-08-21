#####################################################################
#
# PLINKBINjob.R
#
# copyright (c) 2009-2012, Joeri van der Velde & Danny Arends
# last modified Apr, 2012
# first written Apr, 2012
# 
# Part of the ClusterJobs package
# Contains: run_PLINKBIN
#
######################################################################

######################################################################
#
# run_PLINKBIN: Download Plink binary data files, run simple association analysis, upload results.
#
######################################################################


run_PLINKBIN <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("inputname","hapmap1")), investigationname="", libraryloc=NULL){
  cat("info: Start by sending a message (so we know we're running)\n")
  cat("report(2,\"Starting\")\n",file=myanalysisfile,append=T)
  cat("info: Get your parameters\n")
  inputname <- getparameter("inputname",jobparams)
  
  invId <- find.investigation(name=investigationname)$id #needed since JPA merge
  
  cat(Generate_Statement(paste("system(paste(\"plink --noweb --bfile run",jobid,"/",inputname," --assoc --pheno run",jobid,"/phenotypes.txt --mpheno \",item,\" --out \",outname,item,\"\",sep=\"\"))\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"beforeUpload\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("postForm('",paste(dbpath,"/uploadfile",sep=""),"',investigation_name='",investigationname,"', investigation_id='",invId,"', name='",paste(outname,"_trait_",item,sep=""),".qassoc', type = 'InvestigationFile',curl=ch, file = fileUpload(filename='",paste(outname,item,sep=""),".qassoc'), style='HTTPPOST')","\n",sep="")),file=myanalysisfile,append=T)
  cat("report(3,\"PLINKBIN%20finished\")\n",file=myanalysisfile,append=T)
}
