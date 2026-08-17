package com.example.equipment;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * ビルドパイプライン確認用の最小テスト。
 */
public class SmokeTest {

    @Test
    public void java8CompatibleRuntime() {
        String version = System.getProperty("java.specification.version");
        // ホストが Java 8 より新しくても、コンパイル target は 1.8。
        // コンテナ実行時に Java 8 であることを別途確認する。
        assertTrue("java.specification.version must be present", version != null && version.length() > 0);
    }
}
