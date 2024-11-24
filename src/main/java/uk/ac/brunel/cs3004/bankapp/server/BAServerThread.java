package uk.ac.brunel.cs3004.bankapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/***
 * A server thread instance that deals with an incoming connection.
 * @author Hridyanshu Aatreya
 * @version 1.0
 */

public class BAServerThread extends Thread {
	private boolean authStatus;
	private Socket clientSocket;
	private String clientId;
	private String currentThreadName;
	private BATransactionManager transactionMgr;
	
	public BAServerThread(Socket clientSocket, String threadName, BATransactionManager transactionMgr) {
		super(threadName);
		this.transactionMgr = transactionMgr;
		this.clientSocket = clientSocket;
		this.currentThreadName = threadName;
		authStatus = false;
	}
	
	public void run() {
		BAServer.LOGGER.info("`{}` initialized", currentThreadName);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			
			while (!authStatus) {
				String clientNameAttempt = in.readLine();
				
				if (!BAServer.verifyClientId(clientNameAttempt)) { 
					out.println("UNKNOWN");
					BAServer.LOGGER.info("LOGIN: User tried to authenticate as unknown client ID `{}`", clientNameAttempt);
					continue; 
				} else {
					out.println(clientNameAttempt);
					BAServer.LOGGER.info("LOGIN: User is trying to authenticate as known client ID `{}`", clientNameAttempt);
				}
				
				boolean clientPasswordAttempt = BAServer.authenticate(clientNameAttempt, in.readLine());
				
				if (clientPasswordAttempt) {
					authStatus = true;
					this.clientId = clientNameAttempt;
					out.println("SUCCESSFUL");
					BAServer.LOGGER.info("LOGIN: User was successfully authenticated as `{}`", clientNameAttempt);
				} else {
					out.println("REJECTED");
					BAServer.LOGGER.info("LOGIN: User could not be successfully authenticated as `{}`", clientNameAttempt);
				}
			}
			
			String operation, response;
			
			while ((operation = in.readLine()) != null) {
				try {
					transactionMgr.acquireLock();
					response = transactionMgr.processInput(clientId, operation);
					out.println(response);
					transactionMgr.releaseLock();
				} catch (Exception e) {
					BAServer.LOGGER.error("Exception encountered", e);
				}
			}
			
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			BAServer.LOGGER.error("Exception encountered", e);
		}
	}
}
