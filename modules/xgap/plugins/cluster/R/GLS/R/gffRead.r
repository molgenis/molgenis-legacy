#
# gffRead.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

getAttributeField <- function (x, field, attrsep = ";") {
  if(missing(x)) stop("argument 'x' is missing, with no default")
  if(missing(field)) stop("argument 'field' is missing, with no default")
    
  s = strsplit(x, split = attrsep, fixed = TRUE)
  sapply(s, function(atts){
    a = strsplit(atts, split = "=", fixed = TRUE)
    m = match(field, sapply(a, "[", 1))
    if(!is.na(m)){
      rv = a[[m]][2]
    }else{
      rv = as.character(NA)
    }
    return(rv)
  })
}

gffRead <- function(gffFile, nrows = -1) {
  if(missing(gffFile)) stop("argument 'gffFile' is missing, with no default")

  cat("Reading ", gffFile, ": ", sep="")
  gff = read.table(gffFile, sep="\t", as.is=TRUE, quote="",
  header=FALSE, comment.char="#", nrows = nrows,
  colClasses=c("character", "character", "character", "integer", "integer","character", "character", "character", "character"))
  colnames(gff) = c("seqname", "source", "feature", "start", "end", "score", "strand", "frame", "attributes")
  cat("found", nrow(gff), "rows with classes:", paste(sapply(gff, class), collapse=", "), "\n")
  stopifnot(!any(is.na(gff$start)), !any(is.na(gff$end)))
  return(gff)
}
