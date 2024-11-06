package uk.ac.brunel.cs3004.bankapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BAServerThread extends Thread {
	private boolean authState;
	private Socket clientSocket;
	private String clientId;
	private String currentThreadName;
	private BATransactionManager transactionMgr;
	
	public BAServerThread(Socket clientSocket, String clientId, String threadName) {
		transactionMgr = BAServer.getTransactionManager();
		this.clientSocket = clientSocket;
		this.clientId = clientId;
		this.currentThreadName = threadName;
		authState = false;
	}
	
	public void run() {
		BAServer.LOGGER.info("`{}` initialized", currentThreadName);
		try {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter stdOut = new PrintWriter(clientSocket.getOutputStream(), true);
			String input, response;
			
			while (!authState) {
				stdOut.println("Welcome to the WLFB Bank Application, CLIENT " + clientId + ".\n"
						+ "Please login by entering your password below.");
				String attempt = stdIn.readLine();
				if (BAServer.authenticate(clientId, attempt)) authState = true;
			}
			
			while ((input = stdIn.readLine()) != null) {
				try {
					transactionMgr.acquireLock();
					response = transactionMgr.processInput(clientId, input);
					transactionMgr.releaseLock();
				} catch (Exception e) {
					BAServer.LOGGER.error("Exception encountered", e);
				}
			}
			
			stdOut.close();
			stdIn.close();
			clientSocket.close();
		} catch (IOException e) {
			BAServer.LOGGER.error("Exception encountered", e);
		}
	}
}
