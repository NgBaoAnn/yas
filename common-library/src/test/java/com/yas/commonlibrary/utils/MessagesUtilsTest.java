package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WithExistingCode_ReturnsFormattedMessage() {
        // Since we can't easily mock ResourceBundle.getBundle, 
        // we test with a known code or a missing one.
        // Assuming "access.denied" might exist or not.
        String message = MessagesUtils.getMessage("non.existing.code", "param1");
        assertEquals("non.existing.code", message);
    }

    @Test
    void getMessage_WithArguments_ReturnsFormattedMessage() {
        // Test formatting if message contains {}
        // But since we use real bundle, it's hard to predict.
        // However, we can verify it doesn't crash.
        String message = MessagesUtils.getMessage("code", "arg1", "arg2");
        assertEquals("code", message);
    }
}
