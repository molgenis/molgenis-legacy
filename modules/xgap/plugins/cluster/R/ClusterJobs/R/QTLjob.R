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


run_QTL <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("map","scanone"),c("method","hk"),c("model","normal"),c("stepsize","0")), investigationname="", libraryloc=NULL){
  cat("info: Start by sending a message (so we know we're running)\n")
  cat("report(2,\"LoadingCrossobject\")\n",file=myanalysisfile,append=T)
  
  cat("info: Get your parameters\n")
  map <- getparameter("map",jobparams)
  method <- getparameter("method",jobparams)
  model <- getparameter("model",jobparams)
  stepsize <- getparameter("stepsize",jobparams)
  
  cat("info: Loading cross object\n")
  cat(Generate_Statement(paste("load(\"./run",jobid,"/cross.RData\")","\n",sep="")),file=myanalysisfile,append=T)
  cat("report(2,\"FinishedLoading\")\n",file=myanalysisfile,append=T)
  if(stepsize!=0){
    cat(Generate_Statement(paste("cross <- calc.genoprob(cross, step=",stepsize,")","\n",sep="")),file=myanalysisfile,append=T)
  }
  cat("report(2,\"FinishedMarkerImputation\")\n",file=myanalysisfile,append=T)
  cat(Generate_Statement(paste("results <- ",map,"(cross,pheno.col=",item,",method='",method,"',model='",model,"',verbose=TRUE)","\n",sep="")),file=myanalysisfile,append=T)
#  cat("report(2,\"Gonna do QTL plot\")\n",file=myanalysisfile,append=T)
#  imagefilename <- paste("P",jobid,"_",item,".fig",sep="")
#  cat(Generate_Statement(paste("xfig(file = '",imagefilename,"')","\n",sep="")),file=myanalysisfile,append=T)
#  cat(Generate_Statement(paste("plot(results)","\n",sep="")),file=myanalysisfile,append=T)
#  cat(paste("dev.off()","\n",sep=""),file=myanalysisfile,append=T)
#  cat("report(2,\"PlotInTemp.fig\")\n",file=myanalysisfile,append=T)
#  invId <- find.investigation(name=investigationname)$id #needed since JPA merge
#  cat(Generate_Statement(paste("MOLGENIS.upload('",paste(dbpath,"/uploadfile",sep=""),"',Investigation_name='",investigationname,"', investigation_id='",invId,"', name='",imagefilename,"', type = 'InvestigationFile', filename='",imagefilename,"')","\n",sep="")),file=myanalysisfile,append=T)
#  cat("report(2,\"UploadedFIGtoDatabase\")\n",file=myanalysisfile,append=T)
  cat("report(2,\"StoringQTLResults\")\n",file=myanalysisfile,append=T)
  cat("info: Store QTL results by using ResultsToMolgenis function or any other function provided in the molgenis R API\n")
  cat(paste("ResultsToMolgenis(result=results,resultname='",outname,"',Trait_num=",(item-1),",investigationname=\"",investigationname,"\")","\n",sep=""),file=myanalysisfile,append=T)
  cat("report(3,\"Job%20Finished\")\n",file=myanalysisfile,append=T)
}
