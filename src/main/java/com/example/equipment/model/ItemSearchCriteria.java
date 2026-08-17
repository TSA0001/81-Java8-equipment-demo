package com.example.equipment.model;

import java.io.Serializable;

/**
 * 備品検索条件。
 */
public class ItemSearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String managementNo;
    private String itemName;
    private Long categoryId;
    private String storageLocation;
    private String status;

    public String getManagementNo() {
        return managementNo;
    }

    public void setManagementNo(String managementNo) {
        this.managementNo = managementNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasAnyCondition() {
        return notEmpty(managementNo)
                || notEmpty(itemName)
                || categoryId != null
                || notEmpty(storageLocation)
                || notEmpty(status);
    }

    private boolean notEmpty(String value) {
        return value != null && value.trim().length() > 0;
    }
}
