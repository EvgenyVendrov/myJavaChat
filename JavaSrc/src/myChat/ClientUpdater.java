package myChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JTextArea;

/**
 * this class is a thread class which "listens" to the server to get all
 * messages which users send through the chat
 * 
 * @author Evgeny
 *
 */
public class ClientUpdater extends Thread {
	private JTextArea textToGui;// clients GUI jtext to appedn text to the GUI from the server
	private ObjectInputStream inPut;// input stream to collect the messages
	private Socket stream;// we get this from the client to indicate whether the stream is still open

	/**
	 * constructor thats sets all fields of this thread
	 * 
	 * @param inPut
	 *            ObjectInputStream we get from the client, the direct socket from
	 *            the server
	 * @param textToGui
	 *            jtext area we get from clients GUI, used to append text to the GUI
	 * @param stream
	 *            the socket between the server and the client
	 */
	public ClientUpdater(ObjectInputStream inPut, JTextArea textToGui, Socket stream) {
		this.inPut = inPut;
		this.textToGui = textToGui;
		this.stream = stream;
	}

	/**
	 * the "main" method of this thread which "listens" to the server and appends
	 * the text we get from it to the GUI
	 */
	public void run() {
		CSMessage serverM;
		while (!this.stream.isClosed()) {// while the socket is indeed connected
			try {
				serverM = (CSMessage) this.inPut.readObject();// "collect" the message
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (serverM.getSort().equals("server")) {// make sure the server sent it
				textToGui.append(serverM.getMessage() + "\n");// append the message to the GUI

			}
		}
		close();// in case the socket is closed, close all thread features too

	}

	// closing every feature thread got which sould be closed after the connection
	// is closed
	private void close() {
		try {
			this.inPut.close();
		} catch (IOException e) {
			System.out.println("ERROR Occured: " + e.getMessage());
			return;
		}
		try {
			this.stream.close();
		} catch (IOException e) {
			System.out.println("ERROR Occured: " + e.getMessage());
			return;
		}
	}

}
