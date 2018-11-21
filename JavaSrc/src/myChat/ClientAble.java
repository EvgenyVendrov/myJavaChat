package myChat;

/**
 * interface which represents a client, client code is implementing this
 * 
 * @author Evgeny
 *
 */
public interface ClientAble {
	/**
	 * every client has to be able to connect the server
	 * 
	 * @param userName
	 *            this clients user name
	 */
	public void connectMe(String userName);

	/**
	 * every client has to be able to disconnect from the server
	 * 
	 * @param userName
	 *            this clients user name
	 */
	public void disConnectMe(String userName);

	/**
	 * every client (in this chat at least..) has to be able to send a private
	 * message to another client
	 * 
	 * @param sendUserName
	 *            senders user name
	 * @param recive
	 *            recivers user name
	 * @param text
	 *            the message itself
	 */
	public void sendPrivateMessage(String sendUserName, String recive, String text);

	/**
	 * every client has to be able to demand all online user names
	 * 
	 * @param userName
	 *            user name of the client who sent the request
	 */
	public void allOnline(String userName);

	/**
	 * every client has to be able to send public message to all online chat members
	 * 
	 * @param sendUserName
	 *            user name of the client who sent the message
	 * @param text
	 *            the message itself
	 */
	public void sendPublicMessage(String sendUserName, String text);

}
