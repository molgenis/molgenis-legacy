package ircbot;

public class IRCClientData{
	private String myname;
	private int myid;
	private String start_time;
	private String hostname;
	
	IRCClientData(String n,int i,String t){
		myname =(n);
		myid= (i);
		start_time =(t);
	}
	
	IRCClientData(String host,String n,int i,String t){
		this(n,i,t);
		hostname=host;
	}
	
	public void setHostName(String host){
		hostname=host;
	}
	
	public String getMyIDString(){
		return getMyid()  + ";" + hostname + ";" + getStart_time();
	}

	public String getFullName() {
		return myname + "_" + getMyid();
	}

	public String getMyname() {
		return myname;
	}

	public int getMyid() {
		return myid;
	}

	public String getStart_time() {
		return start_time;
	}
}