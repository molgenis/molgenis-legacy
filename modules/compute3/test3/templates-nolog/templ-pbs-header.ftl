#PBS -N ${jobname}
#PBS -q ${clusterQueue}
#PBS -l nodes=1:ppn=${cores}
#PBS -l walltime=${walltime}
#PBS -l mem=${mem}
#PBS -e ${jobname}.err
#PBS -o ${jobname}.out

##### BEFORE #####
touch $PBS_O_WORKDIR/${jobname}.out
#source {importscript}
before="$(date +%s)"
echo "Begin job ${jobname} at $(date)" >> $PBS_O_WORKDIR/RUNTIME.log

echo Running on node: `hostname`

###### MAIN ######
