package com.iut.banque.test.converter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.converter.AccountConverter;
import com.iut.banque.converter.ClientConverter;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.*;
import com.opensymphony.xwork2.conversion.TypeConversionException;

import java.util.HashMap;

public class TestConverters {

    private AccountConverter accountConverter;
    private ClientConverter clientConverter;
    private IDao mockDao;

    @Before
    public void setUp() throws Exception {
        mockDao = mock(IDao.class);
        AccountConverter.setDao(mockDao);
        ClientConverter.setDao(mockDao);

        accountConverter = new AccountConverter();
        clientConverter = new ClientConverter();
    }

    // ---- AccountConverter ----

    @Test
    public void testAccountConvertFromStringSuccess() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 0, client);
        when(mockDao.getAccountById("FR0123456789")).thenReturn(compte);

        Object result = accountConverter.convertFromString(new HashMap<>(), new String[]{"FR0123456789"}, Compte.class);

        assertEquals(compte, result);
    }

    @Test(expected = TypeConversionException.class)
    public void testAccountConvertFromStringNotFound() {
        when(mockDao.getAccountById("UNKNOWN")).thenReturn(null);
        accountConverter.convertFromString(new HashMap<>(), new String[]{"UNKNOWN"}, Compte.class);
    }

    @Test
    public void testAccountConvertToString() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 0, client);

        String result = accountConverter.convertToString(new HashMap<>(), compte);

        assertEquals("FR0123456789", result);
    }

    @Test
    public void testAccountConvertToStringNull() {
        String result = accountConverter.convertToString(new HashMap<>(), null);
        assertNull(result);
    }

    // ---- ClientConverter ----

    @Test
    public void testClientConvertFromStringSuccess() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        when(mockDao.getUserById("j.doe1")).thenReturn(client);

        Object result = clientConverter.convertFromString(new HashMap<>(), new String[]{"j.doe1"}, Client.class);

        assertEquals(client, result);
    }

    @Test(expected = TypeConversionException.class)
    public void testClientConvertFromStringNotFound() {
        when(mockDao.getUserById("unknown")).thenReturn(null);
        clientConverter.convertFromString(new HashMap<>(), new String[]{"unknown"}, Client.class);
    }

    @Test
    public void testClientConvertToString() throws Exception {
        Client client = new Client("Doe", "John", "a", true, "j.doe1", "pwd", "123456789");

        String result = clientConverter.convertToString(new HashMap<>(), client);

        assertEquals(client.getIdentity(), result);
    }

    @Test
    public void testClientConvertToStringNull() {
        String result = clientConverter.convertToString(new HashMap<>(), null);
        assertNull(result);
    }
}