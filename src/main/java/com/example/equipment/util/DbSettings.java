package com.example.equipment.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DB 接続設定。環境変数またはシステムプロパティから取得する。
 *
 * <p>Java 7 NIO.2: DB パスの正規化に {@link Paths#get(String, String...)} を使用する。
 */
public final class DbSettings {

    private static final String DEFAULT_PATH = "/data/h2/equipment";

    private DbSettings() {
    }

    public static String getDbPath() {
        String raw = System.getenv("DB_PATH");
        if (raw == null || raw.trim().isEmpty()) {
            raw = System.getProperty("equipment.db.path", DEFAULT_PATH);
        }
        // Java 7 NIO.2: Path で正規化（余分なセパレータ除去）
        Path path = Paths.get(raw.trim());
        return path.toString();
    }

    public static String getJdbcUrl() {
        return "jdbc:h2:file:" + getDbPath()
                + ";AUTO_SERVER=FALSE;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=TRUE";
    }

    public static String getUser() {
        String user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = System.getProperty("equipment.db.user", "sa");
        }
        return user;
    }

    public static String getPassword() {
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = System.getProperty("equipment.db.password", "");
        }
        return password;
    }
}
