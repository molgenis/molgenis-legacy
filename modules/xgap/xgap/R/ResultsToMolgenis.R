#####################################################################
#
# ResultsToMolgenis.R
#
# copyright (c) 2009, Danny Arends
# last modified Apr, 2009
# first written Oct, 2009
#
#     This program is free software; you can redistribute it and/or
#     modify it under the terms of the GNU General Public License, as
#     published by the Free Software Foundation; either version 2 of
#     the License, or (at your option) any later version. 
# 
#     This program is distributed in the hope that it will be useful,
#     but without any warranty; without even the implied warranty of
#     merchantability or fitness for a particular purpose.  See the
#     GNU General Public License for more details.
# 
#     A copy of the GNU General Public License is available at
#     http://www.r-project.org/Licenses/
#
# Part of the R/qtl package
# Contains: ResultsToMolgenis: Stores results from scanone scanMQM and scanALL into a molgenis database
#
######################################################################



PermToMolgenis <- function(investigationname,resultsname,results,Trait_num=0,number=nrow(permResults),verbose=TRUE){
	permResults <- results
	name <- resultsname
	library("RCurl")
	if(!("RCurl" %in% names( getLoadedDLLs()))){
		stop("Please install the package RCurl from bioconductor to use the molgenis interface\n")
	}
	#get data from server
	if(is.null(permResults)){
		stop("Please supply a perm result\n")
	}
	
	#Get all the markers
	markers <- find.marker(.verbose=verbose)
	if(verbose)cat("INFO: Found",dim(markers)[1],"markers in the current database\n")
	
	#Markers are inside molgenis, now we need to get the QTL's in
	matri <- find.data(name=name,.verbose=verbose)
  counter = 0;
  while(!dim(matri)[1] && counter < 5){
		if(!dim(matri)[1]){
      #No find, so we'll create one
      if(verbose) cat("INFO: No matrix named",name,"found in the current database\n")
      if(verbose) cat("INFO: Creating:",name,"in the current database\n")
      matri <- add.data(name = name,storage="Database",investigation_name=investigationname,targettype="Marker",featuretype="Trait",ontologyreference_name="qtl_matrix",valuetype="Decimal",.verbose=verbose)
    }else{
      if(verbose)cat("INFO: Matrix named",name,"found in the current database\n")
      matri <- find.data(name=name,.verbose=verbose)
    }
    counter = counter+1
  }

	colnam= "LOD_5_percent"
	res <- find.derived(name=colnam,.verbose=verbose)
  counter = 0;
  while(!dim(res)[1] && counter < 5){
    if(!dim(res)[1]){
      tryCatch(add.derived(name=colnam,investigation_name=investigationname),error=function(e){cat("Caught")})
    }else{
      res <- find.derived(name=colnam,.verbose=verbose)
    }
    counter = counter+1
  }
	colindex=0
	names <- rownames(permResults)
	start <- Trait_num
	stop <- start+nrow(permResults)-1
	rowindex=start:stop
	if(verbose)cat("INFO: ROW:",rowindex,"\n")  
	if(verbose)cat("INFO: names:",names,"\n")  
	values = permResults[,1]
	if(verbose)cat("INFO: Trying to upload ",colnam," to column:",colindex,"\n")  
	add.decimaldataelement(data_id=matri$id, feature_name=colnam, target_name=names, targetindex=rowindex, featureindex=colindex, value=values,.verbose=verbose)

	colnam= "LOD_10_percent"
	res <- find.derived(name=colnam,.verbose=verbose)
  counter = 0;
  while(!dim(res)[1] && counter < 5){
    if(!dim(res)[1]){
     tryCatch(add.derived(name=colnam,investigation_name=investigationname),error=function(e){cat("Caught")})
    }else{
      res <- find.derived(name=colnam,.verbose=verbose)
    }
    counter = counter+1
  }
	colindex=1
	#if(verbose)cat("INFO: ROW:",rowindex,"\n")
	#if(verbose)cat("INFO: names:",names,"\n")  
	values = permResults[,2]
	if(verbose)cat("INFO: Trying to upload ",colnam," to column:",colindex,"\n")  
	add.decimaldataelement(data_id=matri$id, feature_name=colnam, target_name=names, targetindex=rowindex, featureindex=colindex, value=values,.verbose=verbose)
	
}


ResultsToMolgenis <- function(investigationname = "", resultname = "", result=NULL,Trait_num=0,verbose=TRUE){
	intervalQTLmap <- result
	name <- resultname
	library("RCurl")
	if(!("RCurl" %in% names( getLoadedDLLs()))){
		stop("Please install the package RCurl from bioconductor to use the molgenis interface\n")
	}
	#get data from server
	if(is.null(intervalQTLmap)){
		stop("Please supply a QTL results file\n")
	}
	if(any(class(intervalQTLmap) == "scanone")){
		#cat("INFO: Valid object from scanone, containing 1 phenotype\n")
		num_pheno <- 1
		if(dim(intervalQTLmap)[1]>0){
	       intervalQTLmap <- list(intervalQTLmap)
	    }
	}
	if(any(class(intervalQTLmap) == "mqmmulti")){
		cat("INFO: Valid object from MultiQTL scan, containing ",length(intervalQTLmap)," phenotypes\n")
		num_pheno <- length(intervalQTLmap)
	}
	
	#Get all the markers
	markers <- find.marker(.verbose=verbose)
	if(verbose)cat("INFO: Found",dim(markers)[1],"markers in the current database\n")
	if(verbose)cat("INFO: Number of traits:",num_pheno,"\n")
	#if(verbose)cat("INFO: length:",length(intervalQTLmap),"should be above line\n")

	cnt <- 0
	for(j in 1:num_pheno){
		#cat("j value:",j,"\n")
		#cat(class(intervalQTLmap[[j]]),"\n")
		for(i in 1:dim(intervalQTLmap[[j]])[1]) {
			if(!rownames(intervalQTLmap[[j]])[i] %in% markers$name){
				add.marker(name=rownames(intervalQTLmap[[j]])[i],chr=intervalQTLmap[[j]][i,"chr"],cm=intervalQTLmap[[j]][i,"pos (Cm)"],investigation_name=investigationname)
				cnt=cnt+1
			}
		}
	}
	if(verbose)cat("INFO: Added",cnt,"markers to the current database\n")	
	#Markers are inside molgenis, now we need to get the QTL's in
	
	counter = 0;
	aaa <- find.data(name=name,.verbose=verbose)
	while(!dim(aaa)[1] && counter < 5){
		if(!dim(aaa)[1]){
		cat("NOT DIM AAA (NOT FOUND IN DB")
		#No find, so we'll create one
		if(verbose)cat("INFO: Not matrix named",name,"found in the current database\n")
		if(verbose)cat("INFO: Creating:",name,"in the current database\n")
		cat("BEFORE TRYCATCH")
		#removed: ontologyreference_name="qtl_matrix"
		tryCatch(aaa <- add.data(name = name,storage="Database",investigation_name=investigationname,targettype="Marker",featuretype="DerivedTrait",valuetype="Decimal",.verbose=verbose), error=function(e){cat("Upload failed.")})
		cat("AFTER TRYCATCH")
		}else{
		   cat("FOUND")
			if(verbose)cat("INFO: Matrix named",name,"found in the current database\n")
			cat("Matrix named ",name," found in the current database\n")
		}
		cat(paste("Counter:",counter))
		Sys.sleep(3)
		aaa <- find.data(name=name,.verbose=verbose)
		counter = counter +1
	}
	for(j in 1:num_pheno){
		colnam <- colnames(intervalQTLmap[[j]])[3]
		colnam <- substr(colnam,5,nchar(colnames(intervalQTLmap[[j]])[3]))
		if(colnam ==""){
			colnam = paste("unknown",j,sep="")
		}
		names <- NULL
		values <- NULL
		rowindex <- NULL
		colindex <- (j+Trait_num)-1
		for(i in 1:dim(intervalQTLmap[[j]])[1]) {
			names <- c(names,rownames(intervalQTLmap[[j]])[i])
			values <- c(values,intervalQTLmap[[j]][i,3])
			rowindex <- c(rowindex,i-1)
		}
		if(verbose)cat("INFO: Trying to upload a trait",j,":",colnam,"to column:",colindex,"\n")  
		tryCatch(add.decimaldataelement(investigation_id=aaa$investigation, data_id=aaa$id, feature_name=colnam, target_name=names, targetindex=rowindex, featureindex=colindex, value=values,.verbose=verbose), error=function(e){cat(e[[1]]);stop("Upload failed.", e[[1]])})
	}
}


scanone <- function(cross,pheno.col=1,...){
	res<- qtl::scanone(cross=cross,pheno.col=pheno.col,...)
	colnames(res)[3] <- paste("lod",names(cross$pheno)[pheno.col])
	res
}

#scanall <- function(cross,upload=FALSE,...){
#	npheno <- length(names(cross$pheno))
#	allres <- vector(mode = "list", length = npheno)
#	for(x in 1:npheno){
#		tempresult <- scanone(cross,pheno.col=x,...)
#		if(upload) ResultsToMolgenis(tempresult)
#		allres[[x]] <- tempresult
#	}
#	class(allres) <- c(class(allres),"mqmmulti")
#	allres
#}

# end of ResultsToMolgenis.R
