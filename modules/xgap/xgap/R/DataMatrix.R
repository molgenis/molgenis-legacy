
#
# Hand written convenient function for dealing with matrix data using dbGG framework.
#


add.datamatrix <- function(.data_matrix, name=NULL , investigation_id=NULL , rowtype=NULL , coltype=NULL , valuetype=NULL , .usesession=T, .verbose=F)
{
	starttime=Sys.time()
    #TODO check for reasonable rowtype and coltype values.
	if(is.null(rowtype)) stop("rowtype has to be provided")
	if(is.null(coltype)) stop("coltype has to be provided")
	if(is.null(valuetype)) stop("valuetype has to be provided")
	
	#check matrix structure
    if(is.null(rownames(.data_matrix)) || is.null(colnames(.data_matrix)) )
    {
        stop("the matrix has to have columnames and rownames")
    } 	
    
	#get session parameters
   	if(.usesession && is.null(investigation_id) && !is.null(.MOLGENIS$session.investigation.id))    
   	{
       	investigation_id = .MOLGENIS$session.investigation.id
       	cat("Using investigation (id='",.MOLGENIS$session.investigation.id,"'", sep="")
		cat(", name='",.MOLGENIS$session.investigation.name,"'", sep="")
		cat(") from session (.usession = T).\n")
   	} 

    #add container entity 'data' 
    container_arguments <-  mget(ls(),environment())
                                       
    #get the ids of the rows
    cat('checking rownames and columnames with database: \n')
    #todo: trim the spaces of rownames, otherwise comparison fails...
    #todo: add the missing parts for this query [or check that this never happens]
    #dynamic call, e.g. find.marker()
    call1 <- call(paste("find.",tolower(rowtype),sep=""), name=rownames(.data_matrix), investigation_id=investigation_id, .verbose=.verbose )
    row_labels <- eval(call1)   
    missing_row_labels <- setdiff( rownames(.data_matrix), row_labels$name)
    if(length(missing_row_labels)>0)
    {
        stop("not all ",rowtype," objects are known in database, missing rows: ", toString(missing_row_labels),". Use add.",rowtype,"() to correct.")
    }

    #get the ids of the columns 
    #todo: trim the spaces of rownames, otherwise comparison fails...
    #todo: add the missing parts for this query [or check that this never happens]
    #dynamic call, e.g. find.marker()
    call2<- call(paste("find.",tolower(coltype),sep=""), name=colnames(.data_matrix), investigation_id=investigation_id, .verbose=.verbose )
    col_labels <- eval(call2)
    missing_col_labels <- setdiff( colnames(.data_matrix), col_labels$name)
    if(length(missing_col_labels)>0)
    {
        stop("not all ",coltype," are known in database, missing cols: ", toString(missing_col_labels),". Use add.",coltype,"() to correct.")
    }
    
	container <- add.data(investigation_id=investigation_id, name=name, featuretype=coltype, targettype=rowtype, valuetype=valuetype, storage="Database", .verbose=.verbose)
    if( !is.logical(container) )
    {     
        MAX_BATCH <- 10000 #todo: centralize this parameter
        cat('Preparing matrix for upload to database (matrices with more than',MAX_BATCH,'elements will be batched):\n')
        
        #get row and column ids        
        rows = merge(data.frame(name = rownames(.data_matrix)), data.frame(id=row_labels$id, name=row_labels$name), sort=F)
        cols = merge(data.frame(name = colnames(.data_matrix)), data.frame(id=col_labels$id, name=col_labels$name), sort=F)
        
        #calculate row batch size
        nrowPerBatch <- min(nrow(.data_matrix),floor( MAX_BATCH / ncol(.data_matrix)))
        if(nrowPerBatch == 0)
        {
            nrowPerBatch <- 1
        }
        
        #iterate through row batches
        i = 1;
        while(i < nrow(rows))
        {
        	until <- min(nrow(.data_matrix), i + nrowPerBatch - 1)
        	cat("uploading rows ",i,":",until,"...\n")
        	#transform into RCV
			content_rows <- cbind(
				investigation_id = investigation_id,
        		data_name = name,
        		target = rows$id[i:until],
        		feature = rep(cols$id, each=(until - i + 1)),
        		targetindex = (i:until)-1,
        		featureindex = rep((i:until)-1, each=(until - i + 1)), 	
    			value=c(.data_matrix[i:until,]))
			#cat(content_rows)
			
			#add to database or handle error
            call3 <- call(paste("add.",tolower(valuetype),"dataelement",sep=""), content_rows , .verbose=.verbose)
			result <- eval(call3)
        	if( is.logical(result) )
        	{
            	cat('addition failed, rolling back\n')
                call4 <- call(paste("remove.",valuetype,"dataelement",sep=""), data=container$id)
                result <- eval(call4)            	
            	remove.data(container)
            	return(FALSE)
        	}

            i <- i + nrowPerBatch
        }

        cat('Upload of data matrix \'',name,'\' successful in ', format(difftime(Sys.time(),starttime, units="sec"), digits=3),"\n",sep="")
        return(TRUE)
    }
}

# New function to get a matrix. TODO: more arguments, use of session, unit test.
downloadmatrixascsv<-function(id=NULL)
{
	data_url <- paste(app_location,"/downloadmatrixascsv?id=",id,"&download=all&stream=false",sep="")
	data <- read.table(data_url,sep="\t",header=T,row.names=1,colClasses=c("character"),check.names=FALSE)
	data <- as.matrix(data)
	colnames <- colnames(data)
	rownames <- rownames(data)
	if(find.data(id=id)$valuetype == "Decimal"){
		data <- matrix(as.numeric(as.matrix(data)),c(dim(data)[1],dim(data)[2]))
	}else{
		data <- matrix(as.matrix(data),c(dim(data)[1],dim(data)[2]))
	}
	colnames(data) <- colnames
	rownames(data) <- rownames
	return(data)
}

downloadmatrixascsvCURL <- function(id=NULL, timeout = 1800000)
{
	myOpts = curlOptions(timeout.ms = timeout, verbose=TRUE)
	data_url <- paste(app_location,"/downloadmatrixascsv?id=",id,"&download=all&stream=true",sep="")
	tetsing <- getURL(data_url, curl = .MOLGENIS.curlHandle,.opts=myOpts)
	tmpfile <- tempfile()
	cat(tetsing,file=tmpfile)
	data <- read.table(tmpfile,sep="\t",header=T,row.names=1,colClasses=c("character"),check.names=FALSE)
	data <- as.matrix(data)
	colnames <- colnames(data)
	rownames <- rownames(data)
	if(find.data(id=id)$valuetype == "Decimal"){
		data <- matrix(as.numeric(as.matrix(data)),c(dim(data)[1],dim(data)[2]))
	}else{
		data <- matrix(as.matrix(data),c(dim(data)[1],dim(data)[2]))
	}
	colnames(data) <- colnames
	rownames(data) <- rownames
	return(data)
}


#find by pkey or skey container and remove
remove.datamatrix <- function(id=NULL, name=NULL, investigation_id=NULL)
{     
    container <- find.data(id, name=name, investigation_id=investigation_id)
    if(is.null(container)) stop('cannot find data. Search using {id} or {name and investigation_id}')

    #find the type of the dataelements
    datatype = container$valuetype
    id = container$id
    
    #e.g. find.textdataelement(data = id)
    call1 <- call(paste("find.",tolower(datatype),"dataelement",sep=""), data_id = id)
    content_rows <- eval(call1)
     
    #todo: make more efficient by moving this to server
    #e.g. remove.textdataelement(content_rows)
    call2 <- call(paste("remove.",tolower(datatype),"dataelement",sep=""), content_rows)
    eval(call2)
    remove.data(container)    
}

#
# CsvToXgapBin.R
#
# From: https://github.com/DannyArends/iqtl/blob/master/R/CsvToXgapBin.R
#
# copyright (c) 2011, Danny Arends
# last modified dec, 2011
# first written dec, 2011
#
# R functions: CsvToXgapBin
#

CsvToXgapBin <- function(name = "ExampleData", investigation = "ExampleInv", rowtype= "Metabolite", coltype= "Individual", valuetype="Decimal", file="metab.txt", verbose = TRUE){
  require(RCurl)
  if(file.exists("CsvToBin.jar")){
    if(verbose) cat("WARNING: 'CsvToBin.jar' exists and will be re-downloaded\n")
    file.remove("CsvToBin.jar")
  }
  csvtobin <- getBinaryURL("http://www.molgenis.org/svn/standalone_tools/jars/CsvToBin.jar")
  writeBin(csvtobin,"CsvToBin.jar")
  outname <- gsub(".txt",".bin",file)
  if(file.exists(outname)){
    if(verbose) cat("WARNING: Output file '",outname,"'exists and will be overwitten\n")
    file.remove(outname)
  }
  command <- paste("java -jar CsvToBin.jar",name,investigation,rowtype,coltype,valuetype,file)
  if(verbose) cat("CMD:", command,"\n")
  res <- system(command,show.output.on.console=verbose)
  if(res != 0){
    stop("Error executing CsvToBin")
  }
}
