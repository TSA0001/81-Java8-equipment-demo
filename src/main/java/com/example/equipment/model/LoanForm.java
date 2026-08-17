package com.example.equipment.model;

import java.io.Serializable;

public class LoanForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long userId;
    private String loanDate;
    private String plannedReturnDate;
    private String loanNote;
    private Long loanId;
    private int loanVersion;
    private int itemVersion;
    private String actualReturnDate;
    private String returnStatus;
    private String returnNote;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getPlannedReturnDate() {
        return plannedReturnDate;
    }

    public void setPlannedReturnDate(String plannedReturnDate) {
        this.plannedReturnDate = plannedReturnDate;
    }

    public String getLoanNote() {
        return loanNote;
    }

    public void setLoanNote(String loanNote) {
        this.loanNote = loanNote;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public int getLoanVersion() {
        return loanVersion;
    }

    public void setLoanVersion(int loanVersion) {
        this.loanVersion = loanVersion;
    }

    public int getItemVersion() {
        return itemVersion;
    }

    public void setItemVersion(int itemVersion) {
        this.itemVersion = itemVersion;
    }

    public String getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnNote() {
        return returnNote;
    }

    public void setReturnNote(String returnNote) {
        this.returnNote = returnNote;
    }
}
