#download executable
lcg-cp lfn://grid/${srm_name} \
file:///$TMPDIR/${input}

echo -n "SUM_ADLER32_${input}" 2>&1 | tee -a ${log}
adler32 ${input} 2>&1 | tee -a ${log}

chmod 755 $TMPDIR/${input}

/bin/hostname

