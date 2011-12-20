#!/bin/bash
#PBS -q cluster
#PBS -l nodes=1:ppn=1
#PBS -l walltime=00:16:00
#PBS -l mem=10gb
#PBS -e blaat/err/err_martijnDemult6_demultiplex.err
#PBS -o blaat/out/out_martijnDemult6_demultiplex.out
mkdir -p blaat/err
mkdir -p blaat/out
printf "martijnDemult6_demultiplex_started " >>blaat/log_.txt
date "+DATE: %m/%d/%y%tTIME: %H:%M:%S" >>blaat/log_.txt
date "+start time: %m/%d/%y%t %H:%M:%S" >>blaat/extra/martijnDemult6_demultiplex.txt
echo running on node: `hostname` >>blaat/extra/martijnDemult6_demultiplex.txt

#INPUTS 
#OUTPUTS
#EXEC
#TARGETS lane, sequencer, sequencingStartDate, run, flowcell

flowcell: flowcell
sample: [testsample1]

# determine number of elements
15




printf "martijnDemult6_demultiplex_finished " >>blaat/log_.txt
date "+finish time: %m/%d/%y%t %H:%M:%S" >>blaat/extra/martijnDemult6_demultiplex.txt
date "+DATE: %m/%d/%y%tTIME: %H:%M:%S" >>blaat/log_.txt
