#setwd(data.path)
# Create the subdirectory structure. If it already exists, nothing is done.  
#dir.create("results", showWarnings = F)
#dir.create("plots", showWarnings = F)
#dir.create("boxplots", showWarnings = F)
#dir.create("output", showWarnings = F)


####################################################################################################################################################
# load functions and data:

# load the GWAS-object (contained in r.image.name) and load functions.R
#load(r.image.name)
#source(paste(script.path,"functions.R",sep=""))

# install and load R-packages asreml and multtest (asreml needs to be installed from a zip file)

#if (!is.installed("asreml")) {cat("ERROR: first install asreml","\n")}
#library(asreml)

#if (BT==5) {
##  if (!is.installed("multtest")) {
#    source("http://www.bioconductor.org/biocLite.R")
#    biocLite("multtest")
#  }
#  library(multtest)
#}

#if (kinship.type==2) {
#  genomic.control <- T
#  GWAS.obj$external$kinship.name <- alternative.kinship.name
#  GWAS.obj$kinship               <- read.table(file=GWAS.obj$external$kinship.name,sep=",",header=T)
#  GWAS.obj$external$kinship.name <- paste("temp_",alternative.kinship.name,sep="")
#  row.names(GWAS.obj$kinship)    <- names(GWAS.obj$kinship)
#  GWAS.obj$kinship               <- as.matrix(GWAS.obj$kinship)
#} else {
#  # write kinship-matrix to file:
#  write.table(GWAS.obj$kinship,file=GWAS.obj$external$kinship.name,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)
#}

#GWAS.obj$kinship        <- 2*GWAS.obj$kinship
#GWAS.obj$kinship.asreml <- MakeKinshipAsreml(GWAS.obj$kinship,genotype.names=GWAS.obj$plant.names)
#write.table(GWAS.obj$kinship,file=GWAS.obj$external$kinship.name,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)

################################################################
####### covariates

# if no covariates are to be used, cov.frame will be an dataframe with one column ("mu": vector of ones). 
# Otherwise, it will be a dataframe with ones in the first
# column, and the actual covariates in subsequent columns 

#if (pca.as.cofactors) {
#  n.pca          <- min(n.pca,ncol(GWAS.obj$pca))
#  GWAS.obj$pheno <- cbind(GWAS.obj$pheno,GWAS.obj$pca[as.character(GWAS.obj$pheno$genotype),])
#  covariables    <- T
#  cov.cols       <- c(cov.cols, (ncol(GWAS.obj$pheno)-n.pca+1):(ncol(GWAS.obj$pheno)))  
#  }

#if (!covariables) {
#  cov.cols  <- integer(0)
#  covariate.file  <- ""
#} else { 
#  covariate.file  <- paste(data.path,"output/covariates.txt",sep="")
#}
#cov.frame       <- MakeCovariateFile(pheno.dataframe=GWAS.obj$pheno,cov.cols=cov.cols,file.name=covariate.file)

###############################################################
                  
#for (tr.n in trait.numbers) {
#
#if (transformation.type==1) {GWAS.obj$pheno[,tr.n] <- NormalQuantileTransform(GWAS.obj$pheno[,tr.n])}

#if (transformation.type==2) {
#  if (min(GWAS.obj$pheno[,tr.n],na.rm=T)<=0) {GWAS.obj$pheno[,tr.n] <-   GWAS.obj$pheno[,tr.n]  -  min(GWAS.obj$pheno[,tr.n],na.rm=T) + 1}
#  GWAS.obj$pheno[,tr.n] <- log10(GWAS.obj$pheno[,tr.n])
#}

#no.significant.snps   <- FALSE
#
# the name of the trait, which will be the basis of many file names:
#trait       <- names(GWAS.obj$pheno)[tr.n]  

# Create the summary file:

# name of the file that will contain the significant snps : 
#summ.file    <- paste(data.path,"results/","summary.",trait,suffix,".txt",sep="") 
#cat("Trait: ",trait,"\n","\n",file=summ.file)    # file is created and first line is written; APPEND=FALSE (default)

#varcomp.file        <- paste(data.path,"output/",trait,".","varcomp",".csv",sep="")
#if (sum(cov.cols)!=0) {
#  reml.formula        <- as.formula(paste(paste(names(GWAS.obj$pheno)[tr.n],"~"),paste(names(GWAS.obj$pheno)[cov.cols],collapse="+")))
#} else {
#  reml.formula        <- as.formula(paste(names(GWAS.obj$pheno)[tr.n],"~ 1"))
#}    


#cat("Analysis started on: ",date(),"\n",file=summ.file,append=TRUE)
# Name of the phenotype-file for scan.GLS:
#input.pheno <- paste(data.path,"output/",trait,".csv",sep="")
#MakePhenoFile(pheno.object=GWAS.obj$pheno,col.number=tr.n,file.name=input.pheno)
#
# Run asreml and scan.GLS, genome-wide, using the input, output, pheno and kinship files defined above
#reml.obj            <- asreml(maxiter = 25,fixed= reml.formula,data=GWAS.obj$pheno, random = ~ giv(genotype,var=T),na.method.X="omit",ginverse = list(genotype=GWAS.obj$kinship.asreml))

#varcomp.values      <- data.frame(var.comp.values=summary(reml.obj)$varcomp$component)
#MakeVarcompFile(var.comp.values=varcomp.values,file.name=varcomp.file)
# Name of the scan.GLS output-file
#output.file         <- paste(data.path,"results/",trait,".","output",suffix,".txt",sep="")
#GWA.result          <- scan.GLS(gwas.obj=GWAS.obj,input.pheno=input.pheno,varcomp.file=varcomp.file,output.file=output.file,covariate.file=covariate.file)

####################################################################################### GWAS.obj$map$chromosome

#if (genomic.control) {
#  GC <- GenomicControlPvalues(pvals=GWA.result$pvalue,n.obs=sum(!is.na(GWAS.obj$pheno[,tr.n])),n.cov=sum(cov.cols))
#  GWA.result$pvalue <- GC[[1]]
#  inflation.factor  <- GC[[2]]
#}

#############################################################

# When BT is 1,2,3 or 4, determine the LOD-threshold
#if (BT==1) {LOD.thr <- -log10(alpha/GWAS.obj$N)} 
#if (BT==2) {LOD.thr <- -log10(1/GWAS.obj$N)}
#if (BT==3) {LOD.thr <- sort(-log10(GWA.result$pvalue),decreasing=T)[K]}

#if (BT==4) {
#  cut.off <- 0.995
#  number.of.nonmissing  <- aggregate(GWAS.obj$pheno[,tr.n],by=list(ordered(GWAS.obj$pheno$genotype)),FUN=function(x){sum(!is.na(x))})[match(GWAS.obj$plant.names,sort(GWAS.obj$plant.names)),2]
#  number.of.nonmissing[number.of.nonmissing>0] <- 1
#  ind.indices <- rep((1:GWAS.obj$n)[number.of.nonmissing>0],times=number.of.nonmissing[number.of.nonmissing>0])
#  SIGMA       <- varcomp.values[1,] * GWAS.obj$kinship[ind.indices,ind.indices] + varcomp.values[2,] * diag(sum(number.of.nonmissing))
#  COR         <- cov2cor(SIGMA)
#  INV.COR     <- GINV(COR)
#  Keff      <- 0
#  n.block   <- 0
#  b.size    <- 10*sum(number.of.nonmissing)
#  for (CHR in 1:GWAS.obj$nchr) {
#    blocks  <- DefineBlocks(which(GWAS.obj$map$chromosome==CHR),block.size=b.size)
#    n.block <- n.block  +  length(blocks)
#    for (b in 1:length(blocks)) {
#      marker.frame <- GWAS.obj$markers[blocks[[b]],]
#      Keff <- Keff + GaoCorrection(marker.frame=marker.frame,number.of.replicates=number.of.nonmissing,inv.cor.matrix=INV.COR,cut.off=cut.off)
#    }
#  }
#  LOD.thr <- -log10(alpha/Keff)
#}

#######################################################################

#
#if (BT %in% 1:4) {
#  snp.selection         <- (-log(GWA.result$pvalue,base=10) >= LOD.thr)
#}

#if (BT==5) {
#  qw <- mt.rawp2adjp(rawp=GWA.result$pvalue,proc="BY")
#  adj<- qw$adjp[order(qw$index),]
#  snp.selection   <- (adj[,2] < alpha)
#}

#  if (sum(snp.selection,na.rm=T)>0) {
#    snp.sig               <- data.frame(marker=row.names(GWAS.obj$markers)[snp.selection],chromosome=GWAS.obj$map$chromosome[snp.selection],position=GWAS.obj$map$position[snp.selection],pvalue=GWA.result$pvalue[snp.selection],gene1=GWAS.obj$map$gene1[snp.selection],gene2=GWAS.obj$map$gene2[snp.selection])
#    number.of.significant.snps  <- nrow(snp.sig)
#    snp.sig$marker        <- as.character(snp.sig$marker)
#    no.significant.snps   <- FALSE
    # name of the file that will contain the significant snps : 
#    snp.output.file       <- paste(data.path,"results/","significant.snps.",trait,suffix,".csv",sep="")
#    allele.frequencies    <- apply(GWAS.obj$markers[snp.sig$marker,unique(GWAS.obj$pheno$genotype[!is.na(GWAS.obj$pheno[,tr.n])])],1,mean)
#    effect.sizes          <- GWA.result$stat[snp.selection] #rep(NA,sum(snp.selection))
    #
#    if (exact.tests) {
#      emma.pvalues          <- rep(NA,sum(snp.selection))
#      if (sum(cov.cols)!=0) {
#        reml.formula.emma   <- as.formula(paste("phenotype ~ marker +",paste(names(GWAS.obj$pheno)[cov.cols],collapse="+")))
 #     } else {
#         reml.formula.emma  <- as.formula("phenotype ~ marker")
 #     }
      #var.comp.values=summary(reml.obj)$varcomp$component
      # TO DO : F-test !!    
#      for (snp.name in snp.sig$marker) {
#        emma.frame   <- data.frame(genotype=GWAS.obj$pheno$genotype,phenotype=GWAS.obj$pheno[,tr.n],marker=as.numeric(GWAS.obj$markers[snp.name,as.character(GWAS.obj$pheno$genotype)]))
#        if (sum(cov.cols)!=0) {
#          emma.frame   <- cbind(emma.frame,GWAS.obj$pheno[,cov.cols])
#          names(emma.frame)[-(1:3)] <- names(GWAS.obj$pheno)[cov.cols]
#          }
#        reml.obj.emma <- asreml(fixed= reml.formula.emma,data=emma.frame, random = ~ giv(genotype,var=F),ginverse = list(genotype=GWAS.obj$kinship.asreml),na.method.X="omit")
#        emma.pvalues[which(snp.name==snp.sig$marker)] <- (wald(reml.obj.emma))[[4]][2]
        ##wald.formula <- paste("~ marker | genotype +",paste(names(GWAS.obj$pheno)[cov.cols],collapse="+"))
#      }
#    }
    #
#    explained.variances   <- 2 * effect.sizes^2 * allele.frequencies * (1 - allele.frequencies)
    #effect.sizes[which(snp.name==snp.sig$marker)] <- CalculateEffect(gwas.obj=GWAS.obj,snp.name=snp.name,trait.number=tr.n)
    #explained.variances   <- 100* explained.variances / Heritability(data.vector=GWAS.obj$pheno[,tr.n],geno.vector=GWAS.obj$pheno$genotype)[[2]]
#    explained.variances   <- 100* explained.variances / varcomp.values[1,1]
#    if (exact.tests) {
#      snp.sig             <- cbind(snp.sig,data.frame(col0.allele.freq=allele.frequencies,effect.size=effect.sizes,perc.of.genetic.var=explained.variances,exact.pvalue=emma.pvalues))
#    } else {
#      snp.sig             <- cbind(snp.sig,data.frame(col0.allele.freq=allele.frequencies,effect.size=effect.sizes,perc.of.genetic.var=explained.variances))
#    }
    #snp.sig$pvalue/snp.sig$exact.pvalue
    # creating this file
#    write.table(snp.sig,file=snp.output.file,quote=FALSE,row.names=FALSE,sep=",")
    # Doing haplotype-tests on the genes these SNPs are on
#    if (haplo.tests) {source(paste(script.path,"haplotype_tests_asreml.R",sep=""))}                                             
#  } else {
#    no.significant.snps         <- TRUE
#    number.of.significant.snps  <- 0
#  }

############################################################################
# interactions

#if (include.interactions & !no.significant.snps) {
#  dir.create("interactions", showWarnings = F)
#  best.snps           <-  (1:nrow(GWA.result))[snp.selection]
#  H                   <- length(best.snps)
#  N.inter             <- H*(H-1)/2
#  vec1i               <- rep(1:(H-1),times=(H-1):1)
#  interaction.matrix  <- matrix(rep(H:1,H),ncol=H)
#  vec2i               <- interaction.matrix[upper.tri(interaction.matrix,diag=F)]
#  vec2i               <- vec2i[(length(vec2i)):1]
  #
#  interactions        <- data.frame(cbind(matrix(c(vec1i,vec2i),ncol=2),matrix(0,N.inter,2)))
#  names(interactions) <- c("snp1","snp2","pvalue","epistatic.effect")
#  interactions$snp1   <- best.snps[interactions$snp1]
#  interactions$snp2   <- best.snps[interactions$snp2]
#  row.names(interactions) <- paste(row.names(GWAS.obj$markers)[interactions$snp1],".x.",row.names(GWAS.obj$markers)[interactions$snp2],sep="")
  #
#  cov.frame.interaction <- cov.frame
#  if (ncol(cov.frame.interaction)>1) {cov.frame.interaction  <- data.frame(cov.frame.interaction[,-1]); names(cov.frame.interaction)<- names(cov.frame)[-1]}
  #
#  reml.obj2           <- asreml(maxiter = 25,fixed= reml.formula,data=GWAS.obj$pheno,na.method.X="omit", random = ~ giv(genotype,var=T),start.values=T,ginverse = list(genotype=GWAS.obj$kinship.asreml))
#  iv                  <- reml.obj2$gammas.table
#  iv$Value            <- unlist(varcomp.values)
#  ep=N.inter
#  geno.vector=as.character(GWAS.obj$pheno$genotype);marker1=as.numeric(GWAS.obj$markers[interactions$snp1[ep],as.character(GWAS.obj$pheno$genotype)]);marker2=as.numeric(GWAS.obj$markers[interactions$snp2[ep],as.character(GWAS.obj$pheno$genotype)]);trait.values=GWAS.obj$pheno[,tr.n];cov.values=cov.frame.interaction;kinship.asreml.object=GWAS.obj$kinship.asreml  
  #write.table(data.frame(m1=as.numeric(GWAS.obj$markers[interactions$snp1[ep],]),m2=as.numeric(GWAS.obj$markers[interactions$snp2[ep],])),file="test.inter2.csv",sep=",")
  #write.table(interaction.frame,file="test.inter.csv",sep=",")
#  for (ep in 1:N.inter) {
#    interactions[ep,3:4]  <- EstimateInteraction(geno.vector=as.character(GWAS.obj$pheno$genotype),marker1=as.numeric(GWAS.obj$markers[interactions$snp1[ep],as.character(GWAS.obj$pheno$genotype)]),marker2=as.numeric(GWAS.obj$markers[interactions$snp2[ep],as.character(GWAS.obj$pheno$genotype)]),trait.values=GWAS.obj$pheno[,tr.n],cov.values=cov.frame.interaction,kinship.asreml.object=GWAS.obj$kinship.asreml)
#  }
#  write.table(interactions,file=paste(data.path,"interactions/",trait,".",suffix,".csv",sep=""),sep=",",quote=F)
#}

    

############################################################################

# continue with the summary file:
#cat("Analysis finished on: ",date(),"\n","\n",file=summ.file,append=TRUE)
# extra factor (new) : KinshipTransform(GWAS.obj$kinship)

#non.missing     <- unique(GWAS.obj$pheno$genotype[!is.na(GWAS.obj$pheno[,tr.n])])
#kinship.reduced <- GWAS.obj$kinship[non.missing,non.missing] 
#GWAS.obj$pheno[is.na(GWAS.obj$pheno[,31]),31] <- mean(GWAS.obj$pheno[,31],na.rm=T)
#varcomp.values[1,1] <- varcomp.values[1,1]*KinshipTransform(kinship.reduced)
#varcomp.values[2,1] <- varcomp.values[2,1]#/mean(table(GWAS.obj$pheno$genotype[!is.na(GWAS.obj$pheno[,tr.n])]))
#raw.h2 <- Heritability(data.vector=GWAS.obj$pheno[,tr.n],geno.vector=GWAS.obj$pheno$genotype)
#
#cat("Raw data:","\n",file=summ.file,append=TRUE) 
#cat("Genetic variance: ",raw.h2[[2]],"\n",file=summ.file,append=TRUE) 
#cat("Residual variance: ",raw.h2[[3]],"\n",file=summ.file,append=TRUE)
#cat("Heritability: ",raw.h2[[1]],"\n","\n",file=summ.file,append=TRUE)
#
#cat("Mixed model with only polygenic effects, and no marker effects:","\n",file=summ.file,append=TRUE) 
#cat("Genetic variance: ",varcomp.values[1,1],"\n",file=summ.file,append=TRUE) 
#cat("Residual variance: ",varcomp.values[2,1],"\n",file=summ.file,append=TRUE)
#cat("Heritability: ",(varcomp.values[1,1])/(sum(varcomp.values[,1])),"\n","\n",file=summ.file,append=TRUE)
#
#cat("File containing the p-values of all snps:  ",output.file,"\n","\n",append=TRUE,file=summ.file,sep="")
#

#if (BT %in% 1:4) {
#    cat("LOD-threshold: ",LOD.thr,"\n",append=TRUE,file=summ.file) 
#    if (BT==4) {cat("Number of effective tests: ",Keff,"\n",append=TRUE,file=summ.file)}
    #
#    if (!no.significant.snps) {
#        cat("File containing the p-values of the selected snps: ",snp.output.file,"\n",append=TRUE,file=summ.file)
#        cat("Number of selected snps =",nrow(snp.sig),"\n",append=TRUE,file=summ.file)
#        cat("Smallest p-value among the selected snps:",min(snp.sig$pvalue),"\n",append=TRUE,file=summ.file)
#        cat("Largest  p-value among the selected snps:",max(snp.sig$pvalue),"(LOD-score:",-log10(max(snp.sig$pvalue)),")","\n",append=TRUE,file=summ.file)
#    } else {
#        cat("No significant snps found.","\n",append=TRUE,file=summ.file)
#    }
#    if (genomic.control) {cat("Genomimc control: inflation-factor = ",inflation.factor,"\n",append=TRUE,file=summ.file)}
#}

#if (BT==5) {
#    cat("Requiring that the FDR = E(V/R) <= 0.05",":","\n",append=TRUE,file=summ.file)
#    cat("(using the Benjamini-Hochberg-Yekutieli method)","\n","\n",append=TRUE,file=summ.file)
#    #
#    if (!no.significant.snps) {
#        cat("File containing the p-values of the selected snps: ",snp.output.file,"\n",append=TRUE,file=summ.file)
#        cat("Number of selected snps =",nrow(snp.sig),"\n",append=TRUE,file=summ.file)
#        cat("Smallest p-value among the selected snps:",min(snp.sig$pvalue),"\n",append=TRUE,file=summ.file)
#        cat("Largest  p-value among the selected snps:",max(snp.sig$pvalue),"(LOD-score:",-log10(max(snp.sig$pvalue)),")","\n",append=TRUE,file=summ.file)
#    } else {
#        cat("No significant snps found.",append=TRUE,file=summ.file)
#    }
    #
#}

# Make a plot of the LOD-profile: (pdf and jpeg)
#LOD.thr.plot  <- 0
#if (BT %in% 1:4) {LOD.thr.plot <- LOD.thr}
#if (BT == 5) {if (!no.significant.snps) {LOD.thr.plot <- min(-log10(GWA.result$pvalue[snp.selection]))}}
#
#x.effects=integer(0) 
#effects.size=numeric(0)
#if (nrow(GWAS.obj$real.effects$locations)>0) {
#  x.effects=GWAS.obj$real.effects$locations[,tr.n-1] 
#  effects.size=GWAS.obj$real.effects$sizes[,tr.n-1]
#}
#

#MakeLodPlotWithChromosomeColors(xvalues=GWAS.obj$map$cum.position,yvalues=-log10(GWA.result$pvalue),gwas.obj=GWAS.obj,col.palette = c("royalblue","maroon","royalblue","maroon","royalblue"),file.name=paste(data.path,"plots/",trait,".",suffix,".jpeg",sep=""),x.sig=which(snp.selection),chr.boundaries=cumsum(GWAS.obj$chr.lengths.bp)[-1],y.thr=LOD.thr.plot,x.effects=x.effects,effects.size=effects.size)
#MakeLodPlot(xvalues=GWAS.obj$map$cum.position,yvalues=-log10(GWA.result$pvalue),file.name=paste(data.path,"plots/",trait,".",suffix,".jpeg",sep=""),x.sig=which(snp.selection),chr.boundaries=cumsum(GWAS.obj$chr.lengths.bp)[-1],y.thr=LOD.thr.plot,x.effects=x.effects,effects.size=effects.size)
            
#if (!jpeg.only) {MakeLodPlotWithChromosomeColors(xvalues=GWAS.obj$map$cum.position,yvalues=-log10(GWA.result$pvalue),gwas.obj=GWAS.obj,col.palette = c("royalblue","maroon","royalblue","maroon","royalblue"),file.name=paste(data.path,"plots/",trait,".",suffix,".pdf",sep=""),x.sig=which(snp.selection),chr.boundaries=cumsum(GWAS.obj$chr.lengths.bp)[-1],y.thr=LOD.thr.plot,x.effects=x.effects,effects.size=effects.size,jpeg.plot=FALSE)}

  
#}   # end for (tr.n in trait.numbers)

################################
