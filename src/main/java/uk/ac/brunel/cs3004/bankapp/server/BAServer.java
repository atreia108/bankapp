package uk.ac.brunel.cs3004.bankapp.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BAServer {
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static String[] passwords = {"Hello", "12345", "password"};
	private static int[] accounts = {1000, 1000, 1000};
	private static BATransactionManager transactionMgr;
	private static final int BAServerPort = 4545;
	
	public static void main(String[] args) throws IOException {
		ServerSocket BAServerSocket = null;
		boolean listening = true;
		
		BATransactionManager transactionMgr = new BATransactionManager();
		
		try {
			BAServerSocket = new ServerSocket(BAServerPort);
		} catch (IOException e) {
			LOGGER.error("EXCEPTION: Could not begin BAServer on port {}", BAServerPort);
		}
		
		BAServer.LOGGER.info("BAServer started on port {}", BAServerPort);
		
		while (listening) {
			new BAServerThread(BAServerSocket.accept(), "BAServerThread_1", transactionMgr).start();
			new BAServerThread(BAServerSocket.accept(), "BAServerThread_2", transactionMgr).start();
			new BAServerThread(BAServerSocket.accept(), "BAServerThread_3", transactionMgr).start();
			LOGGER.info("New BAServer thread was started");
		}
		
		BAServerSocket.close();
	}

	public static boolean authenticate (String clientId, String password) {
		return (getPassword(clientId).equals(password) ? true : false);
	}

	public static int getBalance(String clientId) {
		switch (clientId) {
		case "CLIENTA":
			return accounts[0];
		case "CLIENTB":
			return accounts[1];
		case "CLIENTC":
			return accounts[2];
		default:
			return Integer.MIN_VALUE;
		}
	}
	
	public static void setBalance(String clientId, int amount) {
		boolean unknownClient = false;
		
		switch (clientId) {
		case "CLIENTA":
			accounts[0] = amount;
			break;
		case "CLIENTB":
			accounts[1] = amount;
			break;
		case "CLIENTC":
			accounts[2] = amount;
			break;
		default:
			unknownClient = true;
			break;
		}
		
		if (unknownClient)
			LOGGER.warn("Attempt made to update balance of an unknown client.");
		else
			LOGGER.info("`{}` balance was updated to `{}`.", clientId, amount);
	}
	
	private static String getPassword(String clientId) {
		switch (clientId) {
		case "CLIENTA":
			return passwords[0];
		case "CLIENTB":
			return passwords[1];
		case "CLIENTC":
			return passwords[2];
		default:
			return "Unknown client was requested.";
		}
	}
	
	public static boolean verifyClientId(String clientId) {
		if (clientId.equalsIgnoreCase("CLIENTA") || 
			clientId.equalsIgnoreCase("CLIENTB") ||
			clientId.equalsIgnoreCase("CLIENTC")) {
			return true;
		} else return false;
	}
	public static int getServerPort() {
		return BAServerPort;
	}
}
