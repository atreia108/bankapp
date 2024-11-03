package uk.ac.brunel.cs3004.bankapp.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BAServer {
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static String[] passwords = {"Hello", "12345", "password"};
	private static int[] accounts = {1000, 1000, 1000};
	private static BATransactionManager transactionMgr;
	
	public static void main(String[] args) {
		
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
	
	public static BATransactionManager getTransactionManager() {
		return transactionMgr;
	}
}
