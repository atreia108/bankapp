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
		assertTrue(BAServer.authenticate("CLIENTA", PASSWORD_CLIENT_A));
		assertTrue(BAServer.authenticate("CLIENTB", PASSWORD_CLIENT_B));
		assertTrue(BAServer.authenticate("CLIENTC", PASSWORD_CLIENT_C));
		
		assertFalse(BAServer.authenticate("CLIENTA", "hell0"));
		assertFalse(BAServer.authenticate("CLIENTB", "1"));
		assertFalse(BAServer.authenticate("CLIENTC", "Pittsburgh"));
		assertFalse(BAServer.authenticate("CLIENTA", null));
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
		String valid007 = "Transfer CLIENTA 256";
		String valid008 = "TRANSFER CLIENTB 512";
		String valid009 = "TraNSFer CLIENTB 1024";
		
		String invalid001 = "Transfer 256";
		String invalid002 = "subtract one";
		
		transactionMgr.processInput("CLIENTA", valid001);
		assertEquals(1008, BAServer.getBalance("CLIENTA"));
		transactionMgr.processInput("CLIENTA", valid004);
		assertEquals(808, BAServer.getBalance("CLIENTA"));
		transactionMgr.processInput("CLIENTB", valid007);
		assertEquals(1064, BAServer.getBalance("CLIENTA"));
		assertEquals(744, BAServer.getBalance("CLIENTB"));
		
		assertEquals("The client you attempted to transfer balance to does not exist!",
				transactionMgr.processInput("CLIENTA", invalid001));
		
		assertEquals("You submitted an unrecognized transaction request. Please provide a valid request!",
				transactionMgr.processInput("CLIENTC", invalid002));
	}
	
	@Test
	public void testValidClientName() {
		assertTrue(BAServer.verifyClientId("CLIENTA"));
		assertTrue(BAServer.verifyClientId("CLIENTB"));
		assertTrue(BAServer.verifyClientId("CLIENTC"));
		assertFalse(BAServer.verifyClientId("CLIENTD"));
	}
}
