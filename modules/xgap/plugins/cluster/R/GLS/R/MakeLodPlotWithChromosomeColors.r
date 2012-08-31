MakeLodPlotWithChromosomeColors <- function(xvalues,yvalues,gwas.obj,file.name="",jpeg.plot=T,x.lab="base pairs",
                        y.lab="-10Log(p)",x.sig=integer(0),x.effects=integer(0),col.palette = c("royalblue","maroon","royalblue","maroon","royalblue"),
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

plot(x=xvalues,y=yvalues,xlab=x.lab,ylab=y.lab,type="n",lwd=0.4)
if (sum(chr.boundaries)!=0) {
  for (CHR in 1:gwas.obj$nchr) {
    lines(x=xvalues[gwas.obj$map$chromosome==CHR],y=yvalues[gwas.obj$map$chromosome==CHR],type="l",lwd=0.4,col=col.palette[CHR])
  }
  #for (chr.b in chr.boundaries) {
  #  lines(x=rep(chr.b,2),y=c(0,max(yvalues)),lwd=1)
  #}
} else {
  plot(x=xvalues,y=yvalues,xlab=x.lab,ylab=y.lab,type="l",lwd=0.4)
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
