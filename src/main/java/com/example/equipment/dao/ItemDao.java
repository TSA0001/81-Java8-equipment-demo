package com.example.equipment.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.example.equipment.model.Item;
import com.example.equipment.model.ItemSearchCriteria;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.util.ConnectionFactory;

public class ItemDao extends BaseDao {

    public List<Item> findAllActive() throws SQLException {
        return search(new ItemSearchCriteria());
    }

    public List<Item> search(ItemSearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.ITEM_ID, i.MANAGEMENT_NO, i.ITEM_NAME, i.CATEGORY_ID, c.CATEGORY_NAME, ");
        sql.append("i.PURCHASE_DATE, i.STORAGE_LOCATION, i.STATUS, i.NOTE, i.VERSION, i.DELETED, ");
        sql.append("i.CREATED_AT, i.UPDATED_AT ");
        sql.append("FROM ITEMS i INNER JOIN CATEGORIES c ON i.CATEGORY_ID = c.CATEGORY_ID ");
        sql.append("WHERE i.DELETED = FALSE ");

        List<Object> params = new ArrayList<Object>();
        if (criteria != null) {
            if (notEmpty(criteria.getManagementNo())) {
                sql.append("AND i.MANAGEMENT_NO LIKE ? ");
                params.add("%" + criteria.getManagementNo().trim() + "%");
            }
            if (notEmpty(criteria.getItemName())) {
                sql.append("AND i.ITEM_NAME LIKE ? ");
                params.add("%" + criteria.getItemName().trim() + "%");
            }
            if (criteria.getCategoryId() != null) {
                sql.append("AND i.CATEGORY_ID = ? ");
                params.add(criteria.getCategoryId());
            }
            if (notEmpty(criteria.getStorageLocation())) {
                sql.append("AND i.STORAGE_LOCATION LIKE ? ");
                params.add("%" + criteria.getStorageLocation().trim() + "%");
            }
            if (notEmpty(criteria.getStatus())) {
                sql.append("AND i.STATUS = ? ");
                params.add(criteria.getStatus().trim());
            }
        }
        sql.append("ORDER BY i.MANAGEMENT_NO");

        List<Item> list = new ArrayList<Item>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i);
                if (value instanceof Long) {
                    ps.setLong(i + 1, ((Long) value).longValue());
                } else {
                    ps.setString(i + 1, String.valueOf(value));
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    private boolean notEmpty(String value) {
        return value != null && value.trim().length() > 0;
    }

    public Item findById(Long itemId) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            return findById(connection, itemId);
        } finally {
            closeQuietly(null, null, connection);
        }
    }

    public Item findById(Connection connection, Long itemId) throws SQLException {
        String sql = "SELECT i.ITEM_ID, i.MANAGEMENT_NO, i.ITEM_NAME, i.CATEGORY_ID, c.CATEGORY_NAME, "
                + "i.PURCHASE_DATE, i.STORAGE_LOCATION, i.STATUS, i.NOTE, i.VERSION, i.DELETED, "
                + "i.CREATED_AT, i.UPDATED_AT "
                + "FROM ITEMS i INNER JOIN CATEGORIES c ON i.CATEGORY_ID = c.CATEGORY_ID "
                + "WHERE i.ITEM_ID = ? AND i.DELETED = FALSE";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setLong(1, itemId.longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } finally {
            closeQuietly(rs, ps, null);
        }
    }

    public int updateStatus(Connection connection, Long itemId, ItemStatus status, int version)
            throws SQLException {
        String sql = "UPDATE ITEMS SET STATUS = ?, VERSION = VERSION + 1, UPDATED_AT = ? "
                + "WHERE ITEM_ID = ? AND VERSION = ? AND DELETED = FALSE";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setTimestamp(2, now);
            ps.setLong(3, itemId.longValue());
            ps.setInt(4, version);
            return ps.executeUpdate();
        } finally {
            closeQuietly(null, ps, null);
        }
    }

    public boolean existsByManagementNo(String managementNo, Long excludeItemId) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(1) FROM ITEMS WHERE MANAGEMENT_NO = ? AND DELETED = FALSE");
        if (excludeItemId != null) {
            sql.append(" AND ITEM_ID <> ?");
        }
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql.toString());
            ps.setString(1, managementNo);
            if (excludeItemId != null) {
                ps.setLong(2, excludeItemId.longValue());
            }
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    public long insert(Item item) throws SQLException {
        String sql = "INSERT INTO ITEMS (MANAGEMENT_NO, ITEM_NAME, CATEGORY_ID, PURCHASE_DATE, "
                + "STORAGE_LOCATION, STATUS, NOTE, VERSION, DELETED, CREATED_AT, UPDATED_AT) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 1, FALSE, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet keys = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindWritable(ps, item);
            ps.setTimestamp(8, now);
            ps.setTimestamp(9, now);
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getLong(1);
            }
            throw new SQLException("Failed to obtain generated ITEM_ID");
        } finally {
            closeQuietly(keys, ps, connection);
        }
    }

    public int update(Item item) throws SQLException {
        String sql = "UPDATE ITEMS SET MANAGEMENT_NO = ?, ITEM_NAME = ?, CATEGORY_ID = ?, "
                + "PURCHASE_DATE = ?, STORAGE_LOCATION = ?, STATUS = ?, NOTE = ?, "
                + "VERSION = VERSION + 1, UPDATED_AT = ? "
                + "WHERE ITEM_ID = ? AND VERSION = ? AND DELETED = FALSE";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            bindWritable(ps, item);
            ps.setTimestamp(8, now);
            ps.setLong(9, item.getItemId().longValue());
            ps.setInt(10, item.getVersion());
            return ps.executeUpdate();
        } finally {
            closeQuietly(null, ps, connection);
        }
    }

    public int logicalDelete(Long itemId, int version) throws SQLException {
        String sql = "UPDATE ITEMS SET DELETED = TRUE, VERSION = VERSION + 1, UPDATED_AT = ? "
                + "WHERE ITEM_ID = ? AND VERSION = ? AND DELETED = FALSE";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, now);
            ps.setLong(2, itemId.longValue());
            ps.setInt(3, version);
            return ps.executeUpdate();
        } finally {
            closeQuietly(null, ps, connection);
        }
    }

    private void bindWritable(PreparedStatement ps, Item item) throws SQLException {
        ps.setString(1, item.getManagementNo());
        ps.setString(2, item.getItemName());
        ps.setLong(3, item.getCategoryId().longValue());
        if (item.getPurchaseDate() == null) {
            ps.setNull(4, Types.DATE);
        } else {
            ps.setDate(4, item.getPurchaseDate());
        }
        ps.setString(5, item.getStorageLocation());
        ps.setString(6, item.getStatus().name());
        if (item.getNote() == null || item.getNote().trim().isEmpty()) {
            ps.setNull(7, Types.VARCHAR);
        } else {
            ps.setString(7, item.getNote());
        }
    }

    private Item map(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(Long.valueOf(rs.getLong("ITEM_ID")));
        item.setManagementNo(rs.getString("MANAGEMENT_NO"));
        item.setItemName(rs.getString("ITEM_NAME"));
        item.setCategoryId(Long.valueOf(rs.getLong("CATEGORY_ID")));
        item.setCategoryName(rs.getString("CATEGORY_NAME"));
        Date purchaseDate = rs.getDate("PURCHASE_DATE");
        item.setPurchaseDate(purchaseDate);
        item.setStorageLocation(rs.getString("STORAGE_LOCATION"));
        item.setStatus(ItemStatus.fromCode(rs.getString("STATUS")));
        item.setNote(rs.getString("NOTE"));
        item.setVersion(rs.getInt("VERSION"));
        item.setDeleted(rs.getBoolean("DELETED"));
        item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        item.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        return item;
    }

}
