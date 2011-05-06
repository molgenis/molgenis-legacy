package servlets;

import ircbot.IRCClientData;
import ircbot.IRCHandler;
import ircbot.IRCJobStruct;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BotServlet extends Servlet{
  private static final long serialVersionUID = 943772779565141864L;

  IRCHandler getBotEntryPoint(){
      return (IRCHandler)getServletContext().getAttribute("bot");
  }
  
  @Override
  public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    OutputStream o = res.getOutputStream();
    boolean botinfo = true;
    boolean jobinfo = true;
    int botid = -1;
    int jobid = -1;
    String stream ="";
    try{
      botid = Integer.parseInt(req.getParameter("bot"));
    }catch(Exception e){
      botinfo=false;
    }
    try{
      jobid = Integer.parseInt(req.getParameter("id"));
      stream = req.getParameter("stream");
      botinfo=false;
    }catch(Exception e){
      jobinfo=false;
    }
    res.setContentType("text/html");
    if(botinfo){
      o.write(("<html><head><title>BOT Network</title></head><body>" +
          "<h2>Bot: "+botid+"</h2>").getBytes());
      o.write(("</body>").getBytes());
      o.flush();
      o.close();
    }else if(jobinfo){
      o.write(("<html><head><title>BOT Network</title></head><body>" +
          "<h2>Bot: " + botid + ", job: "+jobid + ", "+ stream + "</h2>").getBytes());
      o.write(("</body>").getBytes());
      o.flush();
      o.close();
    }else{
      o.write(("<html><head><title>BOT Network</title></head><body>" +
           "<h2>Bot Network Overview</h2>" +
           "Nr of bots:" + getBotEntryPoint().getAllConnectedHosts().size() + "<br>").getBytes());
      o.write(("<a href='dist/Bot.jar'><font color='green'>Download BOT Jar</font></a><br>").getBytes());
      o.write(("<table>").getBytes());
      o.write(("<tr><td width=150><b>Host</b></td><td width=150><b>Started</b></td><td width=500><b>Jobs</b></td></tr>").getBytes());
      for(IRCClientData d : getBotEntryPoint().getAllConnectedHosts()){
        o.write(("<tr>").getBytes());
        o.write((d.getHTMLString()).getBytes());
        o.write(("<td>").getBytes());
        if(d.getJobQueue().size() > 0){
          o.write(("<table>").getBytes());
          o.write(("<tr><td width=50><b>Stream</b></td><td width=350><b>Command</b></td><td width=120><b>Status</b></td></tr>").getBytes());
          for(IRCJobStruct j : d.getJobQueue()){
            o.write(("<tr><td>" + j.printStatus(d.getMyid()) + "</td></tr>").getBytes());
          }
          o.write(("</table>").getBytes());
        }else{
          o.write(("No Jobs").getBytes());  
        }
        o.write(("</td></tr>").getBytes());
      }
      o.write(("</table>").getBytes());
      o.write(("</body>").getBytes());
      o.flush();
      o.close();
    }
  }
}
