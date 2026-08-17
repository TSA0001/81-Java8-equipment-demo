package com.example.equipment.model;

/**
 * 備品状態。
 */
public enum ItemStatus {
    AVAILABLE("利用可能"),
    LOANED("貸出中"),
    REPAIRING("修理中"),
    DISPOSED("廃棄");

    private final String label;

    ItemStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ItemStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ItemStatus status : values()) {
            if (status.name().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + code);
    }
}
