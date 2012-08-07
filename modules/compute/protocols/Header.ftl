#!/bin/bash
#PBS -N ${jobname}
#PBS -q ${clusterQueue}
#PBS -l nodes=1:ppn=${cores}
#PBS -l walltime=${walltime}
#PBS -l mem=${mem}
#PBS -e ${jobname}.err
#PBS -o ${jobname}.out
#PBS -W umask=0007

# Configures the GCC bash environment
. ${root}/gcc.bashrc

<#include "Macros.ftl"/>
<@begin/>
<#include "NGSHeader.ftl"/>
<#if defaultInterpreter = "R"><@Rbegin/></#if>