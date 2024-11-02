package uk.ac.brunel.cs3004.bankapp.server;

import java.net.Socket;

public class BAServerThread extends Thread {
	private enum SessionState { UNAUTHENTICATED, AUTHENTICATED }
	private Socket clientSocket;
	
	public void run() {
		
	}
	

}
