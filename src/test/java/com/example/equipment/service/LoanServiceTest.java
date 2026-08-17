package com.example.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.example.equipment.model.Item;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.model.Loan;
import com.example.equipment.model.LoanForm;
import com.example.equipment.model.LoanStatus;
import com.example.equipment.util.DatabaseInitializer;

public class LoanServiceTest {

    private final LoanService loanService = new LoanService();
    private final ItemService itemService = new ItemService();

    @Before
    public void setUp() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "equipment-loan-" + UUID.randomUUID());
        dir.mkdirs();
        System.setProperty("equipment.db.path", new File(dir, "test").getAbsolutePath());
        DatabaseInitializer.initializeIfNeeded();
    }

    @Test
    public void canLoanAvailableItem() throws Exception {
        Item item = itemService.findById(Long.valueOf(1L));
        assertEquals(ItemStatus.AVAILABLE, item.getStatus());

        LoanForm form = new LoanForm();
        form.setItemId(item.getItemId());
        form.setUserId(Long.valueOf(2L));
        form.setLoanDate("2024-01-10");
        form.setPlannedReturnDate("2024-01-20");
        long loanId = loanService.loan(form);
        assertNotNull(Long.valueOf(loanId));

        Item after = itemService.findById(item.getItemId());
        assertEquals(ItemStatus.LOANED, after.getStatus());
        Loan active = loanService.findActiveByItemId(item.getItemId());
        assertEquals(LoanStatus.ACTIVE, active.getStatus());
    }

    @Test
    public void cannotLoanAlreadyLoanedItem() throws Exception {
        LoanForm form = new LoanForm();
        form.setItemId(Long.valueOf(1L));
        form.setUserId(Long.valueOf(2L));
        form.setLoanDate("2024-01-10");
        form.setPlannedReturnDate("2024-01-20");
        loanService.loan(form);

        try {
            loanService.loan(form);
            fail("expected ServiceException");
        } catch (ServiceException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void returnUpdatesItemStatus() throws Exception {
        LoanForm form = new LoanForm();
        form.setItemId(Long.valueOf(2L));
        form.setUserId(Long.valueOf(2L));
        form.setLoanDate("2024-02-01");
        form.setPlannedReturnDate("2024-02-10");
        long loanId = loanService.loan(form);

        Item loaned = itemService.findById(Long.valueOf(2L));
        Loan loan = loanService.findById(Long.valueOf(loanId));

        LoanForm ret = new LoanForm();
        ret.setLoanId(loan.getLoanId());
        ret.setLoanVersion(loan.getVersion());
        ret.setItemVersion(loaned.getVersion());
        ret.setActualReturnDate("2024-02-09");
        ret.setReturnStatus(ItemStatus.AVAILABLE.name());
        loanService.returnLoan(ret);

        Item after = itemService.findById(Long.valueOf(2L));
        assertEquals(ItemStatus.AVAILABLE, after.getStatus());
        assertNull(loanService.findActiveByItemId(Long.valueOf(2L)));
    }

    @Test
    public void rejectsPlannedReturnBeforeLoanDate() {
        LoanForm form = new LoanForm();
        form.setItemId(Long.valueOf(1L));
        form.setUserId(Long.valueOf(2L));
        form.setLoanDate("2024-03-10");
        form.setPlannedReturnDate("2024-03-01");
        try {
            loanService.loan(form);
            fail("expected ServiceException");
        } catch (ServiceException e) {
            assertNotNull(e.getMessage());
        }
    }
}
