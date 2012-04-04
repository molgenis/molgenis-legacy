NormalQuantileTransform <- function(y) {
# the normal quantile transform as in Guan and Stephens (2010)
qqnorm(y,plot.it=F)$x
}