#!/bin/bash

getRemoteLocation()
{
	ARGS=($@)

	myFile=${ARGS[0]}
	
	# 0. remoteFile = cutWorkdir( myFile )
	indexSlash=`expr index "${myFile:1}" /`
	# add one to the index because we started myFile from position 1 instead of 0
	indexSlash=`echo $indexSlash + 1 | bc`
	
	remoteFile=lfn://grid/bbmri.nl/byelas/${myFile:$indexSlash}
	
	echo $remoteFile
}

getFile()
{
	ARGS=($@)
	NUMBER="${#ARGS[@]}";
	if [ "$NUMBER" -eq "1" ]
	then

		myFile=${ARGS[0]}
		remoteFile=`getRemoteLocation $myFile`

		# 1. myPath = getPath( myFile ) will strip off the file name and return the path
		mkdir -p $(dirname "$myFile")
		
		# 2. cp lfn:.../remoteFile myFile
		lcg-cp $remoteFile file:///$myFile

	else
		echo "Example usage: getData \"\$TMPDIR/datadir/myfile.txt\""
	fi
}

putFile()
{
	ARGS=($@)
	NUMBER="${#ARGS[@]}";
	if [ "$NUMBER" -eq "1" ]
	then
		myFile=${ARGS[0]}
		remoteFile=`getRemoteLocation $myFile`
		
		echo "lcg-cr -l $remoteFile file:///$myFile"
		lcg-cr -l $remoteFile file:///$myFile
		else
			echo "Example usage: getData \"\$TMPDIR/datadir/myfile.txt\""
		fi
}