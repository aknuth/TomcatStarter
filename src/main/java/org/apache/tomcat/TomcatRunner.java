package org.apache.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.Random;


public abstract class TomcatRunner implements Runnable {

	protected static int SERVER_LISTENER_PORT;

	protected static String SERVER_SHUTDOWN_TOKEN = "SHUTDOWN";

	protected static boolean listenerMode = false;

	private Random random = null;

	protected final Configuration configuration;

	public TomcatRunner(Configuration configuration) {
		this.configuration = configuration;
		String stopPort = System.getProperty("stopport");
		if (stopPort != null) {
			try {
				SERVER_LISTENER_PORT = Integer.parseInt(stopPort);
				System.out.println("Stopport:" + SERVER_LISTENER_PORT);
			} catch (NumberFormatException e) {
				System.err.println("invalid stopport");
				System.exit(1);
			}
		} else if (configuration != null) {
			SERVER_LISTENER_PORT = configuration.getListenerPort();
		}
		listenerMode = (SERVER_LISTENER_PORT == 0) ? false : true;
	}

	public void run() {
		// Set up a server socket to wait on
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SERVER_LISTENER_PORT, 1, InetAddress.getByName("localhost"));
		} catch (IOException e) {
			System.err.println("StandardServer.await: create[" + SERVER_LISTENER_PORT + "]: " + e.getMessage());
			System.exit(1);
		}

		// Loop waiting for a connection and a valid command
		while (true) {

			// Wait for the next connection
			Socket socket = null;
			InputStream stream = null;
			try {
				socket = serverSocket.accept();
				socket.setSoTimeout(10 * 1000); // Ten seconds
				stream = socket.getInputStream();
			} catch (AccessControlException ace) {
				System.err.println("StandardServer.accept security exception: " + ace.getMessage() + ace.getMessage());
				continue;
			} catch (IOException e) {
				System.err.println("StandardServer.await: accept: " + e.getMessage());
				System.exit(1);
			}

			// Read a set of characters from the socket
			StringBuffer command = new StringBuffer();
			int expected = 1024; // Cut off to avoid DoS attack
			while (expected < SERVER_SHUTDOWN_TOKEN.length()) {
				if (random == null)
					random = new Random();
				expected += (random.nextInt() % 1024);
			}
			while (expected > 0) {
				int ch = -1;
				try {
					ch = stream.read();
				} catch (IOException e) {
					System.err.println("StandardServer.await: read: " + e.getMessage());
					ch = -1;
				}
				if (ch < 32) // Control character or EOF terminates loop
					break;
				command.append((char) ch);
				expected--;
			}

			// Close the socket now that we are done with it
			try {
				socket.close();
			} catch (IOException e) {
				;
			}

			// Match against our command string
			boolean match = command.toString().equals(SERVER_SHUTDOWN_TOKEN);
			if (match) {
				break;
			} else
				System.err.println("StandardServer.await: Invalid command '" + command.toString() + "' received");

		}

		// Close the server socket and return
		try {
			serverSocket.close();
			stopServer();
		} catch (IOException e) {
			;
		}
	}

	public abstract void stopServer();

}
