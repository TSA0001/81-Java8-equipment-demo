package com.example.equipment.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordUtilTest {

    @Test
    public void hashProduces64HexChars() {
        String hash = PasswordUtil.hash("password");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    public void sameInputProducesSameHash() {
        assertEquals(PasswordUtil.hash("abc"), PasswordUtil.hash("abc"));
    }

    @Test
    public void differentInputProducesDifferentHash() {
        assertFalse(PasswordUtil.hash("abc").equals(PasswordUtil.hash("ABC")));
    }

    @Test
    public void matchesReturnsTrueForCorrectPassword() {
        String hash = PasswordUtil.hash("admin123");
        assertTrue(PasswordUtil.matches("admin123", hash));
    }

    @Test
    public void matchesReturnsFalseForWrongPassword() {
        String hash = PasswordUtil.hash("admin123");
        assertFalse(PasswordUtil.matches("wrong", hash));
    }

    @Test
    public void matchesReturnsFalseForNullInputs() {
        assertFalse(PasswordUtil.matches(null, "somehash"));
        assertFalse(PasswordUtil.matches("plain", null));
    }

    /** seed.sql の admin ハッシュと一致することを確認 */
    @Test
    public void adminSeedHashMatches() {
        assertTrue(PasswordUtil.matches("admin123",
                "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"));
    }

    /** seed.sql の user1 ハッシュと一致することを確認 */
    @Test
    public void user1SeedHashMatches() {
        assertTrue(PasswordUtil.matches("user1234",
                "831c237928e6212bedaa4451a514ace3174562f6761f6a157a2fe5082b36e2fb"));
    }
}
