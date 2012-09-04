#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4


#INPUTS studyInputPedMap.map,studyInputPedMap.ped
#OUTPUTS studyPedMap.map,studyPedMap.ped
#EXES
#LOGS log
#TARGETS plinkdata

inputs ${studyInputPedMap}.map
inputs ${studyInputPedMap}.ped
alloutputsexist ${studyPedMap}.map
alloutputsexist ${studyPedMap}.ped

mkdir ${studyPedMapDir}

#Convert SNPids to chr_pos
awk '{$2=$1_$4; print $0}' ${studyInputPedMap}.map > ${studyPedMap}.map

#Copy ped file
cp ${studyInputPedMap}.ped ${studyPedMap}.ped

#Becarefull of phenotype field in ped files. Might crash later on -9

