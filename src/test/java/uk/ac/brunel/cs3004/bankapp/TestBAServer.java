package uk.ac.brunel.cs3004.bankapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.brunel.cs3004.bankapp.server.BAServer;

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
	public void testTransactions() {
		assertEquals(1000, BAServer.getBalance("CLIENTA"));
		BAServer.setBalance("CLIENTA", 1200);
		assertEquals(1200, BAServer.getBalance("CLIENTA"));
		BAServer.setBalance("CLIENTC", -1000);
		assertEquals(-1000, BAServer.getBalance("CLIENTC"));
	}
}
