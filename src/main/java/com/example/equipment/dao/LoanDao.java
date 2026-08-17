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
import java.util.Calendar;
import java.util.List;

import com.example.equipment.model.Loan;
import com.example.equipment.model.LoanStatus;
import com.example.equipment.util.ConnectionFactory;

public class LoanDao extends BaseDao {

    public long insert(Connection connection, Loan loan) throws SQLException {
        String sql = "INSERT INTO LOANS (ITEM_ID, USER_ID, LOAN_DATE, PLANNED_RETURN_DATE, "
                + "ACTUAL_RETURN_DATE, STATUS, LOAN_NOTE, RETURN_NOTE, VERSION, CREATED_AT, UPDATED_AT) "
                + "VALUES (?, ?, ?, ?, NULL, ?, ?, NULL, 1, ?, ?)";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PreparedStatement ps = null;
        ResultSet keys = null;
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, loan.getItemId().longValue());
            ps.setLong(2, loan.getUserId().longValue());
            ps.setDate(3, loan.getLoanDate());
            ps.setDate(4, loan.getPlannedReturnDate());
            ps.setString(5, LoanStatus.ACTIVE.name());
            if (loan.getLoanNote() == null || loan.getLoanNote().trim().isEmpty()) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, loan.getLoanNote());
            }
            ps.setTimestamp(7, now);
            ps.setTimestamp(8, now);
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getLong(1);
            }
            throw new SQLException("Failed to obtain LOAN_ID");
        } finally {
            closeQuietly(keys, ps, null);
        }
    }

    public int markReturned(Connection connection, Long loanId, int version, Date actualReturnDate,
            String returnNote) throws SQLException {
        String sql = "UPDATE LOANS SET ACTUAL_RETURN_DATE = ?, STATUS = ?, RETURN_NOTE = ?, "
                + "VERSION = VERSION + 1, UPDATED_AT = ? "
                + "WHERE LOAN_ID = ? AND VERSION = ? AND STATUS = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setDate(1, actualReturnDate);
            ps.setString(2, LoanStatus.RETURNED.name());
            if (returnNote == null || returnNote.trim().isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, returnNote);
            }
            ps.setTimestamp(4, now);
            ps.setLong(5, loanId.longValue());
            ps.setInt(6, version);
            ps.setString(7, LoanStatus.ACTIVE.name());
            return ps.executeUpdate();
        } finally {
            closeQuietly(null, ps, null);
        }
    }

    public Loan findById(Long loanId) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            return findById(connection, loanId);
        } finally {
            closeQuietly(null, null, connection);
        }
    }

    public Loan findById(Connection connection, Long loanId) throws SQLException {
        String sql = baseSelect() + " WHERE l.LOAN_ID = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setLong(1, loanId.longValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } finally {
            closeQuietly(rs, ps, null);
        }
    }

    public Loan findActiveByItemId(Long itemId) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            return findActiveByItemId(connection, itemId);
        } finally {
            closeQuietly(null, null, connection);
        }
    }

    public Loan findActiveByItemId(Connection connection, Long itemId) throws SQLException {
        String sql = baseSelect() + " WHERE l.ITEM_ID = ? AND l.STATUS = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setLong(1, itemId.longValue());
            ps.setString(2, LoanStatus.ACTIVE.name());
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;
        } finally {
            closeQuietly(rs, ps, null);
        }
    }

    public List<Loan> findByItemId(Long itemId) throws SQLException {
        String sql = baseSelect() + " WHERE l.ITEM_ID = ? ORDER BY l.LOAN_DATE DESC, l.LOAN_ID DESC";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Loan> list = new ArrayList<Loan>();
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setLong(1, itemId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    public List<Loan> findByUserId(Long userId) throws SQLException {
        String sql = baseSelect() + " WHERE l.USER_ID = ? ORDER BY l.LOAN_DATE DESC, l.LOAN_ID DESC";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Loan> list = new ArrayList<Loan>();
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setLong(1, userId.longValue());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    public List<Loan> findAll() throws SQLException {
        String sql = baseSelect() + " ORDER BY l.LOAN_DATE DESC, l.LOAN_ID DESC";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Loan> list = new ArrayList<Loan>();
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

    public List<Loan> findActive() throws SQLException {
        String sql = baseSelect() + " WHERE l.STATUS = ? ORDER BY l.PLANNED_RETURN_DATE, l.LOAN_ID";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Loan> list = new ArrayList<Loan>();
        try {
            connection = ConnectionFactory.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, LoanStatus.ACTIVE.name());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } finally {
            closeQuietly(rs, ps, connection);
        }
    }

    private String baseSelect() {
        return "SELECT l.LOAN_ID, l.ITEM_ID, i.MANAGEMENT_NO, i.ITEM_NAME, l.USER_ID, u.USER_NAME, "
                + "l.LOAN_DATE, l.PLANNED_RETURN_DATE, l.ACTUAL_RETURN_DATE, l.STATUS, "
                + "l.LOAN_NOTE, l.RETURN_NOTE, l.VERSION, l.CREATED_AT, l.UPDATED_AT "
                + "FROM LOANS l "
                + "INNER JOIN ITEMS i ON l.ITEM_ID = i.ITEM_ID "
                + "INNER JOIN USERS u ON l.USER_ID = u.USER_ID";
    }

    private Loan map(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(Long.valueOf(rs.getLong("LOAN_ID")));
        loan.setItemId(Long.valueOf(rs.getLong("ITEM_ID")));
        loan.setManagementNo(rs.getString("MANAGEMENT_NO"));
        loan.setItemName(rs.getString("ITEM_NAME"));
        loan.setUserId(Long.valueOf(rs.getLong("USER_ID")));
        loan.setUserName(rs.getString("USER_NAME"));
        loan.setLoanDate(rs.getDate("LOAN_DATE"));
        loan.setPlannedReturnDate(rs.getDate("PLANNED_RETURN_DATE"));
        loan.setActualReturnDate(rs.getDate("ACTUAL_RETURN_DATE"));
        loan.setStatus(LoanStatus.fromCode(rs.getString("STATUS")));
        loan.setLoanNote(rs.getString("LOAN_NOTE"));
        loan.setReturnNote(rs.getString("RETURN_NOTE"));
        loan.setVersion(rs.getInt("VERSION"));
        loan.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        loan.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        loan.setOverdue(isOverdue(loan));
        return loan;
    }

    private boolean isOverdue(Loan loan) {
        if (loan.getStatus() != LoanStatus.ACTIVE || loan.getPlannedReturnDate() == null) {
            return false;
        }
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return loan.getPlannedReturnDate().before(new Date(today.getTimeInMillis()));
    }

}
