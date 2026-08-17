package com.example.equipment.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.example.equipment.model.ItemForm;
import com.example.equipment.model.ItemStatus;

public class ItemFormValidatorTest {

    private final ItemFormValidator validator = new ItemFormValidator();

    @Test
    public void acceptsValidForm() {
        ItemForm form = validForm();
        Map<String, String> errors = validator.validate(form);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void rejectsInvalidManagementNo() {
        ItemForm form = validForm();
        form.setManagementNo("EQ-1");
        Map<String, String> errors = validator.validate(form);
        assertTrue(errors.containsKey("managementNo"));
    }

    @Test
    public void rejectsEmptyItemName() {
        ItemForm form = validForm();
        form.setItemName(" ");
        Map<String, String> errors = validator.validate(form);
        assertTrue(errors.containsKey("itemName"));
    }

    @Test
    public void rejectsFuturePurchaseDate() {
        ItemForm form = validForm();
        form.setPurchaseDate("2999-01-01");
        Map<String, String> errors = validator.validate(form);
        assertTrue(errors.containsKey("purchaseDate"));
    }

    @Test
    public void managementNoPattern() {
        assertTrue(validator.isValidManagementNoFormat("EQ-000001"));
        assertFalse(validator.isValidManagementNoFormat("EQ000001"));
    }

    private ItemForm validForm() {
        ItemForm form = new ItemForm();
        form.setManagementNo("EQ-000010");
        form.setItemName("テスト備品");
        form.setCategoryId(Long.valueOf(1L));
        form.setStorageLocation("倉庫");
        form.setStatus(ItemStatus.AVAILABLE.name());
        form.setPurchaseDate("2024-01-01");
        return form;
    }
}
