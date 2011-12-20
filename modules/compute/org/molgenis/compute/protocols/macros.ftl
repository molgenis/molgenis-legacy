<#macro begin>
<#--
Make sure this template is put at the _top_ of the generated scripts!
#!/bin/bash 
#PBS -N ${jobname}
#PBS -q ${queue}
#PBS -l nodes=1:ppn=${ppn}
#PBS -l mem=${memory}
#PBS -l walltime=${walltime}
-->
##### BEFORE #####
source ${importscript}
before="$(date +%s)"
echo "Begin job ${jobname} for ${fileprefix} at $(date)" >> ${runtimelog}

echo Running on node: `hostname`

###### MAIN ######
</#macro>


<#macro end >


###### AFTER ######
after="$(date +%s)"
elapsed_seconds="$(expr $after - $before)"
echo Completed ${jobname} for ${fileprefix} at $(date) in $elapsed_seconds seconds >> ${runtimelog}
######## END ########

</#macro>