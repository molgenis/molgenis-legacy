


mkdir ${studyPedMapDir}

#Convert SNPids to chr_pos
awk '{$2=$1_$4; print $0}' ${plinkdata}.map > ${studyPedMap}.map

#Copy ped file
cp ${plinkdata}.ped studyPedMapDir.ped

#Becarefull of phenotype field in ped files. Might crash later on -9

