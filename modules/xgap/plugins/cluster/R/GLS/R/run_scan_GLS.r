# open R, then type the following line:
# source("d:/willem/Dropbox/research/STATISTICAL_GENETICS/arabidopsis_project/version_1.1/run_scan_GLS.R")

####

# remove all existing R-objects
rm(list=ls())
gc()
# working directory, where all data files reside:
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/test_data/"  # 

# "script" directory, containing the R-scripts (recommended: equal to data.path)
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/arabidopsis_project/version_1.1/"

##########################################################################################
# CHOOSE THE DATA 
#
# R-image of the GWAS-object to be used
#r.image.name    <- "Rik001.RData"# "Ronny_001.RData" #"FT_reduced001.RData"#"Rik001.RData"#"myGwasData.RData" # "atwellData001with_pheno.RData"#"Rik001.RData"# "atwellData000.RData" "Ronny001.RData"#r.image.name    <- "johanna.RData" # r.image.name    <- "atwellData000.RData
#r.image.name    <- "myGWASdata.RData"#"Rik001.RData"
# the traits to be analyzed (column numbers in GWAS.obj$pheno)
#trait.numbers           <- 31#7:36#c(17,19:20,31) #3:57#7:34#7:36#2:3#c(2:8,24,37:46,70:72)#24#2:108

# Covariables:
# The second column (i.e. the first after the genotype column) may be included as covariate; set covariables to TRUE if this needs to be done
#covariables          <- F
#cov.cols             <- 2

######################################################################
# Indicate the method to compute significance (BT= bound type)
# Let V denote the number of false positives, and p the number of markers
#
# options:
# BT = 1 : Bonferroni : a LOD-threshold of -log10(alpha/p)
#                    This implies that E(V) = alpha under the strong null, for a value of alpha defined below 
#                    - alpha small, e.g. 0.05: the typical Bonferroni correction. We have P(V>0) <= E(V) = alpha, so the probability on at least one false discovery
#                      (type-I error) is at most alpha   
#                    - alternatively, you may choose alpha larger than one, and use that E(V) = alpha. 
#                      Note however that there is no (non-trivial) bound on $P(V>0)$ in this case  
# BT = 2 : Choose a value for the LOD-threshold yourself (specify the constant LOD.thr below). In this case E(V) = p*10^{-LOD.thr} 
# BT = 3 : Choose the LOD-threshold such that the snps with the K smallest p-values are selected (K specified below)
# BT = 4 : Bonferroni : as option BT=1, but with the number of markers (p) replaced by the number of effective tests approach, as in Gao et al (2008, 2010)
# BT = 5 : FDR : Bounds of the type E(V/R) <= alpha  under P_0  (using Benjamini-Hochberg-Yekutieli)
# BT = 6 : permutations (to be done)

#BT      <- 2
#alpha   <- 0.10
#LOD.thr <- 5.5
#K       <- 4

#################################################################
# Tasks to be performed, and output options
#
# The following suffix will be added to all summary and significant snp files : 
#suffix          <- "16march" 

# Before analyzing the traits in trait.numbers, do they need to be transformed ?
# Options:
# 0 (or any other value than 1 and 2) : do nothing (no transformation)
# 1 : normal quantile transformation, as in Guan and Stephens (2010)
# 2 : 10log ; If the minimum value in a trait is a <=0, then we first add (a+1) 
#transformation.type <- 0

# Which kinship matrix has to be used ? 
# Options: based on the full set of markers (1) or on a reduced set of 165 markers (2)
#kinship.type    <- 1
# If kinship.type = 2, specify the name of the csv-file containing this kinship matrix.
# This file should be in the folder data.path
#alternative.kinship.name <- "kinship_based_on_567_markers_correlations.csv" 
#alternative.kinship.name <- "kinship_based_on_3669_markers_residual_correlation_2pcas.csv" 
#alternative.kinship.name <- "kinship_based_on_3669_markers.csv"
#alternative.kinship.name <- "synbreed_kinship.csv"


#pca.as.cofactors <- F
#n.pca            <- 2 #ncol(GWAS.obj$pca)

# compute exact p-value for significant snps ?
#exact.tests     <- T

# Apply genomic.control correction of devlin and roeder (1999) ?
# If kinship.type = 2, this will be put to TRUE in any case
#genomic.control <- T

# If for the significant snps 'gene-wise' haplotype tests have to be performed using asreml, put haplo.tests to TRUE 
#haplo.tests     <- F

# interactions : look at the interactions between the snps that are significant  
#include.interactions <- F

# PLOTS: If jpeg.only is FALSE, also pdf plots are produces (which take much disk space)
#jpeg.only       <- F

###################################################################
#Haplotypes:
#
#minor.allele.frequency  <- 0.05
# Only haplotypes with frequency >= minor.allele.frequency are included; all other (rare) types are assigned to one 
# Recommendation : first run with minor.allele.frequency=0, to detect outliers 

######################################
# Now run scan.GLS...
#source(paste(script.path,"emmax_win.R",sep=""))
#KinshipTransform(GWAS.obj$kinship)

