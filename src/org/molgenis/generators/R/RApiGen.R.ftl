<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
# File:        ${model.getName()}/R/${file}
# Copyright:   GBIC 2000-${year?c}, all rights reserved
# Date:        ${date}
#
# generator:   ${generator} ${version}
#
# This file sources all R files.
#
# THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
#

if( !exists("molgenispath") ) molgenispath <- ""
#allows source the script while passing another path, e.g. to the web server

#entities
<#list model.entities as entity><#if !entity.abstract && !entity.association>
source(paste(molgenispath,"${entity.namespace?replace(".","/")}/R/${Name(entity)}.R", sep=""))
</#if></#list>

# matrices
<#list model.matrices as matrix>
source(paste(molgenispath,"${name(model)}/R/${Name(matrix)}.R", sep=""))
</#list>

#keep track of the session by emulating a browser
ch <- getCurlHandle()
ch <- curlSetOpt(
	curl = ch,
	ssl.verifypeer = FALSE,
	useragent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13",
	timeout = 60,
	followlocation = TRUE,
	cookiejar = "./cookies",
	cookiefile = "./cookies"
)

MOLGENIS.login = function(username, password)
{
	servlet <- paste( .MOLGENIS$servletURL, "/api/R", sep="" )
	curlParams = list(usr = username, pw = password)
	response <- postForm( servlet, .params = curlParams, curl = ch )
	handle <- textConnection(response)
	status <- readLines(handle, 1)
	cat(status, "\n")
	close( handle )
}

MOLGENIS.logout = function()
{
	servlet <- paste( .MOLGENIS$servletURL, "/api/R", sep="" )
	curlParams = list(logout = "logout")
	response <- postForm( servlet, .params = curlParams, curl = ch )
	handle <- textConnection(response)
	status <- readLines(handle, 1)
	cat(status, "\n")
	close( handle )
}

# static helper functions 
MOLGENIS.connect <- function( servletURL, dbUser=NULL )
{
	# Loading RCurl under Unix this way will ONLY work when the package is installed as root.
	# User installations are in different directories.
	# If users load it in a custom way, the library must not be sourced again.
	# If they do not load it.. TODO: write alternative call to load RCurl
	if(!any(loadedNamespaces()=="RCurl")){
    	library( RCurl )
    }
    #used to send messages to server

    # We check and prepare the argument variables
    if( missing( servletURL ) ) {
        stop( "arg1: You should provide an servletURL (e.g. \"http://localhost:8080/molgenis4gg\")\n" )
    }
    if( is.null( dbUser ) ) {
        dbUser <- ""
    }
  
    .MOLGENIS <<- data.frame( servletURL= servletURL, dbUser=dbUser )
    .MOLGENIS.curlHandle <<-  getCurlHandle();
    
    cat("MOLGENIS is connected\n")
}

MOLGENIS.debug<-function(...)
{
    if(!is.null(.MOLGENIS$debug) && .MOLGENIS$debug == T)
    {
        cat(...);
    }
}

#create a list of query criteria
MOLGENIS.createCriteria<-function(args)
{
    result <- list()
  	for(i in 1:length(args))
  	{
        #cat('what:',names(args)[i], args[[i]], class(args[[i]]),"\n" )
          if(!is.null(args[[i]]))
          {                
              if(length(args[[i]])>1)
                 result[names(args[i])] <- paste("[",paste(args[[i]], collapse=","),"]", sep="") 
              else
                 result[names(args[i])] <- toString(args[[i]])               
          }
  	}
  	return(result)
}

MOLGENIS.find<-function( entityName, conditions, .verbose=T )
{ 	
    starttime=Sys.time()
    
    # a list of conditions
    filter<-MOLGENIS.createCriteria(conditions) 

    # Check wether we are connected
    if(!exists(".MOLGENIS"))
    {
        stop("You first must connect to a MOLGENIS. Use function 'MOLGENIS.connect(url)'")
    }
 
    # We check and prepare the argument variables
    if( missing( entityName ) ) {
        stop( "arg1: You should provide an entityName (e.g. \"Experiment\")" )
    }
    #todo: use post instead of get
    uri <- paste( .MOLGENIS$servletURL, "/api/find/", entityName, sep="" )
 
    ##log
    # cat("retrieving using uri",uri,"\n")
    flush.console()
  
    # We query the server

    <#--suppressWarnings(-->
    outputString <- postForm( uri, .params = filter, curl = ch )
    <#--)-->
	MOLGENIS.debug("Send find to server and got to parse in", format(difftime(Sys.time(),starttime, units="sec"), digits=3),"sec.\n")    
	
    # Check for errors
    # - the Entity doesn't exists
    if( regexpr( "java.lang.ClassNotFoundException", outputString ) != -1 ) {
        cat( "Error in getEntity: entity '", entityName,"' doesn't exists\n", sep="" )
        flush.console()
        return (-1)
    }      
    # - empty answer
    else if( nchar( outputString ) == 0 ) {
        # We let the empty string to go outside. It is not a drastic error!   
        if(.verbose) cat("Downloaded", 0,"rows of",entityName,"in", format(difftime(Sys.time(),starttime, units="sec"), digits=3),"sec.\n")    
        return (outputString)
    }
    # no more errors, we can process the output
    else {	
        handle <- textConnection( outputString )
        result <- read.table( handle, as.is=T, header=T, sep="\t", quote="\"" )
        close( handle )
        if(.verbose) cat("Downloaded", format(nrow(result),big.mark=","),"rows of",entityName,"in", format(difftime(Sys.time(),starttime, units="sec"), digits=3),"sec.\n")
        rownames(result) <- tolower(rownames(result))
        colnames(result) <- tolower(colnames(result))
        return (result)
    }
}

MOLGENIS.update <- function(entityName, dataMatrix, action, is_matrix=F, row_type=NA, col_type=NA, is_silent=F, .verbose=T)
{
    if(!is.data.frame(dataMatrix) && !is.matrix(dataMatrix))
        stop("MOLGENIS.Update expects dataMatrix of type data.frame or matrix") 
    if(nrow(dataMatrix) == 0)
        stop("nothing to do, dataMatrix is empty")        

    starttime=Sys.time()
    MOLGENIS.debug(action, "ing " ,nrow(dataMatrix), " rows of ",entityName, ".\n" , sep="" )
    if(.verbose && ceiling(nrow(dataMatrix)/1000) > 5) cat("Can take ", ceiling(nrow(dataMatrix)/1000), " secs... \n",sep="")
    flush.console()
    
    #create tempfile to upload
    temp<-tempfile()
    write.table(dataMatrix, file=temp, sep="\t", quote=F, row.names=is_matrix)
    MOLGENIS.debug("\ncreated tab-file",temp,"for upload in", format(difftime(Sys.time(),starttime, units="sec"), digits=3),"\n")
    
    #execute the request
    servlet <- paste( .MOLGENIS$servletURL, "/api/add", sep="" ) 
    
    curl_params = list(
        data_type_input = entityName, 
        data_action = action,
        submit_input = "submit",
        data_silent = toString(is_silent),
        data_file = fileUpload(filename=temp, contentType="text/plain")
    )
    
    #if small set, do not use file upload)
    if(nrow(dataMatrix)<20000)
    {
        handle<-file(temp)
        curl_params = list(
            data_type_input = entityName, 
            data_input = paste(scan(handle,"raw", sep="\n", quote="\"", quiet=T), collapse="\n"),
            data_action = action,
            data_silent = toString(is_silent),
            submit_input = "submit")
        close(handle)
    }
    
    webResponse <- postForm( servlet, .params = curl_params, curl = ch ) 
    MOLGENIS.debug("send data to server and got response in", format(difftime(Sys.time(),starttime, units="sec"), digits=3),"\n")
    #remove tempfile      
    unlink(temp)                                              
    
    #handle the response
    handle <- textConnection(webResponse)                           
    status <- readLines(handle, 1)
    if( regexpr( "Failed", status ) < 0 ) {
        result = TRUE
        
        # let result hold updated data (if any lines available)
        line <- readLines(handle, n=1, ok=TRUE, warn=<#--FALSE-->TRUE)
        #cat("what is wrong with line '",line,"' of type ",typeof(line), sep="")
        if(line != "")
        {
            pushBack(line,handle)
            result <- read.table( handle, as.is=T, header=T, sep="\t", quote="\"" )
        }
        close( handle )
          
        #show status
        cat(status,"in",format(difftime(Sys.time(),starttime, units="sec"), digits=3),"\n")
        return (result);    	
    }   
    else {
        #cat("\n",status,"\n", sep="")
        close( handle )
        stop(status)
        #return (FALSE);
    }  
}
