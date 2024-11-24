package uk.ac.brunel.cs3004.bankapp.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * The primary server class that launches the server-side application.
 * @author Hridyanshu Aatreya
 * @version 1.0
 */

public class BAServer {
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static String[] passwords = {"Hello", "12345", "password"};
	
	private static int balanceA = 1000;
	private static int balanceB = 1000;
	private static int balanceC = 1000;
	
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

	private static String getPassword(String clientId) {
		switch (clientId) {
		case "A":
			return passwords[0];
		case "B":
			return passwords[1];
		case "C":
			return passwords[2];
		default:
			return "Unknown client was requested.";
		}
	}
	
	public static int getBalance(String clientId) {
		switch (clientId) {
		case "A":
			return balanceA;
		case "B":
			return balanceB;
		case "C":
			return balanceC;
		default:
			return Integer.MIN_VALUE;
		}
	}
	
	public static void setBalance(String clientId, int amount) {
		boolean unknownClient = false;
		int oldAmount = 0;
		
		switch (clientId) {
		case "A":
			oldAmount = balanceA;
			balanceA = amount;
			break;
		case "B":
			oldAmount = balanceB;
			balanceB = amount;
			break;
		case "C":
			oldAmount = balanceC;
			balanceC = amount;
			break;
		default:
			unknownClient = true;
			break;
		}
		
		if (unknownClient)
			LOGGER.warn("Attempt made to update balance of an unknown client.");
		else
			LOGGER.info("`{}`'s balance was updated from {} to {}.", clientId, oldAmount, amount);
	}
	
	public static boolean verifyClientId(String clientId) {
		if (clientId.equalsIgnoreCase("A") || 
			clientId.equalsIgnoreCase("B") ||
			clientId.equalsIgnoreCase("C")) {
			return true;
		} else return false;
	}
	
	public static int getServerPort() {
		return BAServerPort;
	}
}
