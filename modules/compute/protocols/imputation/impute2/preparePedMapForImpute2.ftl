


mkdir ${plinkTempDir}

#Convert SNPids to chr_pos
awk '{$2=$1_$4; print $0}' ${plinkdata}.map > ${plinkdataPrepared}.map

#Copy ped file
cp ${plinkdata}.ped ${plinkdataPrepared}.ped

#Becarefull of phenotype field in ped files

