/**
*
* \file IRCClientData.java
*
* copyright (c) 2009-2010, Danny Arends
* last modified May, 2011
* first written May, 2011
*
*     This program is free software; you can redistribute it and/or
*     modify it under the terms of the GNU General Public License,
*     version 3, as published by the Free Software Foundation.
* 
*     This program is distributed in the hope that it will be useful,
*     but without any warranty; without even the implied warranty of
*     merchantability or fitness for a particular purpose.  See the GNU
*     General Public License, version 3, for more details.
* 
*     A copy of the GNU General Public License, version 3, is available
*     at http://www.r-project.org/Licenses/GPL-3
*
*/

package ircbot;

/**
 * \brief Data structure holding data from all the clients<br>
 *
 * Data structure holding data from all the clients (could be generated)
 * bugs: none found<br>
 */
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