package com.example.equipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.equipment.model.UserAccount;
import com.example.equipment.util.ConnectionFactory;
import com.example.equipment.util.PasswordUtil;

public class UserDao extends BaseDao {

    public List<UserAccount> findAllActive() throws SQLException {
        String sql = "SELECT USER_ID, LOGIN_ID, USER_NAME, ROLE FROM USERS "
                + "WHERE DELETED = FALSE ORDER BY USER_ID";
        List<UserAccount> list = new ArrayList<UserAccount>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    public UserAccount findById(Long userId) throws SQLException {
        String sql = "SELECT USER_ID, LOGIN_ID, USER_NAME, ROLE FROM USERS "
                + "WHERE USER_ID = ? AND DELETED = FALSE";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setLong(1, userId.longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    /**
     * ログイン ID とパスワードで認証する。
     *
     * @param loginId   ログイン ID
     * @param plainPassword 平文パスワード
     * @return 認証成功時は UserAccount、失敗時は null
     */
    public UserAccount authenticate(String loginId, String plainPassword) throws SQLException {
        String sql = "SELECT USER_ID, LOGIN_ID, USER_NAME, ROLE, PASSWORD_HASH FROM USERS "
                + "WHERE LOGIN_ID = ? AND DELETED = FALSE";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, loginId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("PASSWORD_HASH");
                if (PasswordUtil.matches(plainPassword, storedHash)) {
                    UserAccount user = new UserAccount();
                    user.setUserId(Long.valueOf(rs.getLong("USER_ID")));
                    user.setLoginId(rs.getString("LOGIN_ID"));
                    user.setUserName(rs.getString("USER_NAME"));
                    user.setRole(rs.getString("ROLE"));
                    return user;
                }
            }
            return null;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    private UserAccount map(ResultSet rs) throws SQLException {
        UserAccount user = new UserAccount();
        user.setUserId(Long.valueOf(rs.getLong("USER_ID")));
        user.setLoginId(rs.getString("LOGIN_ID"));
        user.setUserName(rs.getString("USER_NAME"));
        user.setRole(rs.getString("ROLE"));
        return user;
    }

}
