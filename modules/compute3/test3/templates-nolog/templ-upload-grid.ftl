#upload result data:
echo -n "SUM_ADLER32_${output} \t" 
adler32 ${output} 

lcg-cr -l lfn://grid/${lfn_name} \
file:///${output}

