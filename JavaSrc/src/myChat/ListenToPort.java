package myChat;

import java.io.IOException;
import java.net.*;

/**
 * this calss is the thread which created to "listen" to the certain port which
 * the server works on, this thread exists to avoid the blocking of GUI when the
 * server starts
 * 
 * @author Evgeny
 *
 */
public class ListenToPort extends Thread {
	private MultiClientHandler currentT;
	private ServerSocket serverSocket;

	/**
	 * constuctor which gets the server socket of the server and sets it to be
	 * threads server socket, this way the thread can "listen" to the chosen port
	 * and avoid blocking in servers GUI
	 * 
	 * @param serverSocket
	 *            servers socket
	 */
	public ListenToPort(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	/**
	 * the "main" method of this thread which listents to servers port while the
	 * server socket is open, every time a client connects to this port (and ip..)
	 * new "MultiClientHandler" opens to handle their requests
	 */
	public void run() {
		while (!this.serverSocket.isClosed()) {
			try {
				currentT = new MultiClientHandler(this.serverSocket.accept());
			} catch (IOException e) {
				System.out.println("error has occured: " + e.getMessage());
				return;
			}
			currentT.start();
		}
	}

}
