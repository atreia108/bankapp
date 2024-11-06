package uk.ac.brunel.cs3004.bankapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.brunel.cs3004.bankapp.server.BAServer;

public class BAClient {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int BAServerPort = BAServer.getServerPort();
	private static boolean authStatus = false;
	
	public static void main(String[] args) throws IOException {
		Socket BAClientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			BAClientSocket = new Socket("localhost", BAServerPort);
			out = new PrintWriter(BAClientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(BAClientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			LOGGER.error("EXCEPTION: Unknown host was tried", e);
			System.exit(1);
		}
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		LOGGER.info("Client is fully initialized");
		
		System.out.println("Welcome to WLFB Bank Application.");
		
		while (!authStatus) {
			System.out.println("Please enter your Client ID to begin.");
			System.out.print("> ");
			String clientNameAttempt = stdIn.readLine();
			out.println(clientNameAttempt);
			LOGGER.info("CLIENT: Sent \"{}\" to server", clientNameAttempt);
			
			String clientNameResponse = in.readLine();
			LOGGER.info("SERVER: Responded with \"{}\" to client", clientNameResponse);
			
			if (clientNameResponse.equals("UNKNOWN")) {
				System.out.println("The client name you entered was not recognized. Please try again.");
				continue;
			}
			
			System.out.println("Now, enter your password.");
			System.out.print("> ");
			String clientPasswordAttempt = stdIn.readLine();
			out.println(clientPasswordAttempt);
			
			if (in.readLine().equals("REJECTED")) {
				System.out.println("The password is incorrect. Please try again.");
				continue;
			}
			
			authStatus = true;
			System.out.println("Login successful. Welcome, " + clientNameAttempt);
		}
		
		while (true) {
			System.out.println("What operation would you like to execute?\n1. ADD BALANCE\n2. SUBTRACT BALANCE\n3. TRANSFER BALANCE");
			String operation = stdIn.readLine();
			
			if (operation.equalsIgnoreCase("EXIT")) {
				System.out.println("Goodbye!");
				LOGGER.info("CLIENT: Terminating application");
				break; 
			}
			
			out.println(operation);
			LOGGER.info("CLIENT: Sent \"{}\" to server", operation);
			String response = in.readLine();
			LOGGER.info("SERVER: Responded with \"{}\" to client", operation);
			System.out.println(response);
		}
		
		out.close();
		in.close();
		stdIn.close();
		BAClientSocket.close();
	}
}
