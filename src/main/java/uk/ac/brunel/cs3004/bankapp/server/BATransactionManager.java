package uk.ac.brunel.cs3004.bankapp.server;

public class BATransactionManager {
	private int threadsInQueue = 0;
	private boolean inAccess = false;
	private String accessingThread = "";
	
	public synchronized String processInput(String clientId, String request) {
		String[] requestDetails = request.split(" ");
		String response = "";
		
		if (requestDetails[0].equalsIgnoreCase("add")) {
			try {
				addMoney(clientId, Integer.parseInt(requestDetails[1]));
				response = requestDetails[1] + " was added to " + clientId + "\'s account balance.";
				BAServer.LOGGER.info("Transaction Request Processed: ADD");
			} catch(Exception e) {
				response = "You submitted an unrecognized transaction request. Please provide a valid request!";
				BAServer.LOGGER.error("Transaction Request Denied: Exception encountered", e);
				return response;
			}
		} else if (requestDetails[0].equalsIgnoreCase("subtract")) {
			try {
				subtractMoney(clientId, Integer.parseInt(requestDetails[1]));
				response = requestDetails[1] + " was subtracted from " + clientId + "\'s account balance.";
				BAServer.LOGGER.info("Transaction Request Processed: SUBTRACT");
			} catch (Exception e) {
				response = "You submitted an unrecognized transaction request. Please provide a valid request!";
				BAServer.LOGGER.error("Transaction Request Denied: Exception encountered", e);
				return response;
			}
		} else if (requestDetails[0].equalsIgnoreCase("transfer")) {
			try {
				if (!BAServer.verifyClientId(requestDetails[1])) {
					response = "The client you attempted to transfer balance to does not exist!";
					BAServer.LOGGER.info("Transaction Request Denied: TRANSFER to unknown client ID `{}` attempted", requestDetails[1]);
					return response;
				}
				
				transferMoney(clientId, requestDetails[1], Integer.parseInt(requestDetails[2]));
				response = clientId + " transferred " + requestDetails[2] + " to " + requestDetails[1] + "\'s account.";
				BAServer.LOGGER.info("Transaction Request Processed: TRANSFER");
			} catch (Exception e) {
				response = "You submitted an unrecognized transaction request. Please provide a valid request!";
				BAServer.LOGGER.error("Transaction Request Denied: Exception encountered", e);
				return response;
			}
		} else {
			return "You submitted an unrecognized transaction request. Please provide a valid request!";
		}
		
		return response;
	}
	
	public synchronized void addMoney(String clientId, int amount) {
		int clientBalance = BAServer.getBalance(clientId);
		BAServer.setBalance(clientId, clientBalance + amount);
		BAServer.LOGGER.info("`{}` added {} to their account", clientId, amount);
	}
	
	public synchronized void subtractMoney(String clientId, int amount) {
		int clientBalance = BAServer.getBalance(clientId);
		BAServer.setBalance(clientId, clientBalance - amount);
		BAServer.LOGGER.info("`{}` withdrew {} from their account", clientId, amount);
	}
	
	public synchronized void transferMoney(String client1Id, String client2Id, int amount) {
		int client1Balance = BAServer.getBalance(client1Id);
		int client2Balance = BAServer.getBalance(client2Id);
		
		BAServer.setBalance(client1Id, client1Balance - amount);
		BAServer.setBalance(client2Id, client2Balance + amount);
		BAServer.LOGGER.info("`{}` transferred {} to `{}`", client1Id, amount, client2Id);
	}
	
	public synchronized void acquireLock() {
		String self = Thread.currentThread().getName();
		BAServer.LOGGER.info("`{}` is attempting to get a lock", self);
		System.out.println("I AM HERE AND ABOUT TO MAYBE WAIT.");
		++threadsInQueue;
		
		while ( inAccess ) {
			
			BAServer.LOGGER.info("`{}` is waiting for a lock as `{}` is currently making a transaction",
					self, accessingThread);
			try {
				wait();
				System.out.println("I AM HERE AND WAITING");
			} catch (InterruptedException e) { BAServer.LOGGER.warn("Exception encountered", e); }
		}
		
		--threadsInQueue;
		inAccess = true;
		accessingThread = self;
		BAServer.LOGGER.info("`{}` has got a lock", self);
	}
	
	public synchronized void releaseLock() {
		String self = Thread.currentThread().getName();
		inAccess = false;
		notifyAll();
		BAServer.LOGGER.info("`{}` has released the lock", self);
	}

	public int getThreadsInQueue() {
		return threadsInQueue;
	}
}
