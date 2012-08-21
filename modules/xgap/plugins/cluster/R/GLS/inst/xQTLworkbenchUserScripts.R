#Voor xQTL workbench

#Data preprocessing

#Get parameters from User
r.image.name     <- getParameter("imagename",jobparams)
csv.file.name    <- getParameter("csvphenoname",jobparams) 

new.r.image.name <- paste(outname,".Rdata",sep="")
#Download the 2 files
urlofdatset <- paste(dbpath,"/downloadfile?name=",bindata,sep="")
dataset <- getURLcontent(urlofdatset)
writebin(dataset,file=paste(r.image.name,".RData",sep=""))

urlofdatset <- paste(dbpath,"/downloadfile?name=",csv.file.name,sep="")
dataset <- getURLcontent(urlofdatset)
write(dataset,file=paste(csv.file.name,".csv",sep=""))
#Load data into R
load(r.image.name)
GWAS.obj <- AddPhenoData(gwas.obj=GWAS.obj,csv.file.name=csv.file.name,add.var.means=add.var.means, mean.cols=mean.cols,make.pheno.image=F, pheno.image.name="",add.normal.transform=add.normal.transform)
#Save the new object
save(GWAS.obj,file=new.r.image.name)
#Upload the object to xQTL
urlofupload <- paste(dbpath,"/uploadfile",sep="")
postForm(urlofupload,investigation_name=investigationname, name=paste(outname,".RData"),type="InvestigationFile", file=fileUpload(new.r.image.name),style="HTTPPOST")


#scan_GLS
lodt    <- getParameter("lodt",jobparams)
bindata <- getParameter("dataname",jobparams)

urlofdatset <- paste(dbpath,"/downloadfile?name=",bindata,sep="")
dataset <- getURLcontent(urlofdatset)
writebin(dataset,file="mydata.RData")

urlofdatset <- paste(dbpath,"/downloadfile?name=BIN",bindata,sep="")
dataset <- getURLcontent(urlofdatset)
writebin(dataset,file="mydata.bin")

if(lodt==-1) lodt <- autoDeterminLod("mydata.RData")

data.path    <- getParameter("path",jobparams)
covariates   <- getParameter("cov",jobparams)

report(2,paste("Parameters loaded",path," ",trait.numbers)
sleep(100)
msource("http://localhost:8080/xQTL/api/R/run_scan_GLS.R")
urlofupload <- paste(dbpath,"/uploadfile",sep="")

for(x in dir("results/")){
  report(2,paste("Start of uploading file",x))
  postForm(urlofupload,investigation_name=investigationname, name = x,type="InvestigationFile", file=fileUpload(paste("result/",x,sep="")),style="HTTPPOST")
}

report(3,"Done")

