#####################################################################
#
# common.R
#
# copyright (c) 2009, Danny Arends
# last modified Fep, 2009
# first written Nov, 2009
# 
# Common functions of the ClusterJobs package
# Contains: run_cluster
#
######################################################################

######################################################################
#
# generateRunfile: Generates a .sh file with job description to be submitted to pbs
# generateQTLfile: Generates a QTL file which is executed (R CMD BATCH <qtlfile>) by a cluster
# DownloadnSave: Saves a Crossobject to the HDD in .RData format for easy loading
# run_cluster: main routine to distribute jobs across a cluster of computers
# generateRunfile: Generates a .sh file with job description to be submitted to pbs
# generateQTLfile: Generates a QTL file which is executed (R CMD BATCH <qtlfile>) by a cluster
# generateESTfile: Generates a QTL file to estimate runtime for each item
# mqmmultitomatrix: Generates a mnatrix, tht can be uploaded by using the add.datamatrix function
#
######################################################################

mqmmultitomatrix <- function(mqmmulti){
  matr <- NULL
  coln <- NULL
  for(x in 1:length(mqmmulti)){
    matr <- cbind(matr,mqmmulti[[x]][,3])
    coln <- c(coln, substr(colnames(r[[x]])[3],5,nchar(colnames(r[[x]])[3])))
  }
  rownames(matr) <- rownames(mqmmulti[[1]])
  colnames(matr) <- coln
  matr
}

generateRunfile <- function(job, est,jobid,libraryloc=NULL){
	#Generate a runfile to submit to the cluster
	runfile <- paste("./run",jobid,"/run",job,".sh",sep="")
	cat("#!/bin/sh","\n",sep="",file=runfile)
	#We need just 1 node TODO: use 2 processors and SNOW on a clusterNODE
	cat("#PBS -l nodes=1","\n",sep="",file=runfile,append=T)
	#Our estimate
	cat("#PBS -l walltime=",est,"\n",sep="",file=runfile,append=T)
	#Go to the correct location
	cat(paste("cd $HOME/run",jobid,sep=""),"\n",sep="",file=runfile,append=T)
	#StartRunning
	cat("R CMD BATCH subjob",job,".R",sep="",file=runfile,append=T)
	runfile
}

DownloadnSave <- function(investigationname, token, DBmarkerID = "", DBtraitID = "", dbpath = "",jobid,njobs,libraryloc=NULL){
	#Generates a R-script to download all the information and build a cross object
	qtlfile <- paste("./run",jobid,"/download.R",sep="")
	#load needed libraries
	cat("library(qtl,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile)
	cat("library(bitops,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
	cat("library(RCurl,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
	cat("msource <- function(murl = 'http://127.0.0.1:8080/xqtl/api/R/', verbose = TRUE){","\n",sep="",file=qtlfile,append=T)
		cat("data <- getURLContent(murl)","\n",sep="",file=qtlfile,append=T)
		cat("t <- tempfile()","\n",sep="",file=qtlfile,append=T)
		cat("writeLines(data, con=t)","\n",sep="",file=qtlfile,append=T)
		cat("sys.source(t,globalenv())","\n",sep="",file=qtlfile,append=T)
		cat("unlink(t)","\n",sep="",file=qtlfile,append=T)
		cat("}","\n",sep="",file=qtlfile,append=T)
    cat("msource(\"",paste(dbpath,"/api/R",sep=""),"\")\n",sep="",file=qtlfile,append=T)
	cat("MOLGENIS.login('",token,"')\n",sep="",file=qtlfile,append=T)
	#Print our report function
	cat("\nreport <- function(status,text){\n",file=qtlfile,append=T)
	cat("\ttask <- ",jobid,"\n",file=qtlfile,append=T)
	cat("\ttext <- substr(URLencode(text),0,100)\n",file=qtlfile,append=T)
	cat("\tlink <- paste(\"",dbpath,"/taskreporter?job=\",task,\"&subjob=0&statuscode=\",status,\"&statustext=\",text,sep=\"\")\n",sep="",file=qtlfile,append=T)
	cat("\tgetURL(link, curl = .MOLGENIS.curlHandle)\n",file=qtlfile,append=T)
	cat("\tif(status==-1){\n\t\tcat(\"!!!\",text,\"!!!\")\n\t\t\n\t\tq(\"no\")\n\t}\n",file=qtlfile,append=T)
	cat("}\n\n",file=qtlfile,append=T)
	#Downloading of Cross object (secured)
	cat(Generate_Statement(paste("cross <- CrossFromMolgenis(genotypematrixname='",DBmarkerID,"',phenotypematrixname='",DBtraitID,"',investigationname='",investigationname,"')","\n",sep="")),file=qtlfile,append=T)
	cat(Generate_Statement(paste("save(cross,file=\"./run",jobid,"/cross.RData\")","\n",sep="")),file=qtlfile,append=T)
	cat(Generate_Statement(paste("dir.create(\"./run",jobid,"/run",jobid,"/\")","\n",sep="")),file=qtlfile,append=T)	
	cat(Generate_Statement(paste("save(cross,file=\"./run",jobid,"/run",jobid,"/cross.RData\")","\n",sep="")),file=qtlfile,append=T)
	cat("q(\"no\")","\n",sep="",file=qtlfile,append=T)
}

DownloadnSavePLINK <- function(investigationname, token, DBtraitID = "", inputname, dbpath = "",jobid,njobs,libraryloc=NULL){
	#Generates a R-script to download all the information and build a cross object
	qtlfile <- paste("./run",jobid,"/download.R",sep="")
	#load needed libraries
	cat("library(bitops,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile)
	cat("library(RCurl,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
    cat("msource(\"",paste(dbpath,"/api/R",sep=""),"\")\n",sep="",file=qtlfile,append=T)
	cat("MOLGENIS.login('",token,"')\n",sep="",file=qtlfile,append=T)
	#Print our report function
	cat("\nreport <- function(status,t=\"\"){\n",file=qtlfile,append=T)
	cat("\ttask <- ",jobid,"\n",file=qtlfile,append=T)
	cat("\ttext <- URLencode(substring(t,1,100))\n",file=qtlfile,append=T)
	cat("\tlink <- paste(\"",dbpath,"/taskreporter?job=\",task,\"&subjob=0&statuscode=\",status,\"&statustext=\",text,sep=\"\")\n",sep="",file=qtlfile,append=T)
	cat("\tgetURL(link, curl = .MOLGENIS.curlHandle)\n",file=qtlfile,append=T)
	cat("\tif(status==-1){\n\t\tcat(\"!!!\",text,\"!!!\")\n\t\t\n\t\tq(\"no\")\n\t}\n",file=qtlfile,append=T)
	cat("}\n\n",file=qtlfile,append=T)
	#Downloading of Cross object (secured)
	cat(Generate_Statement(paste("plink <- PlinkFromMolgenis(phenotypematrixname='",DBtraitID,"',investigationname='",investigationname,"')","\n",sep="")),file=qtlfile,append=T)
	cat(Generate_Statement(paste("write.table(plink,file=\"./run",jobid,"/phenotypes.txt\",quote=FALSE,row.names=FALSE)","\n",sep="")),file=qtlfile,append=T)
	cat("report(2,\"PhenotypesDownloaded\")\n",file=qtlfile,append=T)
  
 #plink, ped/map format (fails for PLINKBIN analysis, but doesn't matter)
  cat(Generate_Statement(paste("downloadFileViaCurl('",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_ped','run",jobid,"/",inputname,".ped')\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"PEDDownloaded\")\n",file=qtlfile,append=T)
  cat(Generate_Statement(paste("downloadFileViaCurl('",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_map','run",jobid,"/",inputname,".map')\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"MAPDownloaded\")\n",file=qtlfile,append=T)
  
  #plink, binary format (fails for PLINK analysis, but doesn't matter)
  cat(Generate_Statement(paste("downloadFileViaCurl('",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_bed','run",jobid,"/",inputname,".bed')\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"BEDDownloaded\")\n",file=qtlfile,append=T)
  cat(Generate_Statement(paste("downloadFileViaCurl('",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_bim','run",jobid,"/",inputname,".bim')\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"BIMDownloaded\")\n",file=qtlfile,append=T)
  cat(Generate_Statement(paste("downloadFileViaCurl('",paste(dbpath,"/downloadfile",sep=""),"?name=",inputname,"_fam','run",jobid,"/",inputname,".fam')\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"FAMDownloaded\")\n",file=qtlfile,append=T)
  
  cat("q(\"no\")","\n",sep="",file=qtlfile,append=T)
}

getparameter <- function(searchterm,jobparams){unlist(lapply(jobparams,function(x){if(x[1]==searchterm){x[2]}}))}


tryloadlibs <- function(lib="qtl", libraryloc, qtlfile){
  cat("error <- FALSE\n",sep="",file=qtlfile,append=TRUE)
  cat("tryCatch({","\n",sep="",file=qtlfile,append=TRUE)
  cat("library(",lib,",lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=TRUE)
  cat("}, error = function(ex) { error <<- TRUE; cat('unable to load from lib.loc\\n')})\n",sep="",file=qtlfile,append=TRUE)
  cat("if(error){\n",sep="",file=qtlfile,append=TRUE)
  cat("error <- FALSE\n",sep="",file=qtlfile,append=TRUE)
  cat("tryCatch({","\n",sep="",file=qtlfile,append=TRUE)
  cat("library(",lib,")","\n",sep="",file=qtlfile,append=TRUE)
  cat("}, error = function(ex) { report(-1,'unable to load library')})\n",sep="",file=qtlfile,append=TRUE)
  cat("}\n",sep="",file=qtlfile,append=TRUE)
}

startcode <- function(token, dbpath,jobid,item,libraryloc=NULL,name="subjob"){
	cat("Debug: StartCode function entered\n")
	qtlfile <- paste("./run",jobid,"/",name,item,".R",sep="")
	cat("Debug: FILE=",qtlfile,"\n")
#	load needed libraries
	cat("library(qtl,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile)
	cat("library(bitops,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
	cat("library(RCurl,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
#	no longer needed: cat("library(ClusterJobs,lib.loc='",libraryloc,"')","\n",sep="",file=qtlfile,append=T)
	cat("msource <- function(murl = 'http://127.0.0.1:8080/xqtl/api/R/', verbose = TRUE){","\n",sep="",file=qtlfile,append=T)
		cat("data <- getURLContent(murl)","\n",sep="",file=qtlfile,append=T)
		cat("t <- tempfile()","\n",sep="",file=qtlfile,append=T)
		cat("writeLines(data, con=t)","\n",sep="",file=qtlfile,append=T)
		cat("sys.source(t,globalenv())","\n",sep="",file=qtlfile,append=T)
		cat("unlink(t)","\n",sep="",file=qtlfile,append=T)
		cat("}","\n",sep="",file=qtlfile,append=T)
    cat("msource(\"",paste(dbpath,"/api/R",sep=""),"\")\n",sep="",file=qtlfile,append=T)
	cat("MOLGENIS.login('",token,"')\n",sep="",file=qtlfile,append=T)
#	Print our report function
	cat("\nreport <- function(status,text){\n",file=qtlfile,append=T)
	cat("\ttask <- ",jobid,"\n",file=qtlfile,append=T)
	cat("\ttext <- substr(URLencode(text),0,100)\n",file=qtlfile,append=T)
	if(item==""){
		cat("\tjob <- 0\n",file=qtlfile,append=T)
	}else{
		cat("\tjob <- ",item,"\n",file=qtlfile,append=T)
	}
	cat("\tlink <- paste(\"",dbpath,"/taskreporter?job=\",task,\"&subjob=\",job,\"&statuscode=\",status,\"&statustext=\",text,sep=\"\")\n",sep="",file=qtlfile,append=T)
	cat("\tgetURL(link, curl = .MOLGENIS.curlHandle)\n",file=qtlfile,append=T)
	cat("\tif(status==-1){\n\t\tcat(\"!!!\",text,\"!!!\")\n\t\tq(\"no\")\n\t}\n",file=qtlfile,append=T)
	cat("}\n\n",file=qtlfile,append=T)
	qtlfile
}

Generate_Statement <- function(statement){
	#printing a secured statement (just means we report a 3 if we fail)
	secured <- paste("tryCatch(\n\t",statement,"\t,error =  function(e){cat(\"!!!!!!\",e[[1]],\"\n\"); report(-1,e$message)}\n)\n",sep="")
	secured
}

prepare_cluster <- function(jobid){
	cat("Creating directory\n")
	#Create a directory and switch to it (in R and on the shell (so our next R's we'll spawn will not have 2 switch dirs)
	dir.create(paste("run",jobid,sep=""))
	#system(paste("cd run",jobid,sep=""))
	#setwd(paste("./run",jobid,sep=""))
}

report <- function(dbpath,task,job,status,t = ""){
	progress <- 0
	text <- URLencode(substr(t,1,100))
	link <- paste(dbpath,"/taskreporter?job=",task,"&subjob=",job,"&statuscode=",status,"&statustext=",text,"&statusprogress=",progress,sep="")
	getURL(link,curl = .MOLGENIS.curlHandle)
	if(status==-1){
		q("no")
	}
}

run_cluster_new_new <- function(name="test", investigation="ClusterDemo", token = "", totalitems=24, njobs=2, dbpath=app_location, jobid=1, job="QTL", libraryloc="C:/Program Files/R/R-2.10.1/library", jobparams=list( c("genotypes", "genotypes"), c("phenotypes", "metaboliteexpression"),c("map","scanone"),c("method","hk"),c("model","normal"),c("stepsize","0"))){
#	Initializes the cluster
#	Downloads the cross datafile from molgenis and save it as a .RData
#	Estimated time needed for each run (very crude) (totalitems in run + 25%)
#	--Generate a QTLfile
#	--Generate runfile for cluster
#	--Sends the runfile as a job to the cluster
  report(dbpath,jobid,0,2,"TETSING1")
	genotypes <- getparameter("genotypes",jobparams)
	phenotypes <- getparameter("phenotypes",jobparams)
	totalitems <- as.integer(totalitems)
  cat(genotypes," ",phenotypes,"\n")
	njobs <- as.integer(njobs)
	jobid <- as.integer(jobid)	
	prepare_cluster(jobid)
	cat("debug: Cluster prepared\n")
	library(bitops,lib.loc=libraryloc)
	cat("debug: bitops loaded\n")
	library(RCurl,lib.loc=libraryloc)
	cat("debug: RCurl loaded\n")
	library(qtl,lib.loc=libraryloc)
	cat("debug: qtl loaded\n")
	#msource(paste(dbpath,"/api/R",sep=""))
	report(dbpath, jobid, 0, 2,"R%20libraries%20loaded")
	est = NULL
	runfile = NULL
	if(totalitems < njobs){
		njobs = totalitems
	}
	nprun <- ceiling(totalitems/njobs)
	cat("# of Traits:",totalitems,"\n# of Jobs",njobs,"# per run",nprun,"\n")
  doDownload <- FALSE
  if(job=="QTL"){
    tryCatch(DownloadnSave(investigationname=investigation, token, genotypes, phenotypes, dbpath=dbpath,jobid,njobs,libraryloc)
      ,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,0,-1,"Downloadscript")}
    )
    cat("debug: R/qtl Downloadfile generated\n")
    doDownload <- TRUE
  }
  if(job=="PLINK" || job=="PLINKBIN"){
    tryCatch(DownloadnSavePLINK(investigationname=investigation, token, phenotypes, getparameter("inputname",jobparams), dbpath=dbpath,jobid,njobs,libraryloc)
      ,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,0,-1,"Downloadscript")}
    )
    totalitems <- (totalitems-2)
    doDownload <- TRUE
    cat("debug: PLINK Downloadfile generated\n")
  }
  if(doDownload){
    report(dbpath,jobid,0,2,"GeneratedDownload")
    tryCatch(system(paste("R CMD BATCH ./run",jobid,"/download.R",sep=""))
      ,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,0,-1,"DownloadingCrossobject")}
    )
    cat("Debug: Finished downloading datasets\n")
    report(dbpath,jobid,0,2,"FinishedDownloadingDatasets")
  }
	tryCatch(est <- est_runtime_new_new(token, njobs, totalitems, dbpath=dbpath, nprun, jobid, job=job, investigation, jobparams=jobparams, libraryloc)
		,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,0,-1,"EstimatingTime")}
	)
	cat("debug: EST_runtime finished downloading datasets\n")
  if(is.null(name)) name="qtlTEST"
	cat("Estimated for ",name,". Runtime per job=",est,"\n")
	report(dbpath,jobid,0,2,"EstimatedRuntime")
#	ALL DONE NOW WE CAN GO INTO/run directory and make some calculations
	for(x in 1:njobs){
		cat("Generating: ",x,".1/",njobs,"\n",sep="")
    if(njobs > 1){
      todo <- which(as.numeric(cut(1:totalitems,njobs))==x)
    }else{
      todo <- 1:totalitems
    }
    tryCatch(myanalysisfile <- startcode(token, dbpath,jobid,x,libraryloc)
        ,error = function(e){cat(e[[1]],"\n");report(dbpath,jobid,x,-1,"StartCodeGeneration")}
    )
  	cat("jobid <- ",jobid,"\n",file=myanalysisfile,append=T)
  	cat("dbpath <- \"", dbpath ,"\"\n",sep="",file=myanalysisfile,append=T)
  	cat("outname <- \"",name,"\"\n",sep="",file=myanalysisfile,append=T)
  	cat("investigationname <- \"",investigation,"\"\n",sep="",file=myanalysisfile,append=T)
  	cat("jobparams <- list(",file=myanalysisfile,append=T)
  	first <- TRUE 
  	for(y in jobparams){
    	if(!first)cat(",",sep="",file=myanalysisfile,append=T)
    	cat("c(\"",y[1],"\",\"",y[2],"\")",sep="",file=myanalysisfile,append=T)
    	first <- FALSE
  	}
  	cat(")","\n",file=myanalysisfile,append=T)
    report(dbpath,jobid,0,2,paste("Generating_QTL_",min(todo),"_",max(todo),sep=""))
    for(item in todo){
      cat("subjob <- ",x,"\n",file=myanalysisfile,append=T)
      cat("item <- ",item,"\n",file=myanalysisfile,append=T)
      tryCatch(do.call(paste("run_",job,sep=""),list(dbpath=dbpath, subjob=x, item=item, jobid=jobid, outname=name, myanalysisfile=myanalysisfile, jobparams=jobparams, investigationname=investigation, libraryloc=libraryloc))
          ,error = function(e){cat(e[[1]],"\n");report(dbpath,jobid,x,-1,"GeneratingQTLrunfile")}
      )
    }
    report(dbpath,jobid,0,2,paste("GonnaDoCleanupOfScript",x,sep=""))	
    cat("q(\"no\")","\n",sep="",file=myanalysisfile,append=T)
		report(dbpath,jobid,0,2,paste("Generated_QTL",x,sep=""))	
		cat("Generating: ",x,".2/",njobs,"\n",sep="")
		tryCatch(runfile <- generateRunfile(x,est,jobid,libraryloc)
			,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,x,-1,"GeneratingSHrunfile")}
		)
		report(dbpath,jobid,0,2,paste("Generated_SH",x,sep=""))	
		cat("Submitting: ",x,".3/",njobs,":",runfile,"\n",sep="")
#		OLD call to sh to compute on the Sceduler
		tryCatch(system(paste("qsub ",runfile,sep=""))
			,error =  function(e){cat(e[[1]],"\n");report(dbpath,jobid,x,-1,"SubmissionPBS:")}
		)
		report(dbpath,jobid,0,2,paste("Submitted_",x,sep=""))	
		report(dbpath,jobid,x,1,"Taskqueued")
#		system(paste("sh ",runfile,sep=""))
	}
	report(dbpath,jobid,0,3,"Preparation done")
}

#time execution of executing 1 trait
est_runtime_new_new <- function(token, njobs=1, ntraits=1, dbpath = "", num_per_run=1, jobid=1, job, investigation, jobparams=list(c("map","scanall"),c("method","hk"),c("model","normal")), libraryloc="~/libs"){
  cat("Debug: Gonna call user function for est time: ",paste("est_",job,sep="")," \n")
  s <- proc.time()
  cat("Debug:  est time: ",dbpath," \n")
  cat("Debug:  est time: ",jobid," \n")
  cat("Debug:  est time: ",libraryloc," \n")
  report(dbpath,jobid,0,2,"Userfunctionexecuting")
  #Crude estimation of time that it would take a job of num_per_run qtls to finish, we get all the data from molgenis and run 2 qtls profiles
  myanalysisfile <- startcode(token, dbpath,jobid,"",libraryloc,"ESTtime")
  cat("subjob <- ",0,"\n",file=myanalysisfile,append=T)
  cat("jobid <- ",jobid,"\n",file=myanalysisfile,append=T)
  cat("dbpath <- \"",dbpath,"\"\n",sep="",file=myanalysisfile,append=T)
  cat("item <- ",1,"\n",file=myanalysisfile,append=T)
  cat("outname <- ","\"something\"","\n",file=myanalysisfile,append=T)
  cat("investigationname <- \"",investigation,"\"\n",sep="",file=myanalysisfile,append=T)
  cat("jobparams <- list(",file=myanalysisfile,append=T)
  first <- TRUE 
  for(x in jobparams){
    if(!first)cat(",",sep="",file=myanalysisfile,append=T)
    cat("c(\"",x[1],"\",\"",x[2],"\")",sep="",file=myanalysisfile,append=T)
    first <- FALSE
  }
  cat(")","\n",file=myanalysisfile,append=T)
  tryCatch(do.call(paste("run_",job,sep=""),list(dbpath=dbpath, subjob=0, item=1, jobid=jobid, outname="something", myanalysisfile=myanalysisfile, jobparams=jobparams, investigationname=investigation, libraryloc=libraryloc))
      ,error = function(e){cat(e[[1]],"\n");report(dbpath,jobid,x,-1,"GeneratingESTTIMEfile")}
  )
	system(paste("R CMD BATCH ./run",jobid,"/ESTtime.R",sep=""))
	e <- proc.time()
	#Add some for security
	EST <- ((num_per_run)*((e[3]-s[3])+60))*2
  cat("!!!" , (e[3]-s[3]), ((num_per_run)*((e[3]-s[3])*1.5)),"\n")
	ESTtime <- sprintf("%02.f:%02.f:%02.f",EST %/% 3600, (EST%%3600) %/% 60, round(EST%%60, digits = 0))
	if(EST >  864000){
		report(dbpath,jobid,0,-1,"JobsTooLong")
	}
  ESTtime
}
