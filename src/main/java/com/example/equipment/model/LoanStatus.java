package com.example.equipment.model;

public enum LoanStatus {
    ACTIVE("貸出中"),
    RETURNED("返却済");

    private final String label;

    LoanStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static LoanStatus fromCode(String code) {
        for (LoanStatus status : values()) {
            if (status.name().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown loan status: " + code);
    }
}
