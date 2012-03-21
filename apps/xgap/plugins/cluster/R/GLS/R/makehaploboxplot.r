MakeHaploBoxplot    <- function(data.vector,hap.vector,file.name="") {
  plot.data   <- data.frame(trait.value=as.numeric(data.vector),hap.vector=as.integer(hap.vector)) 
  if (file.name=="") {
    boxplot(trait.value ~ hap.vector,data=plot.data,names=as.character(tabulate(hap.vector)))
  } else {
    jpeg(file.name,quality=100)
    boxplot(trait.value ~ hap.vector,data=plot.data,names=as.character(tabulate(hap.vector)))
    dev.off()
  }
}