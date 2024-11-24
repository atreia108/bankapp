package uk.ac.brunel.cs3004.bankapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.brunel.cs3004.bankapp.server.BAServer;
import uk.ac.brunel.cs3004.bankapp.server.BATransactionManager;

public class TestBAServer {
	private final String PASSWORD_CLIENT_A = "Hello";
	private final String PASSWORD_CLIENT_B = "12345";
	private final String PASSWORD_CLIENT_C = "password";
	
	@Test
	public void testPasswordRetrieval() {
		assertTrue(BAServer.authenticate("A", PASSWORD_CLIENT_A));
		assertTrue(BAServer.authenticate("B", PASSWORD_CLIENT_B));
		assertTrue(BAServer.authenticate("C", PASSWORD_CLIENT_C));
		
		assertFalse(BAServer.authenticate("A", "hell0"));
		assertFalse(BAServer.authenticate("B", "1"));
		assertFalse(BAServer.authenticate("C", "Pittsburgh"));
		assertFalse(BAServer.authenticate("A", null));
	}

	@Test
	public void testInputProcessing() {
		BATransactionManager transactionMgr = new BATransactionManager();
		String valid001 = "ADD 8";
		String valid002 = "aDD 16";
		String valid003 = "add 32";
		String valid004 = "subtract 200";
		String valid005 = "SUBTRACT 250";
		String valid006 = "SubtraCT 301";
		String valid007 = "Transfer A 256";
		String valid008 = "TRANSFER B 512";
		String valid009 = "TraNSFer B 1024";
		
		String invalid001 = "Transfer 256";
		String invalid002 = "subtract one";
		
		transactionMgr.processInput("A", valid001);
		assertEquals(1008, BAServer.getBalance("A"));
		transactionMgr.processInput("A", valid004);
		assertEquals(808, BAServer.getBalance("A"));
		transactionMgr.processInput("B", valid007);
		assertEquals(1064, BAServer.getBalance("A"));
		assertEquals(744, BAServer.getBalance("B"));
		
		assertEquals("The client you attempted to transfer balance to does not exist!",
				transactionMgr.processInput("A", invalid001));
		
		assertEquals("You submitted an unrecognized transaction request. Please provide a valid request!",
				transactionMgr.processInput("C", invalid002));
	}
	
	@Test
	public void testValidClientName() {
		assertTrue(BAServer.verifyClientId("A"));
		assertTrue(BAServer.verifyClientId("B"));
		assertTrue(BAServer.verifyClientId("C"));
		assertFalse(BAServer.verifyClientId("D"));
	}
}
