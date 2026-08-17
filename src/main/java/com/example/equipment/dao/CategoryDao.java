package com.example.equipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.equipment.model.Category;
import com.example.equipment.util.ConnectionFactory;

public class CategoryDao extends BaseDao {

    public List<Category> findAllActive() throws SQLException {
        String sql = "SELECT CATEGORY_ID, CATEGORY_NAME, DISPLAY_ORDER, DELETED, CREATED_AT, UPDATED_AT "
                + "FROM CATEGORIES WHERE DELETED = FALSE ORDER BY DISPLAY_ORDER, CATEGORY_ID";
        List<Category> list = new ArrayList<Category>();
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

    public Category findById(Long categoryId) throws SQLException {
        String sql = "SELECT CATEGORY_ID, CATEGORY_NAME, DISPLAY_ORDER, DELETED, CREATED_AT, UPDATED_AT "
                + "FROM CATEGORIES WHERE CATEGORY_ID = ? AND DELETED = FALSE";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setLong(1, categoryId.longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    private Category map(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(Long.valueOf(rs.getLong("CATEGORY_ID")));
        category.setCategoryName(rs.getString("CATEGORY_NAME"));
        category.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
        category.setDeleted(rs.getBoolean("DELETED"));
        category.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        category.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        return category;
    }

}
