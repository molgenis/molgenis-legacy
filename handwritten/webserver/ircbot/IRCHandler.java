package ircbot;

import java.util.ArrayList;

import org.jibble.pircbot.NickAlreadyInUseException;

public class IRCHandler implements Runnable{
	private IRCClient ircclient;
	private static String default_name = "MWorkBOT";
	private static String default_channel = "molgenis_apps";
	private static String default_irc_host = "irc.freenode.net";
	String name;
	int id;
	String channel;
	boolean verbose;
	public IRCHandler(){
		this(default_name,(int)(Math.random()*1000),default_channel,false);
	}
	
	public IRCHandler(boolean verbose){
		this(default_name,(int)(Math.random()*1000),default_channel,verbose);
	}
	
	public IRCHandler(int id, boolean verbose){
		this(default_name,id,default_channel,verbose);
	}
	
	public IRCHandler(String n,int i, String c, boolean v){
		name=n;
		id=i;
		channel=c;
		verbose=v;
	}
	
	
	boolean connect(String name, int id, String channel,boolean verbose){
		ircclient = new IRCClient(name,id);
		ircclient.setVerbose(verbose);
		try {
			ircclient.connect(default_irc_host);
			System.out.println("Connection to "+ default_irc_host +" established");
			ircclient.joinChannel("#" + channel);
			System.out.println("Joined "+ channel);
			return true;
		}catch (NickAlreadyInUseException e){
			return connect(name,(int)(Math.random()*1000),channel,verbose);
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	public ArrayList<IRCClientData> getAllConnectedHosts(){
		return ircclient.getOther_Clients();
	}
	
	public void printHosts(ArrayList<IRCClientData> clients){
		for(IRCClientData c : clients){
			System.out.println(c.getMyIDString());
		}
	}
	
	public static void main(String[] args) throws Exception {
		new IRCHandler((int)(Math.random()*1000),false);
	}

	@Override
	public void run()
	{
		connect(name,id,channel,verbose);
	}
}
