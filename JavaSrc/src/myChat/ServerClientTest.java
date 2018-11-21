package myChat;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.JTextArea;
import org.junit.jupiter.api.Test;

/**
 * testing Junit case for client-server features, notice that we test client's
 * methods, if it works, both server and client work correctly
 * 
 * @author Evgeny
 *
 */
class ServerClientTest {
	static JTextArea textTest;// we'll use this to check the messages that is sent from the server to client
	static ServerCode testS;
	static ClientCode testC;

	/**
	 * testing the "connect" method
	 * 
	 * @throws InterruptedException
	 *             java compiler demands this, but in this scenario, this test
	 *             should not throw any exceptinos
	 */
	@Test
	void testConnectMe() throws InterruptedException {
		textTest = new JTextArea();
		testS = new ServerCode();
		testS.start(textTest);// starting the server
		testC = new ClientCode("127.0.0.1", ServerCode.port, textTest);// connecting the client to the server
		testC.connectMe("test");// trying to connect the client
		Thread.sleep(100);// because we're dealing with threads, this can take some time so we'll "freeze"
							// the running of the thread for 100 milliseconds
		assertTrue(ServerCode.allConnected.elementAt(0).getUserName().equals("test"));// testing whether the first
																						// element of "all connected
																						// threads" vector users name is
																						// "test" as the name of the
																						// client we just made going
																						// online
	}

	/**
	 * testing the public message method
	 * 
	 * @throws InterruptedException
	 *             java compiler demands this, but in this scenario, this test
	 *             should not throw any exceptinos
	 */
	@Test
	void testSendPublicMessage() throws InterruptedException {
		textTest = new JTextArea();
		testS = new ServerCode();
		testS.start(textTest);
		testC = new ClientCode("127.0.0.1", ServerCode.port, textTest);
		testC.connectMe("test");// connecting the "main" thread to the server
		JTextArea textTest_2 = new JTextArea();
		ClientCode testC_2 = new ClientCode("127.0.0.1", ServerCode.port, textTest_2);
		testC_2.connectMe("test_2");
		JTextArea textTest_3 = new JTextArea();
		ClientCode testC_3 = new ClientCode("127.0.0.1", ServerCode.port, textTest_3);
		testC_3.connectMe("test_3");// connected 2 more clients
		Thread.sleep(100);// sending this thread "to sleep" for 100 milliseconds to let all threads finish
							// their connection
		// setting both text areas of the client to be "" because we're intrested only
		// in the messgae wr are about to send
		textTest_2.setText("");
		textTest_3.setText("");
		testC.sendPublicMessage("test", "testingPublicMessage");// sending the message
		Thread.sleep(100);// sending this thread to sleep once again, this time to let the message be
							// received
		// checking whether the text both clients got match the message we sent
		assertTrue(textTest_2.getText().equals("test: testingPublicMessage" + "\n")
				&& textTest_3.getText().equals("test: testingPublicMessage" + "\n"));
	}

	/**
	 * testing the private message method
	 * 
	 * @throws InterruptedException
	 *             java compiler demands this, but in this scenario, this test
	 *             should not throw any exceptinos
	 */
	@Test
	void testSendPrivateMessage() throws InterruptedException {
		textTest = new JTextArea();
		testS = new ServerCode();
		testS.start(textTest);
		testC = new ClientCode("127.0.0.1", ServerCode.port, textTest);
		testC.connectMe("test");
		// connecting two clietns to the server
		JTextArea textTest_P = new JTextArea();
		ClientCode testC_P = new ClientCode("127.0.0.1", ServerCode.port, textTest_P);
		testC_P.connectMe("test_P");
		JTextArea textTest_P2 = new JTextArea();
		ClientCode testC_P2 = new ClientCode("127.0.0.1", ServerCode.port, textTest_P2);
		testC_P2.connectMe("test_P2");
		// sending this thread to sleep for 200 milliseconds to handle all connections
		Thread.sleep(200);
		// "emptying" both clients text area
		textTest_P.setText("");
		textTest_P2.setText("");
		// sending the private message
		testC.sendPrivateMessage("test", "test_P", "privateM");
		// sending this thread to sleep for 200 milliseconds to handle the message sent
		// and recived
		Thread.sleep(200);
		assertTrue(textTest_P.getText().equals("test: privateM" + "\n"));// checking that the client which we sent the
																			// message to got it
		assertTrue(textTest_P2.getText().equals(""));// checking that the other client didnt got it
	}

	/**
	 * testing the "allOnline" method
	 * 
	 * @throws InterruptedException
	 *             java compiler demands this, but in this scenario, this test
	 *             should not throw any exceptinos
	 */
	@Test
	void testAllOnline() throws InterruptedException {
		testS = new ServerCode();
		JTextArea textTestS = new JTextArea();
		testS.start(textTestS);
		testC = new ClientCode("127.0.0.1", ServerCode.port, textTest);
		testC.connectMe("test");
		// connecting 3 users
		JTextArea textTest2 = new JTextArea();
		ClientCode testC2 = new ClientCode("127.0.0.1", ServerCode.port, textTest2);
		testC2.connectMe("test2");
		JTextArea textTest3 = new JTextArea();
		ClientCode testC3 = new ClientCode("127.0.0.1", ServerCode.port, textTest3);
		testC3.connectMe("test3");
		JTextArea textTest4 = new JTextArea();
		ClientCode testC4 = new ClientCode("127.0.0.1", ServerCode.port, textTest4);
		testC4.connectMe("test4");
		Thread.sleep(200);// sending this thread to sleep for 200 milliseconds to handel all connections
		testC.allOnline("test");
		Thread.sleep(100);// sending this thread to sleep for 100 milliseconds to handel the communication
							// between server - client
		// checking whether the text area which the client got contains all user names
		// of all online chat members
		assertTrue(textTest.getText().contains("test") && textTest.getText().contains("test2")
				&& textTest.getText().contains("test3") && textTest.getText().contains("test4"));

	}

	/**
	 * testing the "disconnect" method
	 * 
	 * @throws InterruptedException
	 *             java compiler demands this, but in this scenario, this test
	 *             should not throw any exceptinos
	 */
	@Test
	void testDisConnectMe() throws InterruptedException {
		testS = new ServerCode();
		JTextArea textTestS = new JTextArea();
		testS.start(textTestS);
		testC = new ClientCode("127.0.0.1", ServerCode.port, textTest);
		testC.connectMe("test");
		Thread.sleep(100);// sending this thread to sleep for 200 milliseconds to handel connection
		assertFalse(ServerCode.allConnected.isEmpty());// making sure that the vector of "all connected threads" is NOT
														// empty as the client connected
		testC.disConnectMe("test");// disconnecting
		assertTrue(ServerCode.allConnected.isEmpty());// making sure that now the vector is indeed empty, i.e the client
														// is disconnected
	}
}
