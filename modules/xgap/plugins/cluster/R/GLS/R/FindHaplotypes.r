# For an extended version including a training and validation set, see the function define.haplotypes in the SHARE3 script 
FindHaplotypes <- function(marker.frame,snp.set=1:nrow(marker.frame),
                           ind.set=1:ncol(marker.frame),MAF=0.01,classification.constant=1) {
    # marker.frame is assumed to be a data-frame or matrix with the snps in the rows and individuals in the columns
    # The species is assumed to be homozygote (this function is an adapted version of the more general one in the haplo.stats package,
    # which also considers heterozygozity)
    phased.snp.data <- t(marker.frame[snp.set,ind.set])
    nr          <- nrow(phased.snp.data)
    hapSeq      <- apply(phased.snp.data, 1, function(x) {paste(x, sep = "", collapse = "-")})
    uniHap      <- unique(hapSeq)
    nHap        <- length(uniHap)
    hapNum      <- as.character(1:nHap) # N.B. This line replaces the following line from the original code (from the haplo.stats package) # ok now??? 
    #hapNum      <- unlist(sapply(1:nHap, function(x) {paste(paste(rep("0", ceiling(log10(nHap)) - nchar(as.character(x))),collapse = ""), x, sep = "",collapse = "")}))
    names(uniHap) <- paste("hap.", hapNum, sep = "")
    hapPool     <- strsplit(uniHap, "-")
    hapPool     <- data.frame(t(data.frame(lapply(hapPool, function(x) {as.numeric(x)}))))
    colnames(hapPool) <- colnames(phased.snp.data)
    hapCount    <- sapply(uniHap, function(x) {sum(x == hapSeq)})
    hapFreq     <- hapCount/sum(hapCount)
    noNameUniHap <- uniHap
    names(noNameUniHap) <- NULL
    hapIndex    <- sapply(hapSeq, function(x) {which(x == noNameUniHap)})
    hapIndex2   <- hapIndex 
    # Now define respectively haplotypes which  
    # 3) the subset of tr.types that has minimal (MAF) frequency (within tr.types, not the whole sample)
    # 4) the subset of tr.types that is below this frequency
    common.types<- sort((1:nHap)[(tabulate(hapIndex,nbins=nHap)/nr)>MAF])     
    rare.types  <- sort((1:nHap)[(tabulate(hapIndex,nbins=nHap)/nr)<=MAF])
    hapIndex2[hapIndex %in% rare.types] <- 0
    if ((length(rare.types))>0) {
        new.hapPool     <- hapPool[common.types,]
        new.nHap        <- nrow(new.hapPool)
        new.hapIndex    <- matrix(0,nr,length(common.types))
        common.individuals<- which(hapIndex %in% common.types)
        n.c             <- length(common.individuals)# as.numeric(as.factor(... : this is to rename the common types
        new.hapIndex[matrix(c(common.individuals, as.numeric(as.factor(hapIndex[common.individuals]))), ncol=2)]   <- 1
        for (h in rare.types) {
            qw          <- hapPool[h,] 
            differences <- apply(hapPool[common.types,],1,FUN=function(x) {sum(x!=qw)})
            weights     <- exp(-classification.constant*differences)
            weights     <- weights/sum(weights)
            new.hapIndex[which(hapIndex==h),]<- matrix(rep(weights,sum((hapIndex==h))),byrow=TRUE,ncol=length(weights))
        } 
    hapPool     <- new.hapPool
    hapIndex     <- new.hapIndex
    nHap        <- new.nHap
    } else {
        new.hapIndex    <- matrix(0,nr,length(common.types))
        common.individuals<- which(hapIndex %in% common.types)
        n.c             <- length(common.individuals)# as.numeric(as.factor(... : this is to rename the common types
        new.hapIndex[matrix(c(common.individuals, as.numeric(as.factor(hapIndex[common.individuals]))), ncol=2)]   <- 1
        hapIndex     <- new.hapIndex
    }
list(hapIndex=hapIndex,nHap=nHap,hapPool=hapPool,hapCodes=hapIndex2)
}