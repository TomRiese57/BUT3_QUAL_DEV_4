package com.iut.banque.test.constants;

import static org.junit.Assert.*;
import org.junit.Test;
import com.iut.banque.constants.LoginConstants;

public class TestLoginConstants {

    @Test
    public void testUserIsConnected() {
        assertEquals(1, LoginConstants.USER_IS_CONNECTED);
    }

    @Test
    public void testManagerIsConnected() {
        assertEquals(2, LoginConstants.MANAGER_IS_CONNECTED);
    }

    @Test
    public void testLoginFailed() {
        assertEquals(LoginConstants.LOGIN_FAILED, -1);
    }

    @Test
    public void testError() {
        assertEquals(LoginConstants.ERROR, -2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCannotInstantiate() throws Exception {
        java.lang.reflect.Constructor<LoginConstants> c =
                LoginConstants.class.getDeclaredConstructor();
        c.setAccessible(true);
        try {
            c.newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (UnsupportedOperationException) e.getCause();
        }
    }
}