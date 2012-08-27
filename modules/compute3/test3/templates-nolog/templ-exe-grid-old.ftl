#download executable
lcg-cp lfn://grid/${lfn_name} \
file:///${input}

echo -n "SUM_ADLER32_${input} \t" 
adler32 ${input} 

chmod 755 ${input}

/bin/hostname

