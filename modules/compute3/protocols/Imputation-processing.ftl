#MOLGENIS walltime=23:00:00 nodes=1 cores=1 mem=8 clusterQueue=nodes interpreter=bash

#INPUTS input_1,input_2,input_3,input_4,sample_g,exclude_samples_g
#OUTPUTS output_1
#LOGS log
#EXES path_to_impute2
#TARGETS


echo "Before execution" 2>&1 | tee -a ${log}

${path_to_impute2} \
-m ${input_4} \
-h ${input_2} \
-l ${input_3} \
-g ${input_1} \
-sample_g ${sample_g} \
-exclude_samples_g ${exclude_samples_g} \
-int ${start_pos} ${end_pos} \
-Ne 20000 \
-o ${output_1} 2>&1 | tee -a ${log}

echo "After execution" 2>&1 | tee -a ${log}

echo -n "SNP1:" 2>&1 | tee -a ${log}
wc -l ${output_1}  2>&1 | tee -a ${log}





