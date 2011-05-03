package ircbot;

import java.io.IOException;
import java.util.ArrayList;

import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class IRCClient extends PircBot {
	IRCClientData mydata;
	ArrayList<IRCClientData> other_clients = new ArrayList<IRCClientData>();
	
	public IRCClient(String name, int id) {
		mydata = new IRCClientData(name,id,new java.util.Date().toString());
        setName(name+ "_"+ id);
    }
    
	protected void onConnect(){
		mydata.setHostName(getInetAddress().getHostName());
	}
	
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	if (message.equalsIgnoreCase("all_ids")) {
    		sendMessage(sender, mydata.getMyIDString());
    	}
    }
    
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice){
    	System.out.println(sourceNick + " gave us a notice:" + notice);
    }
    
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason){
    	removeClient(sourceNick);
    }
    
    public void removeClient(String sender){
    	System.out.println(sender + " leaving / left ");
		for(int x =0; x < other_clients.size();x++){
			if(other_clients.get(x).getFullName().equals(sender)){
				other_clients.remove(x);
				System.out.println("Found in clients and removed: " + sender);
			}
		}
    }
    
    protected  void	onPart(String channel, String sender, String login, String hostname){
    	removeClient(sender);
    }
    
    public void onPrivateMessage(String sender, String login, String hostname, String message){
    	System.out.println("private msg: " + message + " " + sender);
    	if(sender.startsWith(mydata.getMyname()) && !sender.equals(mydata.getFullName())){
    		if(message.equalsIgnoreCase("known")) {
	    		for(IRCClientData c : other_clients){
	    			sendMessage(sender, c.getMyIDString());
	    		}
	    		return;
	    	}
    		try{
		    	String[] t = message.split(";");
		    	if(!client_know(Integer.parseInt(t[0]))){
		    		other_clients.add(new IRCClientData(t[1],sender.split("_")[0],Integer.parseInt(t[0]),t[2]));
		    		System.out.println("New client: "+sender+ " from "+t[1]+" since " + t[2]);
		    	}
	    	}catch(Exception e){
	    		System.err.println("msg: " + message + " not a client connecting");
	    	}
    	}
    }
    
    public void onIncomingChatRequest(DccChat chat) {
        try {
            chat.accept();
            chat.sendLine("--Molgenis Compute Network--");
            chat.sendLine("Worker node: " + mydata.getMyid());
            chat.sendLine("Location: " + getInetAddress().getHostName());
            chat.sendLine("Online since: " + mydata.getStart_time());
            chat.close();
        }
        catch (IOException e) {}
    }
    
    protected void onJoin(String channel, String sender, String login, String hostname){
    	if(sender.startsWith(mydata.getMyname())){
    		if(!sender.equals(mydata.getFullName())){
	    		sendMessage(sender, mydata.getMyIDString());
	    	}
    	}
    }
    
    protected void onUserList(String channel, User[] users){
    	for(User u : users){
    		if(!u.getNick().equals(mydata.getFullName())){
    			sendMessage(u.getNick(), mydata.getMyIDString());
    		}
    	}
    }
    
    boolean client_know(int id){
    	for(IRCClientData c : other_clients){
    		if(c.getMyid() == id) return true;
    	}
    	return false;
    }

	public ArrayList<IRCClientData> getOther_Clients() {
		return other_clients;
	}
}