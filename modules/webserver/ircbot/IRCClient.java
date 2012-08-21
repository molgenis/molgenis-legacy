///**
//*
//* \file IRCClient.java
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
//import java.util.ArrayList;
//
//import org.jibble.pircbot.PircBot;
//import org.jibble.pircbot.User;
//
///**
// * \brief Basic implementation of a IRC bot<br>
// *
// * Basic implementation of a IRC bot
// * bugs: none found<br>
// */
//public class IRCClient extends PircBot {
//	IRCClientData mydata;
//	boolean verbose = false;
//	
//	public IRCClient(String name, int id) {
//		mydata = new IRCClientData(this,name,id,new java.util.Date());
//		new Thread(mydata).start();
//    setName(name+ "_"+ id);
//  }
//    
//	protected void onConnect(){
//		mydata.setHostName(getInetAddress().getHostName());
//	}
//	
//  protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice){
//    if(verbose) System.out.println(sourceNick + " gave us a notice: " + notice);
//  }
//    
//  protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason){
//    mydata.removeClient(sourceNick,sourceHostname);
//  }
//
//  protected  void	onPart(String channel, String sender, String login, String hostname){
//  	mydata.removeClient(sender,hostname);
//  }
//    
//  public void onPrivateMessage(String sender, String login, String hostname, String message){
//  	if(verbose) System.out.println("Private msg: " + message + " " + sender);
//		if(message.equalsIgnoreCase("known")) {
//		  mydata.sendKnownClients(sender);
//  		return;
//  	}
//		if(message.equalsIgnoreCase("info")) {
//		  mydata.sendInformation(sender);
//  		return;
//		}
//    if(message.startsWith("dojob")) {
//      mydata.getJob(sender,message);
//      return;
//    }
//    if(message.equalsIgnoreCase("joblist")) {
//      mydata.sendJobOverview(sender);
//      return;
//    }
//    try{
//    	String[] t = message.split(";");
//    	if(t[0].equalsIgnoreCase("job")){
//    	  mydata.handleJobCommand(message, sender);
//    	}
//    	if(t[0].equalsIgnoreCase("bot")){
//    	  mydata.handleBotCommand(message, sender);
//    	}
//  	}catch(Exception e){
//  		System.err.println(message + " Unknown command");
//  		mydata.sendSupportedCommands(sender);
//  	}
//  }
//    
//  protected void onJoin(String channel, String sender, String login, String hostname){
//  	if(sender.startsWith(mydata.getMyname())){
//  		sendMessage(sender, mydata.getIDString());
//  		if(verbose) System.out.println("Sending old joblist: " + mydata.getJobQueue().size());
//  		for(int x = 0; x < mydata.getJobQueue().size();x++){
//  		  IRCJobStruct j = mydata.getJobQueue().get(x);
//        String updatecmd = "job;old;"+j.id + ";" + mydata.getMyid() + ";" + j.host + ";"+j.command + ";" + j.queued;
//        updatecmd += ";" + j.running + ";" + j.finished;
//        updatecmd += ((!j.suc6)?";fail":";suc6");
//        if(verbose) System.out.println("Sending old job: " + updatecmd);
//        sendMessage(sender,updatecmd);  
//      }
//  	}
//  }
//    
//  protected void onUserList(String channel, User[] users){
//  	for(User u : users){
//  		if(u.getNick().startsWith(mydata.getMyname())){
//  			sendMessage(u.getNick(), mydata.getIDString());
//  		}
//  	}
//  }
//
//	public ArrayList<IRCClientData> getOther_Clients() {
//		return mydata.getOther_clients();
//	}
// }