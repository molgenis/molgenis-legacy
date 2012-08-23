///**
//*
//* \file IRCHandler.java
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
//package ircbot;
//
//import java.util.ArrayList;
//
//import org.jibble.pircbot.NickAlreadyInUseException;
//
///**
// * \brief Threadable object that contains the IRC bot<br>
// *
// * Threadable object that contains the IRC bot
// * bugs: none found<br>
// */
//public class IRCHandler implements Runnable{
//	private IRCClient ircclient;
//	private static String default_name = "MWorkBOT";
//	public static String default_channel = "molgenis_apps";
//	private static String default_irc_host = "irc.freenode.net";
//	
//	String name;
//	int id;
//	String channel;
//	boolean verbose;
//	public IRCHandler(){
//		this(default_name,(int)(Math.random()*1000),default_channel,false);
//	}
//	
//	public IRCHandler(boolean verbose){
//		this(default_name,(int)(Math.random()*1000),default_channel,verbose);
//	}
//	
//	public IRCHandler(int id, boolean verbose){
//		this(default_name,id,default_channel,verbose);
//	}
//	
//	public IRCHandler(String n,int i, String c, boolean v){
//		name=n;
//		id=i;
//		channel=c;
//		verbose=v;
//	}
//		
//	public boolean connect(String name, int id, String channel,boolean verbose){
//		ircclient = new IRCClient(name,id);
//		ircclient.setVerbose(verbose);
//		try {
//			ircclient.connect(default_irc_host);
//			System.out.println("Connection to "+ default_irc_host +" established");
//			ircclient.joinChannel("#" + channel);
//			System.out.println("Joined "+ channel);
//			return true;
//		}catch (NickAlreadyInUseException e){
//			return connect(name,(int)(Math.random()*1000),channel,verbose);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		return false;
//	}
//	
//	
//	public ArrayList<IRCClientData> getAllConnectedHosts(){
//		return ircclient.getOther_Clients();
//	}
//	
//	public void printHosts(ArrayList<IRCClientData> clients){
//		for(IRCClientData c : clients){
//			System.out.println(c.getIDString());
//		}
//	}
//	
//	public static void main(String[] args) throws Exception {
//		IRCHandler h = new IRCHandler((int)(Math.random()*1000),false);
//		new Thread(h).start();
//	}
//
//	@Override
//	public void run(){
//		connect(name,id,channel,verbose);
//	}
// }
