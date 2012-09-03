//package ircbot;
//
//import java.util.Date;
//
//public class IRCJobStruct{
//  String host;
//  String command;
//  int id;
//  Date received_time;
//  Date start_time;
//  Date end_time;
//  boolean queued = true;
//  boolean running = false;
//  boolean finished = false;
//  boolean suc6 = false;
//  
//  IRCJobStruct(String host,String cmd,int id){
//    received_time = new java.util.Date();
//    this.host=host;
//    if(cmd.equals("dir") || cmd.equals("notepad") || cmd.equals("rgui")){
//    	command=cmd;	
//    }else{
//    	cmd = "dir";
//    }
//    this.id=id;
//  }
//  
//  public String printStatus(int hostid){
//    String s = "<a href='?id="+id + "&bot="+hostid+"&stream=out'><font color='green'>out</font></a> ";
//    s+= "<a href='?id="+id + "&bot="+hostid+"&stream=err'><font color='red'>err</font></a>";
//    s+= "</td><td>" + command + "</td><td>" + (queued?"Queued":"") + (running?"Running":"") + (finished?"Finished":"");
//    if(finished){
//      s+= "</td><td><font color='" + (suc6?"green":"red")+ "'>"+ (suc6?"OK":"Failed")+"</font>";
//    }else{
//      s+= "</td><td><font color='orange'>Unknown</font>";
//    }
//    return s;
//  }
// }