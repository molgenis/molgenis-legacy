///**
//*
//* \file IRCClientData.java
//*
//* copyright (c) 2009-2010, Danny Arends
//* last modified May, 2011
//* first written May, 2011
//*
//*     This program is free software; you can redistribute it and/or
//*     modify it under the terms of the GNU General Public License,
//*     version 3, as published by the Free Software Foundation.
//* 
//*     This program is distributed in the hope that it will be useful,
//*     but without any warranty; without even the implied warranty of
//*     merchantability or fitness for a particular purpose.  See the GNU
//*     General Public License, version 3, for more details.
//* 
//*     A copy of the GNU General Public License, version 3, is available
//*     at http://www.r-project.org/Licenses/GPL-3
//*
//*/
//
//package ircbot;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//import generic.CommandExecutor;
//import generic.Utils;
//
//import org.jibble.pircbot.PircBot;
//
///**
// * \brief Data structure holding data from all the clients<br>
// *
// * Data structure holding data from all the clients (could be generated)
// * bugs: none found<br>
// */
//public class IRCClientData implements Runnable{
//	private String myname;
//	private int myid;
//	private Date start_time;
//	private String hostname;
//	private ArrayList<IRCClientData> otherClients = new ArrayList<IRCClientData>();
//	private ArrayList<IRCJobStruct> jobQueue = new ArrayList<IRCJobStruct>();
//	CommandExecutor cmd = new CommandExecutor();
//	boolean verbose = false;
//	PircBot server;
//	private String date_format = "dd/MM/yyyy k:mm:ss:S";
//	
//	IRCClientData(PircBot s,String name,int id,Date time){
//	  server = s;
//	  myname = name;
//		myid = id;
//		start_time = time;
//	}
//	
//	IRCClientData(PircBot s,String host,String name,int id,Date time){
//		this(s,name,id,time);
//		hostname=host;
//	}
//	
//	public void setHostName(String host){
//		hostname=host;
//	}
//	
//	public String getIDString(){
//		return "bot;" + getMyid()  + ";" + hostname + ";" + getStart_time();
//	}
//	
//	public String getHTMLString(){
//	  return "<td valign='top'><a href='?bot=" + getMyid()  + "'><b>" + hostname + "</b></a></td><td valign='top'>" + getStart_time() + "</td>";
//	}
//
//	public String getFullName() {
//		return myname + "_" + getMyid();
//	}
//
//	public String getMyname() {
//		return myname;
//	}
//
//	public int getMyid() {
//		return myid;
//	}
//	
//  public void removeClient(String sender, String hostname){
//    if(verbose) System.out.println(sender + " leaving / left ");
//    boolean found = false;
//    for(int x = 0; x < getOther_clients().size();x++){
//      if(getOther_clients().get(x).getFullName().equals(sender)){
//        getOther_clients().remove(x);
//        if(verbose) System.out.println("Found in clients and removed: " + sender);
//        found=true;
//      }
//    }
//    if(found) removeJobsFromHost(hostname);
//  }
//	
//	private void removeJobsFromHost(String sender) {
//	  int cnt = 0;
//	  for(int x = 0; x < getJobQueue().size();x++){
//	    if(getJobQueue().get(x).host.equalsIgnoreCase(sender)){
//	      getJobQueue().remove(x);
//	      cnt++;
//	    }
//	  }
//	  if(verbose) System.out.println("Removed " + cnt + " jobs from " + sender);
//  }
//
//  public void sendInformation(String sender){
//    server.sendMessage(sender,"--Molgenis Compute Network--");
//    server.sendMessage(sender,"Worker node: " + getMyid());
//    server.sendMessage(sender,"CPUs: " + Runtime.getRuntime().availableProcessors());
//    server.sendMessage(sender,"Memory free: " + (Runtime.getRuntime().freeMemory()/(1024*1024)) + " M");
//    server.sendMessage(sender,"Memory available: " + (Runtime.getRuntime().totalMemory()/(1024*1024)) + " M");
//    server.sendMessage(sender,"Location: " + server.getInetAddress().getHostName());
//    server.sendMessage(sender,"Online since: " + getStart_time());
//    server.sendMessage(sender,"Other nodes: " + getOther_clients().size());
//	}
//  
//  public void sendSupportedCommands(String sender){
//    server.sendMessage(sender,"--Supported commands--");
//	  server.sendMessage(sender,"info - Prints out information");
//	  server.sendMessage(sender,"known - Print out known other nodes");
//	  server.sendMessage(sender,"dojob - Do the job available at location");
//	  server.sendMessage(sender,"joblist - Print the joblist");
//	  server.sendMessage(sender,"bot - Add a new bot to the network");
//	  server.sendMessage(sender,"job - Add/Update jobs in the network");
//	}
//	
//	public void sendKnownClients(String sender){
//    for(IRCClientData c : getOther_clients()){
//      server.sendMessage(sender, c.getIDString());
//    }
//	}
//	 
//   public void sendJobOverview(String sender){
//     server.sendMessage(sender, "--Job Queue (" + getJobQueue().size() + ")--");
//     for(int x = 0; x < getJobQueue().size();x++){
//       server.sendMessage(sender, ""+x + getJobQueue().get(x).printStatus(myid));
//     }
//   }
//
//	public String getStart_time() {
//		DateFormat formatter = new SimpleDateFormat(date_format);
//		return formatter.format(start_time);
//	}
//
//  public void setOther_clients(ArrayList<IRCClientData> other_clients) {
//    this.otherClients = other_clients;
//  }
//
//  public ArrayList<IRCClientData> getOther_clients() {
//    return otherClients;
//  }
//
//  public void getJob(String sender, String message) {
//    if(verbose) System.out.println(sender + " has a job");
//    try{
//      String[] t = message.split(";");
//      String command = t[1];
//      int jobid = Integer.parseInt(t[2]);
//      for(int x = 0; x < getJobQueue().size();x++){
//        if(getJobQueue().get(x).id == jobid && getJobQueue().get(x).host.equalsIgnoreCase(sender)){
//          server.sendMessage(sender, "Job " + jobid + " already in queue at " + x + "");
//          return;
//        }
//      }
//      getJobQueue().add(new IRCJobStruct(sender,command,jobid));
//      server.sendMessage(sender, "Job " + jobid + " in queue (" + getJobQueue().size() + ")");
//      for(IRCClientData c : otherClients){
//        server.sendMessage(c.getFullName(), "job;new;"+jobid + ";" + myid + ";" + sender + ";"+command);
//      }
//      if(verbose) System.out.println(sender + " job accepted");
//    }catch(Exception e){
//      sendSupportedCommands(sender);
//      System.err.println("Unknown job: " + message);
//    }
//  }
//
//  public void setJobQueue(ArrayList<IRCJobStruct> jobQueue) {
//    this.jobQueue = jobQueue;
//  }
//
//  public ArrayList<IRCJobStruct> getJobQueue() {
//    return jobQueue;
//  }
//
//  public int getFirstQueuedJob() {
//    for(int x = 0; x < getJobQueue().size();x++){
//      if(getJobQueue().get(x).queued) return x;
//    }
//    return -1;
//  }
//  
//  boolean client_know(int id){
//    for(IRCClientData c : getOther_clients()){
//      if(c.getMyid() == id) return true;
//    }
//    return false;
//  }
//  
//  int client_index(int id){
//    int cnt=0;
//    for(IRCClientData c : getOther_clients()){
//      if(c.getMyid() == id){
//        return cnt;
//      }
//      cnt++;
//    }
//    return 0;
//  }
//  
//  public void handleBotCommand(String message,String sender){
//    try{
//      String[] t = message.split(";");
//      if(!client_know(Integer.parseInt(t[1]))){
//        DateFormat formatter = new SimpleDateFormat(date_format);
//        getOther_clients().add(new IRCClientData(server,t[2],sender.split("_")[0],Integer.parseInt(t[1]),formatter.parse(t[3])));
//        if(verbose) System.out.println("New client: "+sender+ " from "+t[2]+" since " + t[3]);
//      }
//    }catch(Exception e){
//      System.err.println(" Unknown Bot command" + message);
//      e.printStackTrace();
//    }
//  }
//  
//  public void handleJobCommand(String message,String sender){
//    try{
//    String[] t = message.split(";");
//    int jobid = Integer.parseInt(t[2]);
//    int clientid = Integer.parseInt(t[3]);
//    if(t[1].equals("update")){
//      int jobindex=0;
//      ArrayList<IRCJobStruct>  queue = getOther_clients().get(client_index(clientid)).getJobQueue();
//      for(IRCJobStruct j : queue){
//        if(j.id == jobid){
//          if(t[4].equals("running")){
//            queue.get(jobindex).queued=false;
//            queue.get(jobindex).running=true;
//            if(verbose) System.out.println("Client: "+sender+ " updated a job "+ jobid);
//            getOther_clients().get(client_index(clientid)).setJobQueue(queue);
//          }
//          if(t[4].equals("finished")){
//            queue.get(jobindex).running=false;
//            queue.get(jobindex).finished=true;
//            if(t[5].equals("suc6")){
//              queue.get(jobindex).suc6=true;
//            }
//            if(verbose) System.out.println("Client: "+sender+ " updated a job "+ jobid);
//            getOther_clients().get(client_index(clientid)).setJobQueue(queue);
//          }
//        }
//        jobindex++;
//      }
//    }
//    if(t[1].equals("old")){
//      IRCJobStruct s = new IRCJobStruct(t[4],t[5],jobid);
//      s.queued = Boolean.parseBoolean(t[6]);
//      s.running = Boolean.parseBoolean(t[7]);
//      s.finished = Boolean.parseBoolean(t[8]);
//      s.suc6 = t[9].equals("suc6");
//      getOther_clients().get(client_index(clientid)).getJobQueue().add(s);
//      if(verbose)  System.out.println("Client: "+sender+ " reports an old job " + jobid+ t[4]);
//    }
//    if(t[1].equals("new")){
//        IRCJobStruct s = new IRCJobStruct(t[4],t[5],jobid);
//        getOther_clients().get(client_index(clientid)).getJobQueue().add(s);
//        if(verbose) System.out.println("Client: "+sender+ " reports a new job " + jobid+ t[4]);
//    }
//    }catch(Exception e){
//      System.err.println(message + " Unknown Job command");
//    }
//
//  }
//  
//  @Override
//  public void run() {
//    while(true){
//      int jobindex;
//      if(jobQueue.size() > 0 && (jobindex = getFirstQueuedJob()) >= 0){
//        IRCJobStruct job = jobQueue.get(jobindex);
//        cmd.clearCommands();
//        cmd.addCommand(job.command);
//        Thread t = new Thread(cmd);
//        t.start();
//        jobQueue.get(jobindex).queued=false;
//        jobQueue.get(jobindex).running=true;
//        jobQueue.get(jobindex).finished=false;
//        jobQueue.get(jobindex).start_time = new Date();
//        for(IRCClientData c : otherClients){
//          server.sendMessage(c.getFullName(), "job;update;"+ job.id + ";" + myid + ";running");
//        }
//        try{
//          while(t.isAlive()){
//            Utils.idle(2000);
//          }
//        }catch (Exception e){
//          e.printStackTrace();
//        }
//        cmd.getResult();
//        jobQueue.get(jobindex).queued=false;
//        jobQueue.get(jobindex).running=false;
//        jobQueue.get(jobindex).finished=true;
//        jobQueue.get(jobindex).suc6=!(cmd.failed_commands>0);
//        jobQueue.get(jobindex).end_time = new Date();
//        for(IRCClientData c : otherClients){
//          String updatecmd = "job;update;"+ job.id + ";" + myid + ";finished";
//          updatecmd+= ((cmd.failed_commands>0)?";fail":";suc6");
//          server.sendMessage(c.getFullName(),updatecmd);
//        }
//      }else{
//        Utils.idle(2000);
//      }
//    }
//  }
// }