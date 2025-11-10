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
public class TestsClientNumeroFormat {

    private final String input;
    private final boolean expected;

    public TestsClientNumeroFormat(String input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: checkFormatNumeroClient({0}) = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1234567890", true},
                {"12a456789", false},
                {"12#456789", false},
                {"12345678", false},
                {"12345678901", false}
        });
    }

    @Test
    public void testCheckFormatNumeroClient() {
        boolean result = Client.checkFormatNumeroClient(input);
        if (expected) {
            assertTrue("String " + input + " devrait être accepté", result);
        } else {
            assertFalse("String " + input + " ne devrait PAS être accepté", result);
        }
    }
}
