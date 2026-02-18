package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ResultatSuppression;
import com.iut.banque.modele.*;

public class TestResultatSuppression {

    private ResultatSuppression action;

    @Before
    public void setUp() {
        action = new ResultatSuppression();
    }

    @Test
    public void testGetSetCompte() {
        CompteSansDecouvert compte = new CompteSansDecouvert();
        action.setCompte(compte);
        assertEquals(compte, action.getCompte());
    }

    @Test
    public void testGetSetClient() {
        Client client = new Client();
        action.setClient(client);
        assertEquals(client, action.getClient());
    }

    @Test
    public void testGetSetCompteInfo() {
        action.setCompteInfo("FR0123456789");
        assertEquals("FR0123456789", action.getCompteInfo());
    }

    @Test
    public void testGetSetUserInfo() {
        action.setUserInfo("John Doe (123456789)");
        assertEquals("John Doe (123456789)", action.getUserInfo());
    }

    @Test
    public void testSetErrorMessageEmpty() {
        action.setErrorMessage("");
        assertFalse(action.isError());
        assertEquals("", action.getErrorMessage());
    }

    @Test
    public void testSetErrorMessageNull() {
        action.setErrorMessage(null);
        assertFalse(action.isError());
        assertNull(action.getErrorMessage());
    }

    @Test
    public void testSetErrorMessageWithValue() {
        action.setErrorMessage("TECHNICAL");
        assertTrue(action.isError());
        assertEquals("TECHNICAL", action.getErrorMessage());
    }

    @Test
    public void testGetSetError() {
        action.setError(true);
        assertTrue(action.isError());
        action.setError(false);
        assertFalse(action.isError());
    }

    @Test
    public void testGetSetAccount() {
        action.setAccount(true);
        assertTrue(action.isAccount());
        action.setAccount(false);
        assertFalse(action.isAccount());
    }
}