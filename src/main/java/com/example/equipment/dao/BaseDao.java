package com.example.equipment.dao;

import java.sql.SQLException;

/**
 * DAO 共通基底クラス。
 *
 * <p>Java 5 の varargs を使い、{@code AutoCloseable} の配列を受け取って
 * まとめてクローズする {@link #closeQuietly(AutoCloseable...)} を提供する。
 * 各 DAO の closeQuietly コピペを排除する。
 */
public abstract class BaseDao {

    /**
     * 渡されたリソースを後ろから順にクローズする。
     * null 要素はスキップし、発生した例外はすべて無視する。
     *
     * @param resources クローズ対象（null 可）
     */
    protected void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ignored) {
                    // ignore
                }
            }
        }
    }
}
