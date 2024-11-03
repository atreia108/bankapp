package uk.ac.brunel.cs3004.bankapp.server;

public class BATransactionManager {
	private int threadsInQueue = 0;
	private boolean inAccess = false;
	private String accessingThread = "";
	
	public synchronized String processInput(String request) {
		// For all recognized operations, TRY to convert integer to string. 
		// If not, log it as error and ask client to try again.
		if (request.equalsIgnoreCase("add")) {
			
		} else if (request.equalsIgnoreCase("subtract")) {
			
		} else if (request.equalsIgnoreCase("transfer")) {
			
		} else {
			// TODO: Any other case.
		}
		
		return "Hello, World";
	}
	
	public synchronized void addMoney(String clientId, int amount) {
		int clientBalance = BAServer.getBalance(clientId);
		BAServer.setBalance(clientId, clientBalance + amount);
		BAServer.LOGGER.info("`{}` added `{}` to their account", clientId, amount);
	}
	
	public synchronized void subtractMoney(String clientId, int amount) {
		int clientBalance = BAServer.getBalance(clientId);
		BAServer.setBalance(clientId, clientBalance - amount);
		BAServer.LOGGER.info("`{}` withdrew `{}` from their account", clientId, amount);
	}
	
	public synchronized void transferMoney(String client1Id, String client2Id, int amount) {
		int client1Balance = BAServer.getBalance(client1Id);
		int client2Balance = BAServer.getBalance(client2Id);
		
		BAServer.setBalance(client1Id, client1Balance - amount);
		BAServer.setBalance(client2Id, client2Balance + amount);
		BAServer.LOGGER.info("`{}` transferred `{}` to `{}`", client1Id, amount, client2Id);
	}
	
	public synchronized void acquireLock() {
		String self = Thread.currentThread().getName();
		BAServer.LOGGER.info("`{}` is attempting to get a lock", self);
		++threadsInQueue;
		
		while ( inAccess ) {
			BAServer.LOGGER.info("`{}` is waiting for a lock as `{}` is currently making a transaction",
					self, accessingThread);
			try {
				wait();
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
