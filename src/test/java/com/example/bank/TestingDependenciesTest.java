package com.example.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class TestingDependenciesTest {

    @Test
    void junitAndMockitoAreAvailable() {
        AccountNumberGenerator generator = mock(AccountNumberGenerator.class);
        when(generator.next()).thenReturn("10123456789");

        assertEquals("10123456789", generator.next());
    }

    private interface AccountNumberGenerator {
        String next();
    }
}
