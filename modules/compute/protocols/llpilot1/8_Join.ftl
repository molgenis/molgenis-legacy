#MOLGENIS walltime=00:45:00

inputs "${filehandleMissingString}"
inputs "${filehandleHeterozygosityString}"

join -1 "FID" -2 "FID" ${filehandleMissingString} ${filehandleHeterozygosityString} > ${filehandleJoinString}