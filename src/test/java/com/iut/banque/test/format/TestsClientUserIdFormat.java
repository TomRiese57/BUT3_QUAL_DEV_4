package com.iut.banque.test.format;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.iut.banque.modele.Client;

@RunWith(Parameterized.class)
public class TestsClientUserIdFormat {

    private final String input;
    private final boolean expected;

    public TestsClientUserIdFormat(String input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: checkFormatUserIdClient({0}) = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a.utilisateur928", true},
                {"a.a1", true},
                {"32a.abc1", false},
                {"aaa.abc1", false},
                {"abc1", false},
                {"", false},
                {"a.138", false},
                {"a.bcdé1", false},
                {"a.abc01", false},
                {"a.ab.c1", false}
        });
    }

    @Test
    public void testCheckFormatUserIdClient() {
        boolean result = Client.checkFormatUserIdClient(input);
        if (expected) {
            assertTrue("String " + input + " devrait être accepté", result);
        } else {
            assertFalse("String " + input + " ne devrait PAS être accepté", result);
        }
    }
}
