#####################################################################
#
# CrossFromMolgenis.R
#
# copyright (c) 2009, Danny Arends
# last modified Oct, 2009
# first written Oct, 2009
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
# Contains: CrossFromMolgenis
#
######################################################################

######################################################################
#
# CrossFromMolgenis:
#
######################################################################

#CrossFromMolgenis <- function(DBmarker="", DBtrait="", verbose=TRUE){
CrossFromMolgenis <- function(investigationname="",genotypematrixname=NULL,phenotypematrixname="", verbose=FALSE){
	library("RCurl")
	library("qtl")
	if(!("RCurl" %in% names( getLoadedDLLs()))){
		stop("Please install the package RCurl from bioconductor to use the molgenis interface\n")
	}
	
	DBmarkerID = NULL
	DBtraitID = NULL
	DBmarkerID = find.data(investigation_name=investigationname, name=genotypematrixname,.verbose=verbose)$id
	DBtraitID  = find.data(investigation_name=investigationname, name=phenotypematrixname,.verbose=verbose)$id
	if(is.na(DBmarkerID&&1) || is.na(DBtraitID&&1)){
		stop("Please provide valid names for the traits and/or genotypes\n")
	}else{
		if(verbose){
		cat("Downloading from\n")
		cat("Markermatrix ID:",DBmarkerID,"\n")
		cat("Traitmatrix ID:",DBtraitID,"\n")
		}
	}
#get data from server
	marker_row <- find.data(id=DBmarkerID,.verbose=verbose)["targettype"]
	marker_col <- find.data(id=DBmarkerID,.verbose=verbose)["featuretype"]
	trait_row <- find.data(id=DBtraitID,.verbose=verbose)["targettype"]
	trait_col <- find.data(id=DBtraitID,.verbose=verbose)["featuretype"]
#Checks
	if(trait_row != "Individual" && trait_col != "Individual"){
		stop("No Individuals found in DBtraitID")
	}
	if(marker_row != "Marker" && marker_col != "Marker"){
		stop("No markers found in DBmarkerID")
	}
	
	if(verbose) cat(DBmarkerID,"\n")
	marker_data <- downloadmatrixascsvCURL(DBmarkerID)
	if(verbose) cat(DBtraitID,"\n")
	trait_data <- downloadmatrixascsvCURL(DBtraitID)
	
	temp <- matrix(as.numeric(as.matrix(trait_data)),c(dim(trait_data)[1],dim(trait_data)[2]))
	rownames(temp) <- rownames(trait_data)
	colnames(temp) <- colnames(trait_data)
	trait_data <- temp
	marker_info <- find.marker(investigation_name=investigationname,.verbose=verbose)
	
	if(marker_row != "Marker"){
		if(verbose) cat("INFO: Flipping markerset\n")
		marker_data <- t(marker_data)
	}
	
	if(trait_col != "Individual"){
		if(verbose) cat("INFO: Flipping traitset\n")
		trait_data <- t(trait_data)
	}
	if(verbose)cat("INFO: Number of individuals in Marker set:",dim(marker_data)[2],"\n")
	if(verbose)cat("INFO: Number of individuals in Phenotype set:",dim(trait_data)[2],"\n")
	prob_cross = find.ontologyterm(name=find.individual(name=colnames(marker_data)[1],.verbose=verbose)$ontologyreference_name)$definition
	if(prob_cross=='bc'||prob_cross=='riself'||prob_cross=='f2'){
		cat("CrossType:",prob_cross,"supported (bc, riself, f2)\n")
	}else{
		stop("CrossType unsupported")
	}
	
	#We assume that if we have IND in markers = IND in trait that individuals match
	if(dim(marker_data)[2] > dim(trait_data)[2]){
		if(verbose)cat("INFO: Scaling down the markerset\n")
		matchV <- na.omit(match(colnames(trait_data),colnames(marker_data)))
		marker_data <- marker_data[,matchV]
		matchV <- na.omit(match(colnames(marker_data),colnames(trait_data)))
		trait_data <- trait_data[,matchV]
	}else{
		if(verbose)cat("INFO: Scaling down the traitset\n")
		matchV <- na.omit(match(colnames(marker_data),colnames(trait_data)))
		trait_data <- trait_data[,matchV]
		matchV <- na.omit(match(colnames(trait_data),colnames(marker_data)))
		marker_data <- marker_data[,matchV]
	}
	if(verbose)cat("INFO: Number of individuals in Marker set:",dim(marker_data)[2],"\n")
	if(verbose)cat("INFO: Number of individuals in Phenotype set:",dim(trait_data)[2],"\n")

#Parse data towards the R/QTL format we need to convert all AA/AB/BB etc to 1,2,3
	cnt <- 0
	for(i in 1:dim(marker_data)[1]) {
		for(j in 1:dim(marker_data)[2]) {
			repl <- FALSE
			cnt<-cnt+1
			if(cnt%%100==0){
			  Sys.sleep(0.1)
			  cat(".")
			}
			if(is.na(marker_data[i,j])){
				marker_data[i,j] <- NA
				repl <- TRUE
			}
#RIL
			if(prob_cross=="riself"){
			if(!repl && (as.character(marker_data[i,j]) == 'A' || as.character(marker_data[i,j]) == '0' || as.character(marker_data[i,j]) == '1')){
				marker_data[i,j] <- 1
				repl <- TRUE
			}
			if(!repl && (as.character(marker_data[i,j]) == 'B'|| as.character(marker_data[i,j]) == '2')){
				marker_data[i,j] <- 2
				repl <- TRUE
			}
			}
#BC
			if(prob_cross=="bc"){
			if(!repl && as.character(marker_data[i,j]) == 'AA'){
				marker_data[i,j] <- 1
				repl <- TRUE
			}
			if(!repl && as.character(marker_data[i,j]) == 'AB'){
				marker_data[i,j] <- 2
				repl <- TRUE
			}
			}
#F2 intercross
			if(prob_cross=="f2"){
			if(!repl && as.character(marker_data[i,j]) == 'AA' || as.character(marker_data[i,j]) == 'A' || as.character(marker_data[i,j]) == 'X'){
				marker_data[i,j] <- 1
				repl <- TRUE
			}
			if(!repl && as.character(marker_data[i,j]) == 'AB' || as.character(marker_data[i,j]) == 'H' || as.character(marker_data[i,j]) == 'Y'){
				marker_data[i,j] <- 2
				repl <- TRUE
			}
			 
			if(!repl && as.character(marker_data[i,j]) == 'BB' || as.character(marker_data[i,j]) == 'B'){
				marker_data[i,j] <- 3
				repl <- TRUE
			}
			}
			if(!repl){
				marker_data[i,j] <- NA
			}
		}
	}
	cat("\nConverted into R/QTL coding\n")
	chr <- NULL
	chrid <- NULL
	loc <- NULL
	names <- NULL
	allchr <- find.chromosome(investigation_name=investigationname)
	for(i in 1:dim(marker_data)[1]){
		chrid <- marker_info[which(rownames(marker_data)[i]== marker_info$name),"chromosome_id"]
		chr <- c(chr,allchr[which(chrid==allchr$id),]$ordernr)
		loc <- c(loc,marker_info[which(rownames(marker_data)[i]== marker_info$name),"cm"])
		names <- c(names,marker_info[which(rownames(marker_data)[i]== marker_info$name),"name"])
	}
#FIX for NA in chromosome
	remFromChr <- NULL
	for(i in 1:length(chr)) {
		if(is.na(chr[i]) || chr[i]==""){
			if(verbose)cat("INFO: Gonna remove marker #",i,"Which is prob:",names(marker_data[,1])[i],"\n")
			remFromChr <- c(remFromChr,i)
		}
	}
	if(!is.null(remFromChr)){
		chr <- chr[-remFromChr]
		loc <- loc[-remFromChr]
		names <- names[-remFromChr]
		marker_data <- marker_data[-remFromChr,]
	}
	
#All in expected format, So we can begin filling our cross object
	cross <- NULL
	cur_chr <- 0
	for(i in sort(unique(chr))){
# for each chromosome do
		cur_chr <- cur_chr+1
		matrix <- NULL
		map <- NULL
		namez <- NULL
		length(cross$geno) <- cur_chr
		for(j in which(chr==i)){
			#For all markers on the chromosome do
			#cat("INFO: marker:",j,names[j],"on chr",i,"at",loc[j],"\n")
			matrix <- rbind(matrix,marker_data[j,])
			map <- rbind(map,loc[j])
			namez <- rbind(namez,names[j])
		}
		mapi <- cbind(namez,map)
		chrmap <- as.numeric(mapi[,2])
		names(chrmap) <- mapi[,1]
		schrmap <- sort(chrmap)
		resort <- match(names(schrmap),names(chrmap))
		map <- map[resort]
		namez <- namez[resort]
		matrix <- matrix[resort,]

#We got everything so lets start adding it to the cross object
		
		cross$geno[[cur_chr]]$data <- t(matrix)
		colnames(cross$geno[[cur_chr]]$data)<- namez
		cross$geno[[cur_chr]]$map <- as.numeric(t(map))
		names(cross$geno[[cur_chr]]$map) <- colnames(cross$geno[[cur_chr]]$data)
#Type of the chromosome should be retrieved from the database
		if(i=="X"){
			class(cross$geno[[cur_chr]])[1] <- "X"
		}else{
			class(cross$geno[[cur_chr]])[1] <- "A"
		}
	}
	names(cross$geno) <- unique(chr)
	cross$pheno <- as.data.frame(t(trait_data[,]))
	#Make it into a crossobject get the kind of cross from the database (RISELF/RISIBL/F2/BC)
  	cat("The algorithm thinks u downloaded a cross of type:",prob_cross,"\n")
	class(cross)[1] <- prob_cross
	class(cross)[2] <- "cross"
	cross
}
 
# end of CrossFromMolgenis.R
