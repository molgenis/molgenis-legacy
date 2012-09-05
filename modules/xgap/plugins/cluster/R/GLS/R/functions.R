
#pst <- function(str) {paste(,sep="")}
AddMeans <- function(input.frame,col.select=1:ncol(input.frame),keep.original=TRUE) {
    # input.frame is assumed to have the same format as data1; it should contain a column named "genotype"
    #input.frame=data1;col.select=3:ncol(input.frame);keep.original=TRUE
    n.col         <- ncol(input.frame) 
    n.col.select  <- length(col.select)
    input.frame   <- cbind(input.frame,input.frame[col.select])
    input.frame[,-(1:n.col)]  <- NA
    names(input.frame)[-(1:n.col)]  <- paste(names(input.frame)[col.select],".mean",sep="")    
    #tapply(data1$AUC.NS, factor(data1$genotype,ordered=T),FUN=mean,na.rm =T)
    ind.names     <- unique(input.frame$genotype)
    first.occurence <- rep(0,length(ind.names))
    for (pl in 1:length(ind.names)) {first.occurence[pl]  <- min(which(input.frame$genotype==ind.names[pl]))}
    for (cl in 1:n.col.select) {
      new.col <- aggregate(input.frame[,col.select[cl]], by=list(input.frame$genotype),FUN=mean,na.rm =T)
      new.col <-     new.col[match(ind.names,new.col$Group.1),2]
      new.col[is.nan(new.col)]  <- NA
      input.frame[first.occurence,n.col+cl]   <- new.col
    }
    #if(!keep.original)
    input.frame
}

MakeSnpBoxplot    <- function(data.vector,marker.vector,file.name="") {
    data.vector   <- as.numeric(data.vector)
    marker.vector <- as.numeric(marker.vector)
    plot.data   <- data.frame(marker.value=marker.vector,trait.value=data.vector)
    plot.data   <- plot.data[!is.na(plot.data$trait.value),] 
    if (file.name=="") {
        boxplot(trait.value ~ marker.value,data=plot.data,
                names=c(paste("n0=",as.character(sum(plot.data$marker.value==0)),sep=""),
                paste("n1=",as.character(sum(plot.data$marker.value==1)),sep="")))
    } else {   
        jpeg(file.name,quality=100)
        boxplot(trait.value ~ marker.value,data=plot.data,
        names=c(paste("n0=",as.character(sum(plot.data$marker.value==0)),sep=""),
        paste("n1=",as.character(sum(plot.data$marker.value==1)),sep="")))
        dev.off()
    }
}

# then : 
#- asreml # nb columbia genotypes !!
#- call this using snp.sig, + genes 
# license : system command !
# boxplots
# output in files
# document denife.haplotypes; classification, MAF etc
# interpretation haplo-tested
# correction haplo boxplot (when MAF>0)

# estimate.interaction
#geno.vector=GWAS.obj$pheno$genotype;marker1=as.numeric(GWAS.obj$markers[interactions$snp1[ep],GWAS.obj$pheno$genotype]);marker2=as.numeric(GWAS.obj$markers[interactions$snp2[ep],GWAS.obj$pheno$genotype]);trait.values=GWAS.obj$pheno[,tr];cov.values=cov.frame.interaction;kinship.asreml.object=GWAS.obj$kinship.asreml
#
#
# assumes 0-1 markers    
# TO DO : HETEROZYGOTES
#

MakeGwasObject2 <- function(geno.data="atwell_data.RData",description="test",maf=0) {
#                           csvName=paste(description,".csv",sep=""),binName=paste(description,".bin",sep=""),RimageName=paste(description,".RData",sep="")) {
# OBJECTIVE : 
# load the objects created using the function ReadMarkerAndGeneData; then call 
# the function MakeGwasObject to make a GWAS object
#
# INPUT :
# *
# *
# *
# OUTPUT :
# *
# *
# *
load(geno.data)

MakeGwasObject(marker.object=snp,description=description,gene.info=gene.info,gene.dataframe=genes,
               generate.csv=T,generate.bin=T,csvName=paste(description,".csv",sep=""),
               binName=paste(description,".bin",sep=""),maf=maf,
               RimageName=paste(description,".RData",sep=""),returnObject=F)
}
###########################
ReadMarkerAndGeneData  <- function(gffFileName="TAIR10_GFF3_genes.gff",geno.data="atwell_data.RData",
                                   snp.file.name="call_method_32_without_header.csv",
                                   header.name="call_method_32_header.csv",n.snp=216130,
                                   snp.start.col=3,ref.nr=1,maf=0.10) {
### OBJECTIVE :
#
### INPUT :
# gffFileName   : gff(3) file obtained from TAIR
# snp.file.name : the file containing all snp-data, WITHOUT header
#                  Should contain letters A,C,G,T and "-" for missing values
# geno.data     : name of the RData file that is to be created
# header.name   : file containing the accession names (see the default for an example)
#                 SHOULD JUST CONTAIN THE ACCESSION NAMES, ON ONE LINE, SEPARATED BY COMMAS!
# n.snp         : number of markers in the file snp.file.name 
#                 since snp.file.name does not have a header (i.e. column names)
#                 n.snp should be the number of lines in this file.
#                 n.snp may be overestimated by several lines, but not underestimated
# snp.start.col : column number where the marker-data start. SHOULD NOW BE 3 !!
# ref.nr        : column number of the reference genotype, counted from snp.start.col
#                  i.e. if the first column of markers is the reference genotype, then ref.nr=1
# maf : minor allele frequency to which the markers in marker.object WILL BE restricted
#
### OUTPUT :
# an R-image with the name r.image.name, containing a data-frame snp, the numbers n (of genotypes) and N (of snps)
# in the data-frame, and the file plant.names.csv, containing all genotype names
# The data frame snp has N rows and n+2 columns. The first and second  column  contain the chromosome number and base pair
# position of each marker.
### comments

gc()
#header          <- read.table(file=header.name,sep=",")
#plant.names     <- as.character(unlist(header[1,snp.start.col:ncol(header)]))
plant.names     <- as.character(read.table(file=header.name,sep=",",colClasses="character"))
n               <- length(plant.names)                     # number of genotypes

list.of.blocks  <- DefineBlocks(1:n.snp,block.size=40000)
n.block         <- length(list.of.blocks)
con             <- file(description=snp.file.name, open = "r")
cat("",file=paste("new_",snp.file.name,sep=""))

for (bl in 1:n.block) {
  snp             <- read.table(con,sep=",", nrows = length(list.of.blocks[[bl]])) # ,colClasses=c(rep("character",n+2))
  number.of.missing <- apply(snp,1,FUN=function(x){sum(x=="-")+sum(is.na(x))})
  # remove the markers with at least one missing obervation
  snp             <- snp[number.of.missing==0,]
  if (maf>0) {
    minor.allele.fr <- apply(snp[,snp.start.col:ncol(snp)],1,FUN=function(x){sum(sort(table(unlist(x)),decreasing=T)[-1])})/n
    snp             <- snp[minor.allele.fr>=maf,]
  }
  snp[,snp.start.col:ncol(snp)] <- as.data.frame(lapply(snp[,snp.start.col:ncol(snp)],FUN=ConvertACGTfactorLevelsTo1234))
  # for all columns of the snp-data, except the column corresponding to the reference genotype,
  # we change the snp-values : they will be zero if a genotype is the same as the reference genotype
  # on that locus, and larger than zero otherwise
  snp[,-c(1:(snp.start.col-1),snp.start.col+ref.nr-1)] <- as.data.frame(lapply(snp[,-c(1:(snp.start.col-1),snp.start.col+ref.nr-1)],FUN=function(x){abs(x-snp[,snp.start.col+ref.nr-1])}))
  # We now put the snp-values of the reference genotype to zero
  snp[,snp.start.col+ref.nr-1] <- 0  
  snp[,snp.start.col:ncol(snp)] <- as.data.frame(lapply(snp[,snp.start.col:ncol(snp)],FUN=function(x){x[x>1]<-1;return(1-x)}))
  # Keep the snp-data abd first two columns, which are supposed to contain the chromosome and position data
  # Throw away all other columns 
  snp <- snp[,c(1:2,snp.start.col:ncol(snp))]
  write.table(snp,sep=",",col.names=F,row.names=F,quote=F,file=paste("new_",snp.file.name,sep=""),append=TRUE)
}
close(con)
# snp             <- read.table(file="large_test_file.csv",colClasses=rep("integer",10),sep=",")

rm(snp)
gc()

snp             <- read.table(file=paste("new_",snp.file.name,sep=""),sep=",",colClasses=rep("integer",10))
names(snp)      <- c("chromosome","position",plant.names)
row.names(snp)  <- paste("m",as.character(1:nrow(snp)),sep="")
N               <- nrow(snp)
#


gff <- gffRead(gffFileName)
gff <- gff[gff$feature=="gene",]
gff <- gff[(gff$seqname!="ChrC") & (gff$seqname!="ChrM"),]
gff$seqname     <- as.integer(as.factor(gff$seqname))
gff$Name <- getAttributeField(gff$attributes, "Name")

gc()

nchr            <- length(unique(snp[,1]))
chr.lengths     <- as.integer(table(snp[,1]))
chr.pos         <- c(0,cumsum(chr.lengths)[1:(nchr-1)])

gene.lengths     <- as.integer(table(gff$seqname))
gene.pos         <- c(0,cumsum(gene.lengths)[1:(nchr-1)])

for (i in 1:nchr) {
  gff$end[gene.pos[i] + 1:(gene.lengths[i]-1)]  <-  gff$start[gene.pos[i] + 2:(gene.lengths[i])] - 1
}

gene.info       <- rep(NA,nrow(snp))

for (i in 1:nrow(gff)) {
  gene.info[(snp$chromosome==gff$seqname[i]) & (snp$position %in% ((gff$start[i]):(gff$end[i])))]  <- gff$Name[i]
  #cat(i,"\n")
}

n.g         <- length(unique(gene.info[!is.na(gene.info)]))
genes       <- data.frame(gene.name=as.character(unique(gene.info[!is.na(gene.info)])),first.marker=rep(0,n.g),last.marker=rep(0,n.g),gene.length=rep(0,n.g))
for (i in 1:n.g) {gene.i  <- which(gene.info==genes$gene.name[i])
                  genes$first.marker[i] <- min(gene.i)
                  genes$last.marker[i]  <- max(gene.i)
                  }
genes$gene.length <- genes$last.marker-genes$first.marker+1
genes$gene.name   <-  as.character(genes$gene.name)

save(genes,n.g,gene.info,snp,n,N,plant.names,maf,file=geno.data)
}

############################

# new version
ReadMarkerAndGeneData2  <- function(gffFileName="TAIR10_GFF3_genes.gff",geno.data="atwell_data.RData",
                                   snp.file.name="call_method_32_without_header.csv",
                                   header.name="call_method_32_header.csv",n.snp=216130,
                                   snp.start.col=3,ref.nr=1,maf=0.10,max.shift=3000) {
### OBJECTIVE :
#
### INPUT :
# gffFileName   : gff(3) file obtained from TAIR
# snp.file.name : the file containing all snp-data, WITHOUT header
#                  Should contain letters A,C,G,T and "-" for missing values
# geno.data     : name of the RData file that is to be created
# header.name   : file containing the accession names (see the default for an example)
#                 SHOULD JUST CONTAIN THE ACCESSION NAMES, ON ONE LINE, SEPARATED BY COMMAS!
# n.snp         : number of markers in the file snp.file.name 
#                 since snp.file.name does not have a header (i.e. column names)
#                 n.snp should be the number of lines in this file.
#                 n.snp may be overestimated by several lines, but not underestimated
# snp.start.col : column number where the marker-data start. SHOULD NOW BE 3 !!
# ref.nr        : column number of the reference genotype, counted from snp.start.col
#                  i.e. if the first column of markers is the reference genotype, then ref.nr=1
# maf : minor allele frequency to which the markers in marker.object WILL BE restricted
# max.shift : maximum number of base pairs the end of a gene is extended to the right (in case the strand is -)
#                                          the start of a gene is extended to the left (in case the strand is +)
### OUTPUT :
# an R-image with the name r.image.name, containing a data-frame snp, the numbers n (of genotypes) and N (of snps)
# in the data-frame, and the file plant.names.csv, containing all genotype names
# The data frame snp has N rows and n+2 columns. The first and second  column  contain the chromosome number and base pair
# position of each marker.
### comments

gc()
#header          <- read.table(file=header.name,sep=",")
#plant.names     <- as.character(unlist(header[1,snp.start.col:ncol(header)]))
plant.names     <- as.character(read.table(file=header.name,sep=",",colClasses="character"))
n               <- length(plant.names)                     # number of genotypes

list.of.blocks  <- DefineBlocks(1:n.snp,block.size=40000)
n.block         <- length(list.of.blocks)
con             <- file(description=snp.file.name, open = "r")
cat("",file=paste("new_",snp.file.name,sep=""))

for (bl in 1:n.block) {
  snp             <- read.table(con,sep=",", nrows = length(list.of.blocks[[bl]])) # ,colClasses=c(rep("character",n+2))
  number.of.missing <- apply(snp,1,FUN=function(x){sum(x=="-")+sum(is.na(x))})
  # remove the markers with at least one missing obervation
  snp             <- snp[number.of.missing==0,]
  if (maf>0) {
    minor.allele.fr <- apply(snp[,snp.start.col:ncol(snp)],1,FUN=function(x){sum(sort(table(unlist(x)),decreasing=T)[-1])})/n
    snp             <- snp[minor.allele.fr>=maf,]
  }
  snp[,snp.start.col:ncol(snp)] <- as.data.frame(lapply(snp[,snp.start.col:ncol(snp)],FUN=ConvertACGTfactorLevelsTo1234))
  # for all columns of the snp-data, except the column corresponding to the reference genotype,
  # we change the snp-values : they will be zero if a genotype is the same as the reference genotype
  # on that locus, and larger than zero otherwise
  snp[,-c(1:(snp.start.col-1),snp.start.col+ref.nr-1)] <- as.data.frame(lapply(snp[,-c(1:(snp.start.col-1),snp.start.col+ref.nr-1)],FUN=function(x){abs(x-snp[,snp.start.col+ref.nr-1])}))
  # We now put the snp-values of the reference genotype to zero
  snp[,snp.start.col+ref.nr-1] <- 0  
  snp[,snp.start.col:ncol(snp)] <- as.data.frame(lapply(snp[,snp.start.col:ncol(snp)],FUN=function(x){x[x>1]<-1;return(1-x)}))
  # Keep the snp-data abd first two columns, which are supposed to contain the chromosome and position data
  # Throw away all other columns 
  snp <- snp[,c(1:2,snp.start.col:ncol(snp))]
  write.table(snp,sep=",",col.names=F,row.names=F,quote=F,file=paste("new_",snp.file.name,sep=""),append=TRUE)
}
close(con)
# snp             <- read.table(file="large_test_file.csv",colClasses=rep("integer",10),sep=",")

rm(snp)
gc()

snp             <- read.table(file=paste("new_",snp.file.name,sep=""),sep=",",colClasses=rep("integer",10))
names(snp)      <- c("chromosome","position",plant.names)
row.names(snp)  <- paste("m",as.character(1:nrow(snp)),sep="")
N               <- nrow(snp)
#
nchr            <- length(unique(snp[,1]))
chr.lengths     <- as.integer(table(snp[,1]))
chr.pos         <- c(0,cumsum(chr.lengths)[1:(nchr-1)])

### read gff file
gff <- gffRead(gffFileName)
gff <- gff[gff$feature=="gene",]
gff <- gff[(gff$seqname!="ChrC") & (gff$seqname!="ChrM"),]
gff$seqname     <- as.integer(as.factor(gff$seqname))
gff$Name <- getAttributeField(gff$attributes, "Name")

### remove genes that are completely covered (overlapped) by another gene

geneRegion <- function(currentGeneNumber,End,Start=1,size=5) {max(Start,currentGeneNumber-size):min(End,currentGeneNumber+size)}
n.gene     <- nrow(gff)
gene.test  <- rep(0,n.gene)
for (i in 1:n.gene) {gene.test[i] <- sum(apply(gff[geneRegion(currentGeneNumber=i,End=n.gene),4:5],1,FUN=function(x){sum(gff[i,4:5] %in% (x[1]):(x[2]))})==2)}
gff        <- gff[which(gene.test==1),]

### sort gff (with starting position from small to large, within each chromosome)
gff <- gff[order(gff$seqname,gff$start),]

n.gene     <- nrow(gff)
gene.test2 <- rep(0,n.gene)
for (chr in unique(gff$seqname)) {
  rows.chr.i <- which(gff$seqname==chr)
  rows.chr.i <- rows.chr.i[2:(length(rows.chr.i)-1)]
  #for (j in rows.chr.i) {gene.test[j] <- (gff$end[j-1]>=gff$start[j]) & (gff$start[j+1]<=gff$end[j])}
  for (j in rows.chr.i) {gene.test2[j] <- (gff$end[j-1]>=gff$start[j+1])}
 } 
gff        <- gff[which(gene.test2==0),]

### sort gff (with starting position from small to large, within each chromosome)
gff <- gff[order(gff$seqname,gff$start),]

# which(gff$start!=gff[order(gff$seqname,gff$start),][,4])
# which(gff$end<gff$start)

### define number of chromosomes, their lengths, starting positions,...
gc()
gene.lengths     <- as.integer(table(gff$seqname)) # number of genes per chromosome
gene.pos         <- c(0,cumsum(gene.lengths)[1:(nchr-1)])

# using strand information :
gff.new <- gff
for (i in 1:nchr) {
  i.index    <- gene.pos[i] + 1:(gene.lengths[i]-1)
  strand.min <- which(gff$strand[i.index]=="-")
  strand.pos <- which(gff$strand[i.index+1]=="+")
  new.end    <- apply(rbind(gff$start[i.index+1][strand.min] - 1,gff.new$end[i.index][strand.min]),2,FUN=max)
  new.end    <- apply(rbind(new.end,gff.new$end[i.index][strand.min]+max.shift),2,FUN=min)
  new.start  <- apply(rbind(gff$end[i.index][strand.pos] + 1,gff.new$start[i.index+1][strand.pos]),2,FUN=min)
  new.start  <- apply(rbind(new.start,gff.new$start[i.index+1][strand.pos]-max.shift),2,FUN=max)  
  gff.new$end[i.index][strand.min]      <-  new.end
  gff.new$start[i.index+1][strand.pos]  <-  new.start
}
# which(gff.new$start!=gff.new[order(gff.new$seqname,gff.new$start),]$start)
# which(gff.new$end<gff.new$start)
### sort gff (with starting position from small to large, within each chromosome)
#gff.new <- gff.new[order(gff.new$seqname,gff.new$start),]

gene.info       <- data.frame(gene1=rep(NA,nrow(snp)),gene2=rep(NA,nrow(snp)))
#
for (i in seq(1,nrow(gff.new),by=2)) {
  gene.info$gene1[(snp$chromosome==gff.new$seqname[i]) & (snp$position %in% ((gff.new$start[i]):(gff.new$end[i])))]  <- gff.new$Name[i]
}
for (i in seq(2,nrow(gff.new),by=2)) {
  gene.info$gene2[(snp$chromosome==gff.new$seqname[i]) & (snp$position %in% ((gff.new$start[i]):(gff.new$end[i])))]  <- gff.new$Name[i]
}
# 'allign' the gene1 and gene2 columns: if there is exactly one NA for a certain gene-name 
# (i.e. row in the gene.info data-frame), it should always be under gene2
na.12.ind <- (!is.na(gene.info$gene2) & is.na(gene.info$gene1))
gene.info$gene1[na.12.ind] <- gene.info$gene2[na.12.ind]
gene.info$gene2[na.12.ind] <- NA

### test
#which(apply(gene.info,1,function(x){sum(is.na(x))})==2)
#sum(apply(gene.info,1,function(x){sum(is.na(x))})==2)
################
# construction without loops ?
#gff.odd <- as.data.frame(t(gff.new[seq(1,nrow(gff.new),by=2),c(1,4:5])))
#names(gff.odd) <- gff.new$Name[seq(1,nrow(gff.new),by=2)]
#snps.odd <- lapply(gff.odd,FUN=function(x){(snp$chromosome==x[3]) & (snp$position %in% ((x[2]):(x[3])))})
##################

n.gene      <- length(unique(gff.new$Name))
genes       <- data.frame(gene.name=as.character(gff.new$Name),first.marker=rep(0,n.gene),last.marker=rep(0,n.gene),gene.length=rep(0,n.gene))
for (i in 1:nrow(genes)) {
  gene.i <- which((snp$chromosome==gff.new$seqname[i]) & (snp$position %in% ((gff.new$start[i]):(gff.new$end[i]))))
  if (length(gene.i)>0) {
    genes$first.marker[i] <- min(gene.i)
    genes$last.marker[i]  <- max(gene.i)
    genes$gene.length[i]  <- genes$last.marker[i]-genes$first.marker[i]+1
  }
}
genes$gene.name   <-  as.character(genes$gene.name)
genes <- genes[which(genes$gene.length>0),]
####test
#gene.list1 <- unique(c(gene.info$gene1[!is.na(gene.info$gene1)],gene.info$gene2[!is.na(gene.info$gene2)]))
#length(gene.list1)
#gene.list2 <- unique(genes$gene.name) #gene.list2 <- unique(gff.new$Name)
#setdiff(gene.list1,gene.list2)
#setdiff(gene.list2,gene.list1)
######

#########
n.g <- nrow(genes)
rm(n.gene,gff,gff.new)
#save(genes,n.g,gene.info,snp,n,N,plant.names,maf,file=geno.data)
save(genes,n.g,gene.info,snp,n,N,plant.names,maf,file=geno.data)

}

#ReadMarkerAndGeneData(gffFileName="TAIR9_GFF3_genes.gff",geno.data="wur_data.RData",snp.file.name="hapmapsnpfile_without_header.csv",header.name="hapmapsnpfile_header.csv",n.snp=214555,snp.start.col=3,ref.nr=18,maf=0)
#gffFileName="TAIR9_GFF3_genes.gff";geno.data="wur_data.RData";snp.file.name="hapmapsnpfile_without_header.csv";header.name="hapmapsnpfile_header.csv";n.snp=214555;snp.start.col=3;ref.nr=18;maf=0

# Future options / to do :
# - read kinship-matrix from a file
# - test the first lines (reading markers from a file)
#
MakeGwasObject <- function(marker.object,description="test",kinship=matrix(0),map=data.frame(),
                           pheno=data.frame(),gene.info=character(),kinship.type="ibs",
                           accession.name.type=1,allele.name.type=1,marker.name.type=2,maf=0,
                           gene.dataframe=data.frame(),generate.csv=FALSE,generate.bin=FALSE,
                           csvName=paste(description,".csv",sep=""),binName=paste(description,".bin",sep=""),
                           RimageName=paste(description,".RData",sep=""),returnObject=TRUE) {

# OBJECTIVE : given several internal and/or external objects, create a GWAS object
#
# INPUT :
# * marker.object :  a string, specifying a csv-file name with the marker-data
#   (row names: marker names; column-names: genotype names)
#    Alternatively, marker.object may be an R-data.frame similar to that
#  * the map (2 cols: chromosome and position) is either contained in the data-frame "marker.object",
#    or in a separate  data.frame "map" (in which case "marker.object" ONLY contains marker scores).
#    The column names should be "chromosome" and "position"; these should be the first 2 columns.
#    The positions are in base-pair or morgan. They should not be 'cumulative' over the chromosomes
# *  description: name of the data-set
# *  pheno : an R-data-frame with the phenotypic data. Importing pheno-data from a text/csv file can be done
#         using the AddPhenoData function
# *  accession.name.type : the type of accession names used in marker.object:
#    1= "stock.new", 2= "stock.old", 3= "ecotype.standard", 4="ecotype.yi", 5="array.id"
# *  allele.name.type   : the allele-encoding used in marker.object:
#    1="columbia as one",2="minor allele as one",3="other"
# *  marker.name.type   : the type of marker names used in marker.object:
#    1="chromosome, position and gene",2="numbers",3="other"
# * maf : minor allele frequency to which the markers in marker.object have been restricted
#          (in particular : before, when using the ReadMarkerAndGeneData function)
#
# * csvName
# * binName
# * RimageName : name of the R-image the GWASobject will be saved in
# * returnObject : if true, the GWASobject is returned, otherwise it is only written to the RData file
#
# OUTPUT :
# * a GWAS object (if returnObject=TRUE), where many entries and filenames will start, with "description "
# *
#
################################

# Define various factors
accession.levels   <- c("stock.new","stock.old","ecotype.standard","ecotype.yi","array.id")
allele.levels      <- c("columbia as one","minor allele as one","other")
marker.name.levels <- c("chromosome, position and gene","numbers","other")

if (is.character(marker.object)) {
  marker.object <- read.table(marker.object)
  marker.object <- as.data.frame(marker.object)
} else { # then marker.object must be an R-object. Check if it has column and row names. If not, give default names
  if (length(row.names(marker.object))==0) {row.names(marker.object)  <- paste("ind",as.character(1:nrow(marker.object)),sep="")}
  if (length(names(marker.object))==0) {names(marker.object)  <- paste("m",as.character(1:ncol(marker.object)),sep="")}
  marker.object <- as.data.frame(marker.object)
}

if (all(c("chromosome","position") %in% names(marker.object))) { # test: correct with and without map?
    map <- data.frame(chromosome=marker.object$chromosome,position=marker.object$position)
    marker.object <- marker.object[,-c(1,2)]
} else {
    if (ncol(map)>1) {names(map)[1:2]  <- c("chromosome","position")} # if the map-data-frame exists, make sure that the the first
                                                                      # 2 columns are named "chromosome" and "position"
    if (all(c("chromosome","position") %in% names(marker.object)))  {
      map <- data.frame(chromosome=map$chromosome,position=map$position)
    } else {
      map <- data.frame(chromosome=rep(1,nrow(marker.object)),position=1:nrow(marker.object))
    }
}
names(map)  <- c("chromosome","position")

###

nchr            <- length(unique(map$chromosome))
chromosomes     <- unique(map$chromosome)
chr.lengths     <- as.numeric(table(map$chromosome))
if (nchr > 1) {
  chr.pos         <- c(0,cumsum(chr.lengths)[1:(nchr-1)])
  chr.lengths.bp  <- c(0,map$position[cumsum(chr.lengths)[1:(nchr-1)]])
  cumpositions    <- map$position + rep(cumsum(chr.lengths.bp),times=chr.lengths)
} else {
  cumpositions    <- map$position
  chr.pos         <- 0
  chr.lengths.bp  <- nrow(marker.object)
}
plant.names <- names(marker.object)
n           <- ncol(marker.object)
N           <- nrow(marker.object)

### principal components, as in Patterson et al 2006

gc()
PCAmatrix  <- ComputePcaMatrix(marker.object=marker.object,maf=maf)
PCAs       <- as.data.frame(ComputePcas(PCAmatrix)$pcas)
row.names(PCAs) <- plant.names
names(PCAs)<- paste("pca",as.character(1:ncol(PCAs)),sep="")

### kinship matrix
 
if (ncol(kinship)==n) {
  A   <- kinship
} else {
    if (kinship.type=="ibs") {
        A  <- matrix(0,n,n)
        blocks <- DefineBlocks(1:N,5000) 
        for (bl in 1:length(blocks)) {
          #block       <- chr.pos[CR] + 1:chr.lengths[CR]
          A  <- A + IBS(marker.object[blocks[[bl]],],normalization=1)
        }
        A  <- A/N
    }
    if (kinship.type=="id")  {
        A  <- diag(n)
    }
}
K.name          <- paste(description,"_","kinship.csv",sep="")
write.table(A, file=K.name,quote = FALSE, sep = ",", row.names = FALSE,col.names = plant.names)
AINV            <- MakeKinshipAsreml(A,genotype.names=plant.names)

### create mapframe

if (length(gene.info)>0) {
    map.frame <- cbind(map=map,data.frame(cum.position=cumpositions),gene1=gene.info$gene1,gene2=gene.info$gene2)
} else {
    map.frame <- cbind(map=map,data.frame(cum.position=cumpositions))
}

###

csv.name  <- csvName
bin.name  <- binName

if (generate.csv) {
    csv.name=csvName
    MakeCsv(markers=marker.object,plant.names=plant.names,file.name=csv.name)
} else {
    csv.name=""
}
if (generate.bin) {
    bin.name=binName
    MakeBin(markers=marker.object,kinship.file=K.name,csv.file.name=csv.name,bin.file.name=bin.name)
} else {
    bin.name=""
}

GWAS.obj  <- list(description=description,markers=marker.object,pheno=pheno,map=map.frame,
                  kinship=A,kinship.asreml=AINV,genes=gene.dataframe,plant.names=plant.names,N=N,
                  n=n,nchr=nchr,chromosomes=chromosomes,chr.lengths.bp=chr.lengths.bp,
                  external=list(kinship.name=K.name,csv.name=csv.name,bin.name=bin.name),pca=PCAs,
                  markerInfo=list(accession.name.type=factor(x = accession.levels[accession.name.type], levels=accession.levels),
                                  allele.name.type=factor(x = allele.levels[allele.name.type], levels=allele.levels),
                                  marker.name.type=factor(x = marker.name.levels[marker.name.type], levels=marker.name.levels),maf=maf),
                  real.effects=list(locations=data.frame(NULL),sizes=data.frame(NULL))
                  )

names(GWAS.obj$map)[1:2] <- c("chromosome","position")

if (RimageName!="") {save(GWAS.obj,file=RimageName)}

if (returnObject) {return(GWAS.obj)}
}



##########################

#test.GWAS.obj <- make.GWAS.obj(marker.object=snp,description="test",generate.csv=T,generate.bin=T,csvName="test.csv",binName="test.bin",RimageName="test.RData")
#marker.object=snp;description="test";generate.csv=T;generate.bin=T;csvName="test.csv";binName="test.bin";RimageName="test.RData"
#data.path       <- "D:/willem/statistical.genetics.large.files/arabidopsis.data/"
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL.GENETICS/arabidopsis.project/version.1.0/"
#GWAS.obj <- add.pheno.data(gwas.obj=GWAS.obj,csv.file.name="2ndHAPMAPscr.csv",add.var.means=T,mean.cols=3:4,make.pheno.image=T,pheno.image.name="charles2test.RData")
#msource(paste(script.path,"functions.R",sep=""))
#setwd(data.path)

##########################
# old argument: new.gwas.image="gwas.RData"
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
# check the number of replicates in the remaining genotypes
#rep.vec                 <- tabulate(factor(data1$genotype))
rep.vec                 <- as.numeric(table(data1$genotype)[match(unique(data1$genotype),sort(unique(data1$genotype)))])


# Add letters a,b,c... to the row-names
##!##
row.names(data1)[1:(rep.vec[1])]<- paste(data1$genotype[1:(rep.vec[1])], letters[1:(rep.vec[1])],sep="")
for (i in 2:length(rep.vec)) {row.names(data1)[sum(rep.vec[1:(i-1)])+ 1:(rep.vec[i])]<- paste(data1$genotype[sum(rep.vec[1:(i-1)])+ 1:(rep.vec[i])], letters[1:(rep.vec[i])],sep="")}
# Now the row names are the accession names, where each accession name is replicated, with lower case letters as extension,
# e.g. CS76113a,CS76113b,CS76113c

data2                   <- data1
# Now add extra accessions, i.e. the ones that do not occur in data1/data2, but DO occur in plant.names(2?)
n.extra <- length(setdiff(gwas.obj$plant.names,unique(data1$genotype)))
if (n.extra>0) {
  extra.acc               <- data.frame(matrix(NA,n.extra,ncol(data2)))
  extra.acc[,1]           <- setdiff(gwas.obj$plant.names,unique(data2$genotype))
  row.names(extra.acc)    <- paste(extra.acc[,1],"a",sep="")
  names(extra.acc)        <- names(data2)
  data2                   <- rbind(data2,extra.acc)
  rep.vec                 <- c(rep.vec,rep(1,n.extra))
}


rep.vec2     <- rep.vec[match(gwas.obj$plant.names,unique(data2$genotype))]
plant.names2 <- rep(gwas.obj$plant.names,times=rep.vec2)
plant.names2[1:(rep.vec2[1])] <- paste(plant.names2[1:(rep.vec2[1])], letters[1:(rep.vec2[1])],sep="")

for (i in 2:length(rep.vec2)) {
  plant.names2[sum(rep.vec2[1:(i-1)])+ 1:(rep.vec2[i])] <- 
  paste(plant.names2[sum(rep.vec2[1:(i-1)])+ 1:(rep.vec2[i])], letters[1:(rep.vec2[i])],sep="")
}

data3   <- data2[match(plant.names2,row.names(data2)),]

data1   <- data3
rep.vec <- rep.vec2


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

# To do: rainbow colored dots for effects size
MakeLodPlot <- function(xvalues,yvalues,file.name="",jpeg.plot=T,x.lab="base pairs",
                        y.lab="-10Log(p)",x.sig=integer(0),x.effects=integer(0),
                        effects.size=numeric(0),chr.boundaries=c(0),y.thr=0) {
# OBJECTIVE : Make a plot of the LOD-profile, on screen or in a file (pdf or jpeg)
# Significant markers can be highlighted with red dots
# 
# INPUT :
# * file.name  : if "", the plot is made to the screen. Otherwise to that file (should be .jpeg or .pdf)
# * jpeg.plot  : if TRUE, jpeg is produced, otherwise pdf
# * xvalues    : vector of cumulative marker positions
# * yvalues    : vector of LOD-scores
# * x.lab      : x-axis label
# * y.lab      : y-axis label
# * x.sig      : vector of integers, indicating which components in the vectors xvalues and yvalues are significant
# * x.effects  : vector of integers, indicating which components in the vector xvalues correspond to a real (known) effect
# * effects.size: vector of reals indicating the effect-sizes corresponding to x.effects 
# * chr.boundaries : vector of chromosome boundaries, i.e. x-values on the same scale as xvalues  
# * y.thr      : LOD-threshold
#
# OUTPUT : a plot
# * markers declared significant get a red dot
# * markers with a true effect get a blue dot
# * if both the real effects and the "declared significant" are given, the markers that are in the
#   intersection (i.e. true positives) get a pink dot
if (file.name!="") {
  if (jpeg.plot) {jpeg(file.name)} else {pdf(file.name)}
}
plot(x=xvalues,y=yvalues,xlab=x.lab,ylab=y.lab,type="l",lwd=0.4)    
if (sum(chr.boundaries)!=0) {
  for (chr.b in chr.boundaries) {
    lines(x=rep(chr.b,2),y=c(0,max(yvalues)),col="green",lwd=1)
  }
}

class(x.sig) <- "integer"
class(x.effects) <- "integer"

if (sum(x.sig)!=0 | sum(x.effects)!=0) {
  if (sum(x.sig)!=0 & sum(x.effects)==0) {
    points(x=xvalues[x.sig],y=yvalues[x.sig],pch=20,col="red")
  }
  if (sum(x.sig)==0 & sum(x.effects)!=0) {
    points(x=xvalues[x.effects],y=yvalues[x.effects],pch=20,col="blue",lwd=2)
  }
  if (sum(x.sig)!=0 & sum(x.effects)!=0) {
    false.neg <- setdiff(x.effects,x.sig)
    false.pos <- setdiff(x.sig,x.effects)
    true.pos  <- intersect(x.sig,x.effects)
    points(x=xvalues[false.pos],y=yvalues[false.pos],pch=20,col="red")
    points(x=xvalues[false.neg],y=yvalues[false.neg],pch=20,col="blue")
    points(x=xvalues[true.pos],y=yvalues[true.pos],pch=20,col="purple",lwd=2)
  }
}
if (y.thr!=0) {lines(x=c(min(xvalues),max(xvalues)),y=rep(y.thr,2))}
if (file.name!="") {dev.off()}

} #   END OF THE FUNCTION

PlotMcmcResults  <- function(mcmc.results,names,effects=data.frame(),file.name="plot.pdf") {
# CODE BY CHRISTIAN SCHAFER (CREST / UNIVERSITE PARIS DAUPHINE)
#
# OBJECTIVE : 
# make a bar-plot of posterior inclusion probablities, based on mcmc or smc output 
# *
# INPUT :
# * mcmc.results : mcmc or smc output. Nruns x p matrix, where p is the number of predictors 
#                   and Nruns the number that the algorithm was run. 
# * names        : vector of variable (predictor) names
# * effects      : m x 2 data-frame or matrix containing the real effects, assuming that there are m effects
#                  first column : integers indicating the location of the effects (indices corresponding to the names vector) 
#                  second column: size of the effect
# * file.name    : name of the pdf-file the output is written to
#
# OUTPUT :
# a pdf-file 


# boxplot data from repeated runs
boxplot = apply(mcmc.results,2,FUN=quantile,probs=c(0,0.2,0.5,0.8,1))
#boxplot = t(array(boxplot,c(length(boxplot)/5,5)))

names <- as.character(names)

if (nrow(effects) > 0) effects[,2]  <- effects[,2]/max(abs(effects[,2]))

no_lines=1
no_bars=ceiling(length(names)/no_lines)

# create PDF-file
pdf(file=file.name, height=4, width=48.6)
par(mfrow=c(no_lines,1), oma=c(0, 0, 0.5, 0), mar=c(2.5, 2, 0.5, 0))

# create empty vector
empty=rep(0,length(names))

for(i in 1:no_lines) {
  start= (i-1)*no_bars + 1
  end  = min(i*no_bars, length(names))

  # create empty plot
  barplot(empty[start:end], ylim=c(0, 1), axes=FALSE, xaxs='i', xlim=c(-1, 584.2/no_lines))
  
  # plot effects
  if (length(effects) > 0) {
      for (i in 1:dim(effects)[1]) {
        if (start <= effects[i,1] && effects[i,1] <= end) {
          empty[effects[i,1]] = 1.05
          barplot(empty[start:end], col=rgb(1,1-abs(effects[i,2]),1-abs(effects[i,2])), axes=FALSE, add=TRUE)
          barplot(empty[start:end], col='black', axes=FALSE, angle=sign(effects[i,2])*45, density=15, add=TRUE)
          empty[effects[i,1]]=0
        }
      }
  }
  
  # plot results
  barplot(boxplot[,start:end], ylim=c(0, 1), names=names[start:end], las=2, cex.names=0.5, cex.axis=0.75,
          axes=TRUE, col=c('skyblue','black','white','white','black'), add=TRUE)
}
dev.off()

} # END OF FUNCTION PlotMcmcResults

mapAsDataFrame  <- function(crossObject) {
# INPUT :
# a cross-object
# OUTPUT :
# the genetic-map of the cross-object, as data-frame
# first column: chromosome; 2nd column : position
        scr          <- summary.cross(crossObject)
        mapDataFrame <- matrix(0,sum(scr$n.mar),2)
        mapDataFrame[,1]<- rep(1:scr$n.chr,times=scr$n.mar)
        for (i in 1:scr$n.chr) {
          mapDataFrame[mapDataFrame[,1]==i,2] <- pull.map(crossObject)[[i]]
          row.names(mapDataFrame)[mapDataFrame[,1]==i] <- names(pull.map(crossObject)[[i]])
        }
        mapDataFrame <- as.data.frame(mapDataFrame)
        names(mapDataFrame) <- c("chromosome","position")
return(mapDataFrame)
}

# to do simplify, and use the functions ExtractPiMassGenoFilesFromGwasObject and
# ExtractPiMassPhenoFilesFromGwasObject defined below
ExtractPiMassInputFilesFromGwasObject  <- function(gwas.object,trait.number,covariate.numbers=0,pca.correct=T,
                                                   geno.file="geno_piMASS.txt",pheno.file="pheno_piMASS.txt") {
# INPUT :
# a gwas-object
# trait.number : column number of the trait
# covariate.numbers :
# pca.correct :  
#
# OUTPUT :
# geno- and phenotype text files
if (pca.correct | sum(covariate.numbers)!=0) {
  regr.frame   <- data.frame(geno=gwas.object$pheno$geno,pheno=gwas.object$pheno[,trait.number],covars=gwas.object$pheno[,covariate.numbers])
  # residuals=rep(NA,nrow(gwas.object$pheno))
  if (pca.correct) {
    regr.frame   <- data.frame(regr.frame,gwas.object$pca[gwas.object$pheno$geno,])
  }
regr.form <- as.formula(paste("pheno ~ ",paste(names(regr.frame)[3:(ncol(regr.frame))],collapse="+")))
lin.mod   <- lm(formula=regr.form,data=regr.frame)
#regr.frame$residuals[!is.na(regr.frame$pheno)] <- lin.mod$residuals
gwas.object$pheno[!is.na(gwas.object$pheno[,trait.number]),trait.number] <- lin.mod$residuals 
}

pheno.vector <- aggregate(gwas.object$pheno[,trait.number], by=list(gwas.object$pheno$geno),FUN=mean,na.rm =T)[,2]
pheno.vector[is.nan(pheno.vector)] <- NA
pheno.frame<- qqnorm(pheno.vector,plot.it=F)$x # OR y ?????????????????
write.table(pheno.frame,file=pheno.file,quote=F,row.names=F,col.names=F,sep=",")

cat("",file=geno.file)
blocks <- DefineBlocks(1:gwas.object$N,10000)
for (i in 1:length(blocks)) {
  maj.alleles <- round(apply(gwas.object$markers[blocks[[i]],],1,FUN=mean))
  write.table(cbind(row.names(gwas.object$markers[blocks[[i]],]),1-maj.alleles,maj.alleles,
              2*gwas.object$markers[blocks[[i]],]),file=geno.file,quote=F,row.names=F,col.names=F,sep=",",append=TRUE)
}

}
# test:
# ExtractPiMassInputFilesFromGwasObject(gwas.object=GWAS.obj,trait.number=31)

ExtractPiMassPhenoFilesFromGwasObject  <- function(gwas.object,trait.number,covariate.numbers=0,pca.correct=T,pheno.file="pheno_piMASS.txt") {
# INPUT :
# a gwas-object
# trait.number : column number of the trait
# covariate.numbers :
# pca.correct :
#
# OUTPUT :
# phenotype text files

if (pca.correct | sum(covariate.numbers)!=0) {
  regr.frame   <- data.frame(geno=gwas.object$pheno$geno,pheno=gwas.object$pheno[,trait.number],covars=gwas.object$pheno[,covariate.numbers])
  # residuals=rep(NA,nrow(gwas.object$pheno))
  if (pca.correct) {
    regr.frame   <- data.frame(regr.frame,gwas.object$pca[gwas.object$pheno$geno,])
  }
regr.form <- as.formula(paste("pheno ~ ",paste(names(regr.frame)[3:(ncol(regr.frame))],collapse="+")))
lin.mod   <- lm(formula=regr.form,data=regr.frame)
#regr.frame$residuals[!is.na(regr.frame$pheno)] <- lin.mod$residuals
gwas.object$pheno[!is.na(gwas.object$pheno[,trait.number]),trait.number] <- lin.mod$residuals 
}

pheno.vector <- aggregate(gwas.object$pheno[,trait.number], by=list(gwas.object$pheno$geno),FUN=mean,na.rm =T)[,2]
pheno.vector[is.nan(pheno.vector)] <- NA
pheno.frame<- qqnorm(pheno.vector,plot.it=F)$x # OR y ??????????????????
write.table(pheno.frame,file=pheno.file,quote=F,row.names=F,col.names=F,sep=",")
}
# test:
# ExtractPiMassPhenoFilesFromGwasObject(gwas.object=GWAS.obj,trait.number=31)

ExtractPiMassGenoFilesFromGwasObject  <- function(gwas.object,geno.file="geno_piMASS.txt") {
# INPUT :
# a gwas-object
#
# OUTPUT :
# geno- text file

cat("",file=geno.file)
#gwas.object$markers <- SwitchToMinorAlleleEncoding(marker.matrix=gwas.object$markers,two.alleles=T)
blocks <- DefineBlocks(1:gwas.object$N,10000)
for (i in 1:length(blocks)) {
  maj.alleles <- round(apply(gwas.object$markers[blocks[[i]],],1,FUN=mean))
  write.table(cbind(row.names(gwas.object$markers[blocks[[i]],]),1-maj.alleles,maj.alleles,
              2*gwas.object$markers[blocks[[i]],]),file=geno.file,quote=F,row.names=F,col.names=F,sep=",",append=TRUE)
}

}
# test:
# ExtractPiMassGenoFilesFromGwasObject(gwas.object=GWAS.obj)


SwitchToMinorAlleleEncoding  <- function(marker.matrix,block.size=5000,two.alleles=F) {
# INPUT :
# a marker-matrix with zeros and ones; markers in the rows; individuals in the columns
# block.size : the operation is carried out on blocks of block.size markers
# if two.alleles=T the output-matrix is multiplied by two
# OUTPUT :
# the same marker-matrix, where, for all rows with mean larger than 0.5 the zeros and ones are interchanged
marker.matrix   <- as.matrix(marker.matrix)
list.of.blocks  <- DefineBlocks(1:nrow(marker.matrix),block.size=block.size)
for (i in 1:length(list.of.blocks)){
  marker.means <- apply(marker.matrix[list.of.blocks[[i]],],1,FUN=mean)
  marker.matrix[list.of.blocks[[i]][marker.means>0.5],] <- 1-marker.matrix[list.of.blocks[[i]][marker.means>0.5],]
  if (two.alleles) {marker.matrix[list.of.blocks[[i]],] <- 2*marker.matrix[list.of.blocks[[i]],]}
}
marker.matrix
}
# qwerty <- SwitchToMinorAlleleEncoding(marker.matrix=GWAS.obj$markers,block.size=5000,two.alleles=T)

#AddPcasToGwasobject <- function(gwas.obj) {
#}

ComputePcaMatrix  <- function(marker.object,maf=0) {
# INPUT :
# marker.object : a p x n matrix with markerscores that are either 0 or 1
#                 (TO DO: extend to 0,1,2) 
#                 n is the number of individuals; p the number of markers
# maf : minor allele frequency : all markers with (rare) allele frequency < maf are 
#        not taken into account
# OUTPUT :
# x.matrix : the matrix X= (1/p) M M' , see p. 2076 of Patterson, Price and Reich (2006)
# # removed:
# #number.of.markers : the number of markers the calculation was based on 
# #(if maf=0, identical to nrow(marker.object); otherwise it may be smaller)
########                                  
blocks <- DefineBlocks(1:nrow(marker.object),block.size=30000)
marker.means <- rep(0,nrow(marker.object))
for (bl in 1:length(blocks)) {
  marker.means[blocks[[bl]]] <- apply(marker.object[blocks[[bl]],],1,mean)# / ncol(marker.object)
}

if (sum(marker.means<maf & marker.means>(1-maf))>0) {
  marker.object <- marker.object[marker.means>=maf & marker.means<=(1-maf),]
  marker.means <- apply(marker.object, 1, sum)/ncol(marker.object)
  }
nr           <- nrow(marker.object)
rescaling    <- nr*marker.means*(1-marker.means)
nX           <- matrix(rep(0.1,(ncol(marker.object))^2),ncol=ncol(marker.object))
nX[1,1]      <- sum((marker.object[,1]-marker.means)*(marker.object[,1]-marker.means)/rescaling)
for (i in 2:ncol(marker.object)) {
    cat(i,"\n")
    nX[i,i] <- sum((marker.object[,i]-marker.means)*(marker.object[,i]-marker.means)/rescaling)
    for (j in 1:(i-1)) {
      tmp <- sum((marker.object[,i]-marker.means)*(marker.object[,j]-marker.means)/rescaling)
      nX[i,j] <- tmp; nX[j,i] <- tmp
      gc()
    }
  }
nX # list(x.matrix=nX,number.of.markers=length(marker.means))
}

ComputePcas <- function(scaled.cov.matrix,sign.thr=0.05) {
# INPUT :
# scaled.cov.matrix : a n x n matrix obtained using the function ComputePcaMatrix
# sign.thr          : significance threshold used when testing for the number of significant pcas 
# 
# OUTPUT : a list with the components
# n.pca = the number of significant pcas 
# pcas  = n x n.pca matrix, containing the scores of all individuals on these pr. components
# In a addition, a plot is made of the first two pcas (TO DO: adjust the plot; save in a file)
 
if (!is.installed("RMTstat")) {install.packages("RMTstat")}
library(RMTstat)

m           <- nrow(scaled.cov.matrix)
ev          <- eigen(scaled.cov.matrix)
evalues     <- ev[[1]]
evector1    <- ev[[2]][,1]
evector2    <- ev[[2]][,2]
significant <- TRUE
k           <- 1

while (significant) {
    meff        <- length(evalues)
    nPrime      <- (m+1)*(sum(evalues))^2 / ((m-1)*sum(evalues*evalues) - (sum(evalues))^2)
    sigma2hat   <- sum(evalues) / ((nPrime)*(m-1))
    mu.mn       <- (sqrt(nPrime-1) + sqrt(meff))^2 / nPrime
    sigma.mn    <- ((sqrt(nPrime-1) + sqrt(meff)) / nPrime) * (1/sqrt(nPrime-1) + 1/sqrt(meff))^(1/3)
    L1          <- (meff)*evalues[1] / sum(evalues)
    x.TW        <- (L1-mu.mn)/sigma.mn
    pValue      <- ptw(x.TW, beta=1, lower.tail = FALSE)
    cat(x.TW,"\t",evalues[1],"\t",pValue,"\n")
    if (pValue<sign.thr) {k <- k+1; evalues <- evalues[2:meff]} else {significant <- FALSE}
    }
## calculate the principal components
pcs <- scaled.cov.matrix %*% ev[[2]][,1:k] 
## plot :
#couleurs <- rainbow(400)[1:m]
#qw      <- 40
#couleurs<- rainbow(qw+1, s = 1, v = 1, start = 0, end = max(1,qw)/(qw+1))
#plot(x=as.vector(pcs[,1]),y=as.vector(pcs[,2]),col=couleurs)

list(n.pca=k,pcas=pcs)
}

ConvertACGTfactorLevelsTo1234 <- function(x) {
# INPUT : a vector of A,C,G and T's , of class factor
# OUTPUT : the same vector with the levels A,C,G and T replaced by 1,2,3,4 
levels(x)[levels(x)=="A"]<-"1"
levels(x)[levels(x)=="C"]<-"2"
levels(x)[levels(x)=="G"]<-"3"
levels(x)[levels(x)=="T"]<-"4"

as.integer(as.character(x))
}

# to do: n.sim >1
SimulatePhenoGivenGeno1 <- function(marker.object,effect.variance=1,n.effect=30,PVE=0.5,replicates=1,n.sim=1) {
# OBJECTIVE : as in section 5.1 of Guan and Stephens (2011) :
# simulation of a phenotype corresponding to real genotypic GWAS data. Gaussian effects
# are simulated, and the residual variance is chosen in order to have a prespecified value 
# of the PVE (explained below). Given these effects and residual variance, a normally
# distributed phenotype is simulated.
#
# INPUT :
# * marker.object : n.ind x p matrix or data.frame of 0/1 marker-scores
# * effect.variance : variance of the (normally distributed) effects
# * n.effect : number of effects
# * PVE : proportian of explained variance; this mimics the heritability
#   see Guan and Stephens (2011) for more details
# * replicates : number of replicates for each genotype
# * n.sim : number of simulated data sets
# OUTPUT :
# *
# *
# *
  n.ind            <- ncol(marker.object)*replicates
  effect.locations <- sort(sample(1:nrow(marker.object),size=n.effect))
  effect.sizes     <- rnorm(sd=sqrt(effect.variance),n=n.effect)
  predicted        <- rep(as.numeric(as.matrix(t(marker.object[effect.locations,])) %*% matrix(effect.sizes,ncol=1)),each=replicates)
  emp.effect.var   <- (predicted)^2 / n.ind
  res.variance     <- emp.effect.var * (1-PVE) / PVE
  y                <- predicted + rnorm(sd=sqrt(res.variance),n=n.ind)
list(phenotype=y,effect.locations=effect.locations,effect.sizes=effect.sizes)
}

SimulatePhenoGivenGeno2 <- function(marker.object,sigma2a=1,sigma2e=1,h=0.5,mu=10,A.matrix,replicates=1,n.sim=1) {
# OBJECTIVE : 
# simulation of a phenotype corresponding to real genotypic GWAS data.
# Following MacKenzie and Hackett (2011, appendix; assuming one block)
#
# INPUT :
# * marker.object : n.ind x p matrix or data.frame of 0/1 marker-scores
# * replicates : number of replicates for each genotype
# * n.sim : number of simulated data sets
  n.ind            <- ncol(marker.object)*replicates
  n.mar            <- nrow(marker.object)
  #
  y                <- matrix(0,n.ind,n.sim)
  effect.loc       <- rep(0,n.sim)
  marker.freq      <- rep(0,n.sim)
  a2               <- rep(0,n.sim)
  #
  for (i in 1:n.sim) { 
    effect.loc[i]  <- sample(1:n.mar,size=1)
    marker.freq[i] <- mean(as.numeric(marker.object[effect.loc[i],]),na.rm=T)
    a2[i]          <- (1/(n.ind*marker.freq[i]*(1-marker.freq[i]))) * ((n.ind-1)*sigma2e*h^2/(1-h^2) + sigma2a*(n.ind-(1/n.ind)*sum(A.matrix)))
    #
    predicted      <- rep(as.numeric(as.matrix(t(marker.object[effect.loc[i],])) %*% matrix(sqrt(a2[i]),ncol=1)),each=replicates)
    predicted      <- predicted + mu
    y[,i]          <- predicted + rnorm(sd=sqrt(sigma2e),n=n.ind) + rep(mvrnorm(n = 1, mu=rep(0,n.ind/replicates), Sigma=2*sigma2a*A.matrix, tol = 1e-6),each=replicates)
  # as.numeric(... %*% ... )
  }
list(phenotype=y,effect.locations=effect.loc,qtl.freq=marker.freq,effect.sizes=sqrt(a2))
}



MoskvinaCorrection <- function(marker.frame,number.of.replicates=rep(1,ncol(marker.frame)),
                               inv.cor.matrix=diag(rep(1,sum(number.of.replicates))),b.size=100,alpha=0.05) {
# OBJECTIVE :
# *
# *                
# INPUT :
# * marker.frame : markers in the rows; genotypes in the columns. Data from one chromosome !
# * number.of.replicates : a vector of length ncol(marker.frame). 
#   For every genotype, it gives the number of observations (individuals of that genotype) 
# * inv.cor.matrix : the inverse of the correlation matrix of the individual observations. 
#   Should have dimension sum(number.of.replicates) x sum(number.of.replicates) 
# * b.size : the block-size : for every marker, we look at the correlation with the b.size preceding markers
# * alpha : desired FWE-control
# OUTPUT :
# *
# *
# *
# USES the functions MatrixRoot and DefineBlocks                              
N     <- nrow(marker.frame)
n.geno<- ncol(marker.frame)
Kappa <- rep(1,N)

v.blocks <- DefineBlocks(2:N,block.size=b.size)
n.blocks <- length(v.blocks)
h.blocks <- v.blocks
h.blocks <- lapply(h.blocks,FUN=function(x){ymin <- min(x); ymax<- max(x); (ymin-b.size):(ymax-1)})
h.blocks[[1]] <- 1:(b.size+1)
#h.blocks[[n.blocks]] <- (min(h.blocks[[n.blocks]])):(max(max(h.blocks[[n.blocks]]),N))

INVROOT <- MatrixRoot(inv.cor.matrix)

for (b in 1:n.blocks) {
  X <- as.matrix(marker.frame[(min(h.blocks[[b]])):(max(v.blocks[[b]])),rep(1:n.geno,times=number.of.replicates)])
  #if (doubleGeno) {X <- 2*X}
  # premultiply X with the square root of inv.cor.matrix, and transpose
  X <- INVROOT %*% t(X)
  #A <- cor(X[,v.blocks[[b]]],X[,h.blocks[[b]]])
  A <- cor(X[,(ncol(X)-length(v.blocks[[b]])+1):(ncol(X))],X[,1:length(h.blocks[[b]])]) # b.size + 1:b.size],X[,1:(b.size+1)
  if (b!=1) {
    A[1:min(b.size,length(v.blocks[[b]])),1:min(b.size,length(v.blocks[[b]]))][lower.tri(A[1:min(b.size,length(v.blocks[[b]])),1:min(b.size,length(v.blocks[[b]]))])] <- 0
    A[1:min(b.size-1,length(v.blocks[[b]])),max(1,ncol(A)-b.size+2):ncol(A)][upper.tri(A[1:min(b.size-1,length(v.blocks[[b]])),max(1,ncol(A)-b.size+2):ncol(A)],diag=T)] <- 0
  } else {
    A[!lower.tri(A,diag=T)]<-0
  }
  # detail : diag=F for last block, in upper.tri !
  A <- abs(A)
  Kappa[v.blocks[[b]]] <- apply(A,1,max)
}

Kappa <- sqrt(1-Kappa^{-1.31*log(alpha,base=10)})
sum(Kappa)
}

ReadGenstatSimulatedCross <- function(geno.file,map.file,pheno.file,rqtl.file,trait.name="test") {
# OBJECTIVE : read  a geno, map , and pheno file (typically the DH-simulation by Marcos, done in Genstat),
#             write it to a csvr file (see www.rqtl.org) import it in R-qtl
# INPUT :
# *
# *
# *
# OUTPUT :
# * a cross-object
  require(qtl)
  map             <- read.table(map.file,sep="\t")
  map[,1]         <- as.character(map[,1])
  geno            <- read.table(geno.file,header=T,sep="\t")
  con             <- file(description=pheno.file, open = "r")
  pheno           <- read.table(con,sep=",", nrows = 1) # ,colClasses=c(rep("character",n+2))
  pheno           <- read.table(con,sep=",", nrows = dim(geno)[1] + 5)
  close(con)
  #csvr format
  cat(trait.name,"","",pheno[,2],file=rqtl.file,append=F,sep=",")
  cat("\n",file=rqtl.file,append=T,sep="")
  write.table(cbind(map,t(geno[-(1:2),-1])),quote=F,na="",sep=",",append=T,file=rqtl.file,row.names=F,col.names=F)
  sim.a <- read.cross("csvr", dir=getwd(), rqtl.file,genotypes=c("1","2"))
sim.a
}

