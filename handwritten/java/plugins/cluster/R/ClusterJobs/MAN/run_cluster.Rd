\name{run_cluster}
\alias{run_cluster}
\title{ run_cluster - Main routine }
\description{
	run_cluster
}
\usage{
	run_cluster <- function(name, genotypes , phenotypes, totalitems, njobs, dbpath, taskid, ...)
}

\arguments{
  \item{name}{ Integer to add up with b }
  \item{genotypes}{ ID of genotype matrix }
  \item{phenotypes}{ ID of phenotype matrix }
  \item{totalitems}{ number of items in total }
  \item{njobs}{ number of njobs the user wants to use }
  \item{dbpath}{ URL location of the database }
  \item{taskid}{ ID of the task to report to }
  \item{\dots{...}}{ Parameters passed to the scripts}
}
\details{
	Generates a folder
	Generates R and SH-files for use with PBS
	Submission to PBS
}
\value{
	No return value
}
\references{ 
	No refs yet
}
\author{ 
	Danny Arends \email{Danny.Arends@gmail.com}
	Joeri v/d Velde
	Maintainer: Danny Arends \email{Danny.Arends@gmail.com} 
}
