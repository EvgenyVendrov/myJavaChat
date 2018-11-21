package myChat;

import java.net.*;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JTextArea;
import java.io.*;

/**
 * this is the "backend" code of the server GUI, all of the methods and
 * implementations which the server has, occuring here
 * 
 * @author Evgeny
 *
 */
public class ServerCode {
	public final static int port = 6666;// we'll always make our server work on this port
	private ServerSocket serverSocket;
	public static Vector<MultiClientHandler> allConnected;// this vector will host all the connected servers
	private static JTextArea textToGui;// with this we'll append text into to GUI

	/**
	 * sets all servers fields, connects the "textToGui" to GUI's JtextArea,
	 * declares the vector and starts "lestning" to the port (without any code
	 * blocking using a thread)
	 * 
	 * @param textToGui
	 */
	public void start(JTextArea textToGui) {
		ServerCode.textToGui = textToGui;
		ServerCode.allConnected = new Vector<>();
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
			return;
		}
		ListenToPort listner = new ListenToPort(serverSocket);// creating this new thread to avoid blocking the code
		listner.start();// starting the thread to allow clients to connect

	}

	/**
	 * close servers socket in case the server is shut down
	 */
	public void close() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
			return;
		}
	}

	/**
	 * in case a client wants to connect, this method checks whether the name of
	 * client is in use, if not adds the thread which works with it to the vector
	 * and lets all clients know that new user is connected
	 * 
	 * @param name
	 *            the name of the client
	 * @param origin
	 *            a referense to the thread thats wroking with the client, we'll use
	 *            this if clients name is already taken to sent him a personal
	 *            message
	 */
	synchronized public static void Connect(String name, MultiClientHandler origin) {
		Iterator<MultiClientHandler> it = allConnected.iterator();
		while (it.hasNext()) {
			MultiClientHandler current = it.next();
			if (current.getUserName().equals(name)) {// in case the chosen name is already taken, we'll send the client
														// a message and stop the method
				try {
					origin.getOutPutStream().writeObject(new CSMessage("server", "server",
							"there is already a " + name + " in the chat please pick another name"));
					return;
				} catch (IOException e) {
					ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
					return;
				}
			}
		}
		allConnected.add(origin);// in case the method wasnt stopped i.e the chosen name isnt taken, we'll add
									// clients thread to the vector
		it = allConnected.iterator();
		while (it.hasNext()) {// sending all clietns the message that a new client is online
			MultiClientHandler current = it.next();
			try {
				current.getOutPutStream().writeObject(new CSMessage("server", "server", name + " is now logged in"));
			} catch (IOException e) {
				ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
				return;
			}

		}
		textToGui.append(name + " has connected to the chat" + "\n");// writting it on the server GUI
	}

	/**
	 * in case a client wants to disconnect, this method deletes clients thread from
	 * the vector and sending the message about it to all clietns
	 * 
	 * @param name
	 *            the name of the client we wish to delete
	 */
	synchronized public static void disConnect(String name) {
		Iterator<MultiClientHandler> it = allConnected.iterator();
		boolean hasRemoved = false;
		while (it.hasNext() && !hasRemoved) {// as long we havnt deleted the client, and we still got more elements in
												// the vector
			MultiClientHandler current = it.next();
			if (current.getUserName().equals(name)) {
				it.remove();
				hasRemoved = true;
			}
		}
		it = allConnected.iterator();
		while (it.hasNext()) {// sending the message about clients disconnection to everybody in the chat
			MultiClientHandler current = it.next();
			try {
				current.getOutPutStream().writeObject(new CSMessage("server", "server", name + " has left the chat"));
			} catch (IOException e) {
				ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
				return;
			}
		}
		textToGui.append(name + " has disconnected from the chat" + "\n");// writting it on the server GUI

	}

	/**
	 * in case client wants to see every member of the chat whose online, this
	 * method creats the string outout and sends it only to the requseting client
	 * 
	 * @param sender
	 *            requesting clients thread, used to send the information back
	 *            easier
	 */
	public static void allOnline(MultiClientHandler sender) {
		String outPut = "Online Users: \n";// creating the out put string which will always have the same begining
		Iterator<MultiClientHandler> it = allConnected.iterator();
		while (it.hasNext()) {// adding up all online members names to a one long string
			MultiClientHandler current = it.next();
			outPut += current.getUserName() + "\n";
		}
		try {// sending the string to the concrete client which has requested it
			sender.getOutPutStream().writeObject(new CSMessage("server", "server", outPut));
		} catch (IOException e) {
			ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
			return;
		}
		textToGui.append(sender.getUserName() + " wants all online members" + "\n");// writting it on the server GUI
	}

	/**
	 * sends given message to all online memebers
	 * 
	 * @param senderName
	 *            the name of the client who send the message
	 * @param message
	 *            the String messagse itself
	 */
	public static void messageAll(String senderName, String message) {
		Iterator<MultiClientHandler> it = allConnected.iterator();
		while (it.hasNext()) {// sending the message to every client connected
			MultiClientHandler current = it.next();
			try {
				current.getOutPutStream().writeObject(new CSMessage(senderName, "server", senderName + ": " + message));
			} catch (IOException e) {
				ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
				return;
			}
		}
		textToGui.append(senderName + " has sent  message to everybody" + "\n");// writting it on the server GUI
	}

	/**
	 * sends a message to chosen client (if there is one with the same user name)
	 * 
	 * @param senderName
	 *            name of the user who sent the message
	 * @param recivingName
	 *            the user name of who the message is to
	 * @param message
	 *            the String message itself
	 * @param origin
	 *            the thread from which we got this message
	 */
	public static void privateMessage(String senderName, String recivingName, String message,
			MultiClientHandler origin) {
		boolean messageSent = false;
		Iterator<MultiClientHandler> it = allConnected.iterator();
		while (it.hasNext() && !messageSent) {// looking for the name of the user who has the message is for
			MultiClientHandler current = it.next();
			if (current.getUserName().equals(recivingName)) {// in case we found
				try {
					current.getOutPutStream()
							.writeObject(new CSMessage(senderName, "server", senderName + ": " + message));
				} catch (IOException e) {
					ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
					return;
				}
				messageSent = true;
			}
		}
		if (!messageSent)// this will happen only if we havnt found the certain user we looked for, so
							// we're letting the user know that there is no such user name
			try {
				origin.getOutPutStream()
						.writeObject(new CSMessage("server", "server", "there is no such user name: " + recivingName));
			} catch (IOException e) {
				ServerCode.textToGui.append("error has occurred: " + e.getMessage() + "\n");
				return;
			}
		textToGui.append(origin.getUserName() + " has sent private message to " + senderName + "\n");// writting it on
																										// the server
																										// GUI

	}

}
