#!/bin/bash

getdata()
{
CLUSTER="cluster";
GRID="grid";

INPUT="input";
OUTPUT="output";

ARGS=($@)
BACKEND="${ARGS[0]}";
OPERATION="${ARGS[1]}";

for (( i=4; i<$#; i++ ))
do
	value="${ARGS[2]}${ARGS[3]}${ARGS[$i]}";
	#echo $value
	
	if [ "$BACKEND" == "$CLUSTER" ]
	then
		#check if data exists on the cluster
		if test ! -e $value;
	    then
		  echo "$value is missing" 1>&2
		fi
    fi
    
    if [ "$BACKEND" == "$GRID" ]
    then
    	#download/upload data on the grid
    	if [ "$OPERATION" == "$INPUT" ]
    	then
    		lcg-cp lfn://grid$value file:///$TMPDIR${ARGS[3]}${ARGS[$i]}
    	fi

		if [ "$OPERATION" == "$OUTPUT" ]
    	then
    		lcg-cr -l lfn://grid$value file:///$TMPDIR/${INPUTS[2]}${INPUTS[$i]}
    	fi

    	#check sum on the execution node
    	echo -n "SUM_ADLER32_${ARGS[3]}${ARGS[$i]} "
		adler32 file:///$TMPDIR${ARGS[3]}${ARGS[$i]}
    fi
	
done
}


