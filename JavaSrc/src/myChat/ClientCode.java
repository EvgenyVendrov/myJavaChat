package myChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

/**
 * this class is the backend of the client GUI, all communication between the
 * server and the client is managed here
 * 
 * @author Evgeny
 *
 */
public class ClientCode implements ClientAble {
	private Socket streamSocketWithServer;
	private ObjectInputStream inPut;
	private ObjectOutputStream outPut;
	private ClientUpdater updater;// the thread which "listens" to the server to get all messages
	private JTextArea textToGui;// we'll use this to write on clients GUI

	/**
	 * client code side regular constructor
	 * 
	 * @param ip
	 *            servers IP
	 * @param port
	 *            servers port
	 * @param textToGui
	 *            jtext area which used to append text to clients GUI
	 */
	public ClientCode(String ip, int port, JTextArea textToGui) {
		this.textToGui = textToGui;// first thing we're setting is the jtext area, this way, even if there is a
									// problem with the connection(or any other exception thrown) we'll still be
									// able to write text to the GUI
		try {
			this.streamSocketWithServer = new Socket(ip, port);
		} catch (UnknownHostException e) {
			textToGui.append("ERROR: " + e.getMessage() + "\n");
			return;
		} catch (IOException e) {
			textToGui.append("ERROR: " + e.getMessage() + "\n");
			return;
		}
		try {
			this.inPut = new ObjectInputStream(this.streamSocketWithServer.getInputStream());
		} catch (IOException e) {
			textToGui.append("ERROR: " + e.getMessage() + "\n");
			return;
		}
		try {
			this.outPut = new ObjectOutputStream(this.streamSocketWithServer.getOutputStream());
		} catch (IOException e) {
			textToGui.append("ERROR: " + e.getMessage() + "\n");
			return;
		}
		this.updater = new ClientUpdater(this.inPut, textToGui, this.streamSocketWithServer);// creating the thread, and
																								// starting it to start
																								// // listening to the
																								// server
		updater.start();
	}

	/**
	 * if we got an "event" of connection from GUI, we'll send the server a message
	 * which says "<userName> wants to connect"
	 */
	@Override
	public void connectMe(String userName) {
		if (this.streamSocketWithServer != null)// this will be done only in a case the client is indeed connected to
												// the server, there is no "else" in this method because the error will
												// be printed from the constructor
			try {
				this.outPut.writeObject(new CSMessage(userName, "connect", ""));
			} catch (IOException e) {
				textToGui.append("ERROR: " + e.getMessage());
				return;
			}
	}

	/**
	 * if we got an "event" of disconnection from GUI, we'll send the server a
	 * message which says "<userName> wants to disconnect"
	 */
	@Override
	public void disConnectMe(String userName) {
		if (this.streamSocketWithServer != null) {// this will be done only in a case the socket is indeed connected
			try {
				this.outPut.writeObject(new CSMessage(userName, "disconnect", ""));
			} catch (IOException e) {
				textToGui.append("ERROR: " + e.getMessage());
				return;
			}
			this.textToGui.append("you left the chat" + "\n");
		} else {
			textToGui.append("ERORR: you are NOT connected" + "\n");// in case the socket isnt connected we'll print
																	// this
		}
	}

	/**
	 * if we got an "event" of sending a private message from GUI, we'll send the
	 * server a message which says "<userName> wants to send a private message to
	 * <userName2>"
	 */
	@Override
	public void sendPrivateMessage(String sendUserName, String recive, String text) {
		if (this.streamSocketWithServer != null)// this will be done only in a case the socket is indeed connected
			try {
				this.outPut.writeObject(new CSMessage(sendUserName, recive, text));
			} catch (IOException e) {
				textToGui.append("ERROR: " + e.getMessage() + "\n");
				return;
			}
		else {
			textToGui.append("ERORR: you are NOT connected" + "\n");// in case the socket isnt connected we'll print
			// this
		}
	}

	/**
	 * if we got an "event" of reciving all nuserNames of all users who are online
	 * from GUI, we'll send the server a message which says "<userName> wants to see
	 * all online chat members"
	 * 
	 */
	@Override
	public void allOnline(String userName) {
		if (this.streamSocketWithServer != null)// this will be done only in a case the socket is indeed connected
			try {
				this.outPut.writeObject(new CSMessage(userName, "allConnected", ""));
			} catch (IOException e) {
				textToGui.append("ERROR: " + e.getMessage() + "\n");
				return;
			}
		else {
			textToGui.append("ERORR: you are NOT connected" + "\n");// in case the socket isnt connected we'll print
			// this
		}
	}

	/**
	 * if we got an "event" of sending public message to all users who are online
	 * from GUI, we'll send the server a message which says "<userName> wants to
	 * send a message to all online users"
	 */
	@Override
	public void sendPublicMessage(String userName, String text) {
		if (this.streamSocketWithServer != null)// this will be done only in a case the socket is indeed connected
			try {
				this.outPut.writeObject(new CSMessage(userName, "public", text));
			} catch (IOException e) {
				textToGui.append("ERROR: " + e.getMessage() + "\n");
				return;
			}
		else {
			textToGui.append("ERORR: you are NOT connected" + "\n");// in case the socket isnt connected we'll print
			// this
		}
	}

	// private method to close all class features
	public void close() {
		try {
			this.inPut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.outPut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.streamSocketWithServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
