package myChat;

import java.io.Serializable;

/**
 * the form of messages that are transported from clietns to server and vice
 * versa, every message contains a name field to identify the user who sent it,
 * sort which represents the sort of the request/response from client/server,
 * and the message itself (which can be empty string at times)
 * 
 * NOTE: this class is Serializable to make it possible passing it thorught the
 * stream between client and server
 * 
 * @author Evgeny
 *
 */
public class CSMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String sort;
	private String text;

	/**
	 * regular constructor which used to create a instance of this class
	 * 
	 * @param name
	 *            client user name
	 * @param sort
	 *            sort of message sent
	 * @param message
	 *            the message itself
	 */
	public CSMessage(String name, String sort, String message) {
		this.sort = sort;
		this.name = name;
		this.text = message;
	}

	/**
	 * clients user name getter
	 * 
	 * @return clients user name
	 */
	public String getName() {
		return name;
	}

	/**
	 * message sort getter
	 * 
	 * @return sort of the message
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * the message itself getter
	 * 
	 * @return the message itself, NOTE: may be empty strings in some messages sorts
	 */
	public String getMessage() {
		return text;
	}

}
