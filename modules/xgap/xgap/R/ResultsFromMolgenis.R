#####################################################################
#
# ResultsFromMolgenis.R
#
# copyright (c) 2009, Danny Arends
# last modified Mrt, 2009
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
# Contains: ResultsFromMolgenis
#
######################################################################

######################################################################
#
# ResultsFromMolgenis:
#
######################################################################

ResultsFromMolgenis <- function(investigationname=NULL, resultname=NULL,verbose=TRUE){
	library("RCurl")
	library("qtl")
	if(!("RCurl" %in% names( getLoadedDLLs()))){
		stop("Please install the package RCurl from bioconductor to use the molgenis interface\n")
	}
	#get data from server
	investigation_id = NULL
	investigation_id = find.investigation(name=investigationname)$id
	resultmatrixid = find.data(investigation_id=investigation_id, name=resultname)$id
	if(is.na(investigation_id&&1)){
		stop("Please supply a valid investigation name\n")
	}
	if(is.na(resultmatrixid&&1)){
		stop("Please supply a valid resultname\n")
	}else{
		returnObj <- NULL
		tempObj <- NULL
		m_data_url <- paste(app_location,"/downloadmatrixascsv?id=",resultmatrixid,"&download=all",sep="")
		cat(m_data_url,"\n")
		data <- read.table(m_data_url,sep="\t",header=T,row.names=1,check.names=FALSE)
		marker_info <- find.marker(name=rownames(data), .verbose=verbose)
		matchV <- match(rownames(data),marker_info$name)
		marker_info <- marker_info[matchV,]
		
		if(dim(data)[2] == 1){
			#We need to give back a single QTL scanone object
			returnObj <- data.frame(marker_info$chr,as.numeric(marker_info$cm),as.numeric(data[,1]))
			colnames(returnObj) <- c("chr","pos","lod")
			rownames(returnObj) <- marker_info$name
			returnObj <- returnObj[order(returnObj[,"chr"],returnObj[,"pos"]),]
			class(returnObj) <- c("scanone",class(returnObj))
		}else{	
			for(i in 1:dim(data)[2]) {
				tempObj <- data.frame(marker_info$chr,as.numeric(marker_info$cm),as.numeric(data[,i]))
				colnames(tempObj) <- c("chr","pos","lod")
				rownames(tempObj) <- marker_info$name
				tempObj <- tempObj[order(tempObj[,"chr"],tempObj[,"pos"]),]
				class(tempObj) <- c("scanone",class(tempObj))
				returnObj[[i]] <- tempObj
			}
			class(returnObj) <- c(class(returnObj),"MQMmulti")
		}
		returnObj
	}
}

# end of ResultsFromMolgenis.R
