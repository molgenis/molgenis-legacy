#Enable getting a cross from molgenis
msource(paste(r_api_location,"xgap/R/CrossFromMolgenis.R",sep=""))
#Enable getting a result from molgenis
msource(paste(r_api_location,"xgap/R/ResultsFromMolgenis.R",sep=""))
#Enable storing a result to molgenis
msource(paste(r_api_location,"xgap/R/ResultsToMolgenis.R",sep=""))
#Enable Plink analysis
msource(paste(r_api_location,"xgap/R/PlinkFromMolgenis.R",sep=""))