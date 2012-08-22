MakeSnpBoxplot    <- function(data.vector,marker.vector,file.name="") {
# INPUT:
# data.vector   : phenotypic data
# marker.vector : marker data (assumed to consist of zeros and ones)
# OUTPUT
# 2 boxplots, for the genotypes with the 0 allele and for the genotypes with the 1 allele
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