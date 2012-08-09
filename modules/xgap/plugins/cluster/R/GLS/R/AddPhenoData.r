# Apart from a few more comments, this new version isn't actually much different.
# Only difference: the row.names are now the accession-names + _1, _2 etc for the replicates, instead of a,b,c.... 

AddPhenoData  <- function(gwas.obj,csv.file.name,add.var.means=FALSE,mean.cols=0,make.pheno.image=F,pheno.image.name="pheno.RData",add.normal.transform=F) {
# assumes that the working directory is correct, and that the functions script is loaded
# input  : the "gwas-object" gwas.obj and the phenotypic csv-file csv.file.name
# output : the same gwas.obj, the element gwas.obj$pheno containing the data from the csv file
#           if make.pheno.image=TRUE, also an R-image containing the phenotypic data is created
#           (for consistency with earlier version this data-frame is then called data1)

if (!add.var.means) {mean.cols<-0}
if (sum(mean.cols)==0) {add.var.means<-FALSE}
#
data0                   <- read.table(file=csv.file.name,sep=",",na.strings="",header=T)
data0                   <- read.table(file=csv.file.name,sep=",",na.strings="",header=T,colClasses=c("character",rep("numeric",ncol(data0)-1))) # ,colClasses=rep("numeric",12)
# complete the genotype-names:
for (i in 2:nrow(data0)) {if (is.na(data0[i,1])) {data0[i,1] <- data0[i-1,1]}}
names(data0)[1]         <- "genotype"
# convert the accession names from factor to character
data0$genotype          <- as.character(data0$genotype)
data1                   <- data.frame(data0, stringsAsFactors = F)
# remove the accessions which are not in plant.names
data1                   <- data1[setdiff(1:nrow(data1),which(data1$genotype %in% setdiff(unique(data1$genotype),gwas.obj$plant.names))),]
# count the number of replicates in the remaining genotypes (regardless of missing values in the phenotype)
#rep.vec                 <- tabulate(factor(data1$genotype))
rep.vec                 <- as.numeric(table(data1$genotype)[match(unique(data1$genotype),sort(unique(data1$genotype)))])


# Add _1,_2, etc to the row-names # previously : add letters a,b,c... to the row-names
##!##
#row.names(data1)[1:(rep.vec[1])]<- paste(data1$genotype[1:(rep.vec[1])], letters[1:(rep.vec[1])],sep="")
row.names(data1)[1:(rep.vec[1])]<- paste(data1$genotype[1:(rep.vec[1])],"_",as.character(1:(rep.vec[1])),sep="")
for (i in 2:length(rep.vec)) {row.names(data1)[sum(rep.vec[1:(i-1)])+ 1:(rep.vec[i])]<- paste(data1$genotype[sum(rep.vec[1:(i-1)])+ 1:(rep.vec[i])],"_",as.character(1:(rep.vec[i])),sep="")}
# Now the row names are the accession names, where each accession name is replicated, with lower case letters as extension,
# e.g. CS76113a,CS76113b,CS76113c

data2                   <- data1
# Now add extra accessions, i.e. the ones that do not occur in data1/data2, but DO occur in plant.names(2?)
n.extra <- length(setdiff(gwas.obj$plant.names,unique(data2$genotype)))
if (n.extra>0) {
  extra.acc               <- data.frame(matrix(NA,n.extra,ncol(data2)))
  extra.acc[,1]           <- setdiff(gwas.obj$plant.names,unique(data2$genotype))
  row.names(extra.acc)    <- paste(extra.acc[,1],"_1",sep="")
  names(extra.acc)        <- names(data2)
  data2                   <- rbind(data2,extra.acc)
  rep.vec                 <- c(rep.vec,rep(1,n.extra))
}

###################################
# Now put everything in the same order as in plant.names:

rep.vec2     <- rep.vec[match(gwas.obj$plant.names,unique(data2$genotype))]
plant.names2 <- rep(gwas.obj$plant.names,times=rep.vec2)
plant.names2[1:(rep.vec2[1])] <- paste(plant.names2[1:(rep.vec2[1])], "_",as.character(1:(rep.vec2[1])),sep="")

for (i in 2:length(rep.vec2)) {
  plant.names2[sum(rep.vec2[1:(i-1)])+ 1:(rep.vec2[i])] <-
  paste(plant.names2[sum(rep.vec2[1:(i-1)])+ 1:(rep.vec2[i])],"_",as.character(1:(rep.vec2[i])),sep="")
}

data3   <- data2[match(plant.names2,row.names(data2)),]

data1   <- data3
rep.vec <- rep.vec2

###################################

if (add.var.means) {
  NC    <- ncol(data1)
  data1 <- AddMeans(input.frame=data1,col.select=mean.cols)   # the averages of the columns given in col.select are now added as extra columns
  if (add.normal.transform) {
    for (i in c(mean.cols,NC+1:length(mean.cols))) {
      data1 <- cbind(data1,qqnorm(data1[,i],plot.it=F)$x)
      names(data1)[ncol(data1)] <- paste(names(data1)[i],"_transformed",sep="")
    }
  }
}


if (make.pheno.image) {save(data1,rep.vec,file=pheno.image.name)}


gwas.obj$pheno  <- data1
# old code :
#save(gwas.obj,file=new.gwas.image)
#assign(gwas.obj$pheno, value=data1, envir = .GlobalEnv)
gwas.obj
}

