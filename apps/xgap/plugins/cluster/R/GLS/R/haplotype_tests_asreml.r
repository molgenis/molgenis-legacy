#dir.create("haplotypes", showWarnings = F)
#library(asreml)
#library("MASS")

#n.tr                      <- ncol(GWAS.obj$pheno)
#GWAS.obj$pheno$genotype   <- as.factor(GWAS.obj$pheno$genotype)
#key                       <- match(GWAS.obj$pheno$genotype,GWAS.obj$plant.names)
#
#for (mg in unique(na.omit(as.character(t(as.matrix(data.frame(snp.sig$gene1,snp.sig$gene2))))))){ #unique(c(snp.sig$gene1,snp.sig$gene2)) 
  # mg = levels(snp.sig$gene)[45]; mg="AT2G25430"
#  maf <- minor.allele.frequency
#  haplo.output.file  <- paste(data.path,"haplotypes/",trait,".",mg,".",suffix,".txt",sep="")
#  cat("",append=FALSE,file=haplo.output.file)
  # Old code: 
  #snp.mg  <- sort(snp.sig$marker[snp.sig$gene1==mg | snp.sig$gene2==mg])
  # New :
#  snp.mg   <- row.names(GWAS.obj$markers)[(GWAS.obj$genes$first.marker[GWAS.obj$genes$gene.name==mg]):(GWAS.obj$genes$last.marker[GWAS.obj$genes$gene.name==mg])]
  #GWAS.obj$genes[GWAS.obj$genes$gene.name %in% unique(na.omit(as.character(t(as.matrix(data.frame(snp.sig$gene1,snp.sig$gene2)))))),]
 # if (length(snp.mg)==1) {  
  # if rare allele snps have not been taken out before, and the haplotype minor allele-frequency 
  # is set to a value which is larger than the snp allele-frequency, 
  # then reduce the haplotype minor allele-frequency to the snp minor allele-frequency
  #  snp.mean <- mean(as.numeric(GWAS.obj$markers[snp.mg,]))
   # if (snp.mean*(1-snp.mean) < maf*(1-maf)) {maf <- min(snp.mean,1-snp.mean)-0.00001 }
 # }
  #marker.frame=GWAS.obj$markers;snp.set=snp.mg;MAF=maf
#  haplo   <- FindHaplotypes(GWAS.obj$markers, snp.set=snp.mg,MAF=maf)
#  m       <- haplo[["nHap"]]
  # if there happens to be only one haplotype which is not rare, redo the preceding 2 lines, with maf=0
 # if(m<=1) {haplo   <- FindHaplotypes(GWAS.obj$markers, snp.set=snp.mg,MAF=0); m  <- haplo[["nHap"]]; maf <- 0}
 # hap.frequencies.in.the.hapmap  <- 100*tabulate(haplo$hapCodes)/length(haplo$hapCodes)
 # hap.frequencies.in.the.hapmap  <- hap.frequencies.in.the.hapmap[hap.frequencies.in.the.hapmap>0]
 # X       <- matrix(matrix(haplo[["hapIndex"]],ncol=m)[,-m],ncol=m-1)
 # X       <- cbind(X,matrix(haplo[["hapCodes"]],ncol=1))
 # X       <- as.data.frame(X[key,])
  # remove the individuals (not accessions!) which have X$allele.code==0 (i.e. that have a rare allele)
  # X$allele.code should be a factor!!
 # row.names(X)    <- row.names(GWAS.obj$pheno)
#  names(X)<- c(paste("allele.",seq(1,m-1),sep=""),"allele.code")
#  GWAS.obj$pheno   <- data.frame(GWAS.obj$pheno,X)
  #
 # if (ncol(cov.frame)==1) {  
 #   reml.formula1        <-  as.formula(paste(names(GWAS.obj$pheno)[tr.n],"~","allele.code"))
 # } else {
 #   reml.formula1        <-  as.formula(paste(paste(names(GWAS.obj$pheno)[tr.n],"~","allele.code + "),paste(names(GWAS.obj$pheno)[cov.cols],collapse="+")))
 # }                  
  #reml.formula1        <- as.formula(paste(names(GWAS.obj$pheno)[tr.n],"~","allele.code"))
 # individuals.with.common.haplotype <-GWAS.obj$pheno$genotype %in% GWAS.obj$plant.names[haplo$hapCodes!=0]
 # obj1                <- asreml(maxiter = 25,fixed= reml.formula1,data=GWAS.obj$pheno[individuals.with.common.haplotype,], random = ~ giv(genotype,var=T), ginverse = list(genotype=GWAS.obj$kinship.asreml))
  #obj2                <- asreml(data=GWAS.obj$pheno, random = ~ giv(genotype,var=T) + allele.code, ginverse = list(genotype=GWAS.obj$kinship.asreml))
 # GWAS.obj$pheno               <- GWAS.obj$pheno[,1:n.tr]
  #
  # write output file:
 # cat("Minor allele frequency : ",maf,append=TRUE,file=haplo.output.file)
  # estimates of the random effects : (gen. + res. variance)
 # cat("\n","Estimates of random effects: (genetic + residual variance)",append=TRUE,file=haplo.output.file)
 # cat("\n",summary(obj1)$varcomp$component,append=TRUE,file=haplo.output.file)
  # estimates of the fixed effects :
 # cat("\n","\n","Estimates of the allele (fixed) effect",append=TRUE,file=haplo.output.file)
 # cat("\n",obj1$coefficients$fixed["allele.code"],append=TRUE,file=haplo.output.file)
  # wald test for fixed effects:
 # cat("\n","\n","p-value for wald's test for the allele effect","\n",(wald(obj1))["allele.code","Pr(Chisq)"],append=TRUE,file=haplo.output.file)
  #cat("\n","\n",wald(obj1)[1:m,],append=TRUE,file=haplo.output.file) # error!
  # overview of the haplotypes:
 # cat("\n","\n","Overview of the haplotypes :","\n",append=TRUE,file=haplo.output.file)
 # suppressWarnings(write.table(cbind(haplo[["hapPool"]],hap.frequencies.in.the.hapmap),append=TRUE,file=haplo.output.file,quote=F))
  # make boxplots:
 # hcode   <- (haplo[["hapCodes"]])
  #make.haplo.boxplot(data.vector=GWAS.obj$pheno[individuals.with.common.haplotype,tr.n],hap.vector=(hcode[key])[ individuals.with.common.haplotype],file.name=paste(data.path,"boxplots/",trait,".",mg,".",Kmethod,".",suffix,".jpeg",sep=""))
  #make.haplo.boxplot(data.vector=GWAS.obj$pheno[!is.na(GWAS.obj$pheno[,tr.n]),tr.n],hap.vector=FindHaplotypes(GWAS.obj$markers, snp.set=snp.mg,MAF=0)[["hapCodes"]][key][!is.na(GWAS.obj$pheno[,tr.n])],file.name=paste(data.path,"haplotypes/",trait,".",mg,".",suffix,".jpeg",sep=""))
#}
