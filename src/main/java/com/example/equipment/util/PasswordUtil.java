package com.example.equipment.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * パスワードのハッシュ化ユーティリティ（SHA-256）。
 * 開発・検証用途のみ。本番環境では BCrypt 等のソルト付きアルゴリズムを使用すること。
 */
public final class PasswordUtil {

    private PasswordUtil() {
        // utility class
    }

    /**
     * 平文パスワードを SHA-256 でハッシュ化して16進数文字列で返す。
     *
     * @param plainPassword 平文パスワード
     * @return SHA-256 ハッシュ値（小文字16進数、64文字）
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("password must not be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 は Java SE 7 以降で必須実装のため発生しない
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * 平文パスワードとハッシュ値が一致するか検証する。
     *
     * @param plainPassword 平文パスワード
     * @param hashedPassword 保存済みハッシュ値
     * @return 一致すれば true
     */
    public static boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return hash(plainPassword).equals(hashedPassword);
    }
}
