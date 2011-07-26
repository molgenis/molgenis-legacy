package core;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class BasicAcceptor implements Webserver.Acceptor {
	public Socket accept() throws IOException {
		return socket.accept();
	}

	public void destroy() throws IOException {
		socket.close();
	}

	public void init(Map<String,Object> inProperties, Map<String,Object> outProperties) throws IOException {
		Integer port = Webserver.DEF_PORT;
		if(inProperties.get(Webserver.ARG_PORT) != null){
			port = (Integer)(inProperties.get(Webserver.ARG_PORT));
		}
		String bindAddrStr = (String)inProperties.get(Webserver.ARG_BINDADDRESS);
		InetSocketAddress bindAddr = bindAddrStr != null?new InetSocketAddress(InetAddress.getByName(bindAddrStr),port):null;
		String backlogStr = (String)inProperties.get(Webserver.ARG_BACKLOG);		
		int backlog = backlogStr!=null?Integer.parseInt(backlogStr):-1;
		if (bindAddr != null) {
			socket = new ServerSocket();
			if (backlog < 0)
				socket.bind(bindAddr);
			else
				socket.bind(bindAddr, backlog);
		} else {
			if (backlog < 0)
				socket = new ServerSocket(port);
			else
				socket = new ServerSocket(port, backlog);
		}
		if (outProperties != null)
			if (socket.isBound())
				outProperties.put(Webserver.ARG_BINDADDRESS, socket.getInetAddress().getHostName());
			else
				outProperties.put(Webserver.ARG_BINDADDRESS, InetAddress.getLocalHost().getHostName());
	}

	public String toString() {
		return "basicAcceptor 0.1 " + socket;
	}

	private ServerSocket socket;
}