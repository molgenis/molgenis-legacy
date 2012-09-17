#!/bin/bash
#PBS -N ${jobname}
#PBS -q ${clusterQueue}
#PBS -l nodes=1:ppn=${cores}
#PBS -l walltime=${walltime}
#PBS -l mem=${mem}
#PBS -e ${jobname}.err
#PBS -o ${jobname}.out

<#include "Macros.ftl"/>
<@begin/>
<#include "NGSHeader.ftl"/>
<#if defaultInterpreter = "R"><@Rbegin/></#if>