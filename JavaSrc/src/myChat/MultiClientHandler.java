package myChat;

import java.io.*;
import java.net.*;

/**
 * this class is the thread which opens with every client who connects to the
 * server, the communictaion between the server and the client happens through
 * this thread only, every thread has a socket with the client, input and output
 * object stream and a name which represents users user name in the chat
 * 
 * @author Evgeny
 *
 */
public class MultiClientHandler extends Thread {
	private Socket streamSocketWithClient;
	private ObjectOutputStream outPut;
	private ObjectInputStream inPut;
	private String userName = "deafultName";// starting every thread from this default name untill the user creats hes
											// own name

	/**
	 * cnostructor which sets all data members needed for thread work
	 * 
	 * @param currentStreamSocket
	 *            getting the stream socket with this certain client which the
	 *            thread is working with
	 * 
	 * 
	 */
	public MultiClientHandler(Socket currentStreamSocket) {
		this.streamSocketWithClient = currentStreamSocket;
		try {
			this.outPut = new ObjectOutputStream(this.streamSocketWithClient.getOutputStream());
		} catch (IOException e) {
			System.out.println("ERROR Occured: " + e.getMessage());
			return;
		}
		try {
			this.inPut = new ObjectInputStream(this.streamSocketWithClient.getInputStream());
		} catch (IOException e) {
			System.out.println("ERROR Occured: " + e.getMessage());
			return;
		}
	}

	/**
	 * the "main" method of this thread which gets messages from the client and
	 * breaks them to parts to indicate which service is wanted from the sever,
	 * after figuring out what service is wanted the thread
	 */
	public void run() {
		CSMessage messageFromClient;
		while (!this.streamSocketWithClient.isClosed()) {// keep "lestning" while the client is connected
			try {
				messageFromClient = (CSMessage) inPut.readObject();// getting a message
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("ERROR Occured: " + e.getMessage());
				this.close();
				return;
			}
			this.userName = messageFromClient.getName();// checking which service the client wants
			switch (messageFromClient.getSort()) {
			case "connect":
				connect(messageFromClient);
				break;
			case "disconnect":
				disConnect(messageFromClient);
				break;
			case "allConnected":
				allConnected();
				break;
			case "public":
				messageAll(messageFromClient);
				break;
			default:
				privateMessage(messageFromClient);
				break;
			}
		}
		this.close();// closing all thread "functions" if the stream socket with client is closed
	}

	// closing every feature thread got which sould be closed after the connection
	// is closed

	private void close() {
		try {
			this.inPut.close();
		} catch (IOException e) {
			System.out.println("IO Excpetion occured " + e.getMessage());
			return;
		}
		try {
			this.outPut.close();
		} catch (IOException e) {
			System.out.println("TO Execption occured " + e.getMessage());
			return;
		}
		try {
			this.streamSocketWithClient.close();
		} catch (IOException e) {
			System.out.println("TO Execption occured " + e.getMessage());
			return;
		}
	}

	// ***prviate methods which activate servers static methods for every sort of
	// message
	private void connect(CSMessage m) {
		ServerCode.Connect(m.getName(), this);
	}

	private void disConnect(CSMessage m) {
		ServerCode.disConnect(m.getName());
	}

	private void messageAll(CSMessage m) {
		ServerCode.messageAll(m.getName(), m.getMessage());
	}

	private void allConnected() {
		ServerCode.allOnline(this);
	}

	private void privateMessage(CSMessage m) {
		ServerCode.privateMessage(m.getName(), m.getSort(), m.getMessage(), this);
	}

	/**
	 * clients userName getter
	 * 
	 * @return clietns chosen user name
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * clients out put stream getter
	 * 
	 * @return clietns out put stream
	 */
	public ObjectOutputStream getOutPutStream() {
		return this.outPut;
	}
}
