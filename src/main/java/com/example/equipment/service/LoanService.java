package com.example.equipment.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.equipment.dao.ItemDao;
import com.example.equipment.dao.LoanDao;
import com.example.equipment.dao.UserDao;
import com.example.equipment.model.Item;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.model.Loan;
import com.example.equipment.model.LoanForm;
import com.example.equipment.model.LoanStatus;
import com.example.equipment.model.UserAccount;
import com.example.equipment.util.ConnectionFactory;
import com.example.equipment.validation.LoanFormValidator;

public class LoanService {

    private static final Logger LOGGER = Logger.getLogger(LoanService.class.getName());

    private final LoanDao loanDao = new LoanDao();
    private final ItemDao itemDao = new ItemDao();
    private final UserDao userDao = new UserDao();
    private final LoanFormValidator validator = new LoanFormValidator();

    public List<UserAccount> findUsers() throws ServiceException {
        try {
            return userDao.findAllActive();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list users", e);
            throw new ServiceException("利用者一覧の取得に失敗しました。");
        }
    }

    public List<Loan> findHistory() throws ServiceException {
        try {
            return loanDao.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list loans", e);
            throw new ServiceException("貸出履歴の取得に失敗しました。");
        }
    }

    public List<Loan> findActiveLoans() throws ServiceException {
        try {
            return loanDao.findActive();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list active loans", e);
            throw new ServiceException("貸出中一覧の取得に失敗しました。");
        }
    }

    public List<Loan> findByItemId(Long itemId) throws ServiceException {
        try {
            return loanDao.findByItemId(itemId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list loans by item", e);
            throw new ServiceException("貸出履歴の取得に失敗しました。");
        }
    }

    public List<Loan> findByUserId(Long userId) throws ServiceException {
        try {
            return loanDao.findByUserId(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list loans by user", e);
            throw new ServiceException("貸出履歴の取得に失敗しました。");
        }
    }

    public Loan findActiveByItemId(Long itemId) throws ServiceException {
        try {
            return loanDao.findActiveByItemId(itemId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find active loan", e);
            throw new ServiceException("貸出情報の取得に失敗しました。");
        }
    }

    public Loan findById(Long loanId) throws ServiceException {
        try {
            Loan loan = loanDao.findById(loanId);
            if (loan == null) {
                throw new ServiceException("指定された貸出情報が見つかりません。");
            }
            return loan;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find loan", e);
            throw new ServiceException("貸出情報の取得に失敗しました。");
        }
    }

    public Map<String, String> validateLoanForm(LoanForm form) {
        return validator.validateLoan(form);
    }

    public Map<String, String> validateReturnForm(LoanForm form) {
        return validator.validateReturn(form);
    }

    public String today() {
        return validator.todayString();
    }

    public long loan(LoanForm form) throws ServiceException {
        Map<String, String> errors = validateLoanForm(form);
        if (!errors.isEmpty()) {
            throw new ServiceException(errors.values().iterator().next());
        }

        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            Item item = itemDao.findById(connection, form.getItemId());
            if (item == null) {
                throw new ServiceException("指定された備品が見つかりません。");
            }
            if (item.getStatus() != ItemStatus.AVAILABLE) {
                throw new ServiceException("利用可能な備品のみ貸し出せます。");
            }
            if (loanDao.findActiveByItemId(connection, form.getItemId()) != null) {
                throw new ServiceException("この備品は既に貸出中です。");
            }
            if (userDao.findById(form.getUserId()) == null) {
                throw new ServiceException("指定された利用者が見つかりません。");
            }

            Loan loan = new Loan();
            loan.setItemId(form.getItemId());
            loan.setUserId(form.getUserId());
            loan.setLoanDate(validator.toSqlDate(form.getLoanDate()));
            loan.setPlannedReturnDate(validator.toSqlDate(form.getPlannedReturnDate()));
            loan.setLoanNote(form.getLoanNote());

            long loanId = loanDao.insert(connection, loan);
            int updated = itemDao.updateStatus(connection, form.getItemId(), ItemStatus.LOANED, item.getVersion());
            if (updated == 0) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }

            connection.commit();
            return loanId;
        } catch (ServiceException e) {
            rollbackQuietly(connection);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            LOGGER.log(Level.SEVERE, "Failed to loan item", e);
            throw new ServiceException("貸出処理に失敗しました。");
        } finally {
            closeQuietly(connection);
        }
    }

    public void returnLoan(LoanForm form) throws ServiceException {
        Map<String, String> errors = validateReturnForm(form);
        if (!errors.isEmpty()) {
            throw new ServiceException(errors.values().iterator().next());
        }

        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            Loan loan = loanDao.findById(connection, form.getLoanId());
            if (loan == null) {
                throw new ServiceException("指定された貸出情報が見つかりません。");
            }
            if (loan.getStatus() != LoanStatus.ACTIVE) {
                throw new ServiceException("返却済みの貸出は再度返却できません。");
            }
            if (loan.getVersion() != form.getLoanVersion()) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }

            Item item = itemDao.findById(connection, loan.getItemId());
            if (item == null) {
                throw new ServiceException("対象備品が見つかりません。");
            }
            if (item.getVersion() != form.getItemVersion()) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }

            int loanUpdated = loanDao.markReturned(
                    connection,
                    form.getLoanId(),
                    form.getLoanVersion(),
                    validator.toSqlDate(form.getActualReturnDate()),
                    form.getReturnNote());
            if (loanUpdated == 0) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }

            ItemStatus nextStatus = ItemStatus.fromCode(form.getReturnStatus());
            int itemUpdated = itemDao.updateStatus(connection, item.getItemId(), nextStatus, item.getVersion());
            if (itemUpdated == 0) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }

            connection.commit();
        } catch (ServiceException e) {
            rollbackQuietly(connection);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            LOGGER.log(Level.SEVERE, "Failed to return loan", e);
            throw new ServiceException("返却処理に失敗しました。");
        } finally {
            closeQuietly(connection);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // ignore
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException ignored) {
            // ignore
        }
    }
}
