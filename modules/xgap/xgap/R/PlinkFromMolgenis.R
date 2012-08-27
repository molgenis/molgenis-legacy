#####################################################################
#
# PlinkFromMolgenis.R
#
# copyright (c) 2009, Danny Arends
# last modified Feb, 2012
# first written Feb, 2012
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License, as
# published by the Free Software Foundation; either version 2 of
# the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but without any warranty; without even the implied warranty of
# merchantability or fitness for a particular purpose. See the
# GNU General Public License for more details.
#
# A copy of the GNU General Public License is available at
# http://www.r-project.org/Licenses/
#
# Part of the R/qtl package
# Contains: PlinkFromMolgenis
#
######################################################################

######################################################################
#
# PlinkFromMolgenis:
#
######################################################################

#PlinkFromMolgenis <- function(DBmarker="", DBtrait="", verbose=TRUE){
PlinkFromMolgenis <- function(investigationname="",phenotypematrixname="", verbose=FALSE){
	#library("RCurl")
	#library("qtl")
	if(!("RCurl" %in% names( getLoadedDLLs()))){
		stop("Please install the package RCurl from bioconductor to use the molgenis interface\n")
	}
	
	DBmarkerID = NULL
	DBtraitID = NULL
	DBtraitID  = find.data(investigation_name=investigationname, name=phenotypematrixname,.verbose=verbose)$id
	if(is.na(DBtraitID&&1)){
		stop("Please provide valid names for the traits and/or genotypes\n")
	}else{
		if(verbose){
		cat("Downloading from\n")
		cat("Traitmatrix ID:",DBtraitID,"\n")
		}
	}
#get data from server
	trait_row <- find.data(id=DBtraitID,.verbose=verbose)["targettype"]
	trait_col <- find.data(id=DBtraitID,.verbose=verbose)["featuretype"]
#Checks
	if(trait_row != "Individual" && trait_col != "Individual"){
		stop("No Individuals found in DBtraitID")
	}
	
	if(verbose) cat(DBtraitID,"\n")
	trait_data <- downloadmatrixascsvCURL(DBtraitID)
	
	temp <- matrix(as.numeric(as.matrix(trait_data)),c(dim(trait_data)[1],dim(trait_data)[2]))
	temp[,1] <- trait_data[,1]
	rownames(temp) <- rownames(trait_data)
	colnames(temp) <- colnames(trait_data)
	trait_data <- temp
	
	if(trait_col == "Individual"){
		if(verbose) cat("INFO: Flipping traitset\n")
		trait_data <- t(trait_data)
	}
	if(verbose)cat("INFO: Number of individuals in Phenotype set:",dim(trait_data)[1],"\n")
  return(trait_data)
}
 
# end of PlinkFromMolgenis.R
