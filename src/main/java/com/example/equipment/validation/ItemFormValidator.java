package com.example.equipment.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.example.equipment.model.ItemForm;
import com.example.equipment.model.ItemStatus;

/**
 * 備品フォームの入力検証。
 */
public class ItemFormValidator {

    private static final Pattern MANAGEMENT_NO_PATTERN = Pattern.compile("^EQ-\\d{6}$");

    public Map<String, String> validate(ItemForm form) {
        Map<String, String> errors = new LinkedHashMap<String, String>();

        String managementNo = trim(form.getManagementNo());
        form.setManagementNo(managementNo);
        if (isEmpty(managementNo)) {
            errors.put("managementNo", "管理番号は必須です。");
        } else if (!MANAGEMENT_NO_PATTERN.matcher(managementNo).matches()) {
            errors.put("managementNo", "管理番号は EQ-000001 形式で入力してください。");
        } else if (managementNo.length() > 20) {
            errors.put("managementNo", "管理番号は20文字以内で入力してください。");
        }

        String itemName = trim(form.getItemName());
        form.setItemName(itemName);
        if (isEmpty(itemName)) {
            errors.put("itemName", "備品名は必須です。");
        } else if (itemName.length() > 100) {
            errors.put("itemName", "備品名は100文字以内で入力してください。");
        }

        if (form.getCategoryId() == null) {
            errors.put("categoryId", "カテゴリは必須です。");
        }

        String storage = trim(form.getStorageLocation());
        form.setStorageLocation(storage);
        if (isEmpty(storage)) {
            errors.put("storageLocation", "保管場所は必須です。");
        } else if (storage.length() > 100) {
            errors.put("storageLocation", "保管場所は100文字以内で入力してください。");
        }

        String status = trim(form.getStatus());
        form.setStatus(status);
        if (isEmpty(status)) {
            errors.put("status", "状態は必須です。");
        } else {
            try {
                ItemStatus.fromCode(status);
            } catch (IllegalArgumentException e) {
                errors.put("status", "状態の値が不正です。");
            }
        }

        String purchaseDate = trim(form.getPurchaseDate());
        form.setPurchaseDate(purchaseDate);
        if (!isEmpty(purchaseDate)) {
            Date parsed = parseDate(purchaseDate);
            if (parsed == null) {
                errors.put("purchaseDate", "購入日は yyyy-MM-dd 形式で入力してください。");
            } else if (isFuture(parsed)) {
                errors.put("purchaseDate", "購入日に未来日は指定できません。");
            }
        }

        String note = trim(form.getNote());
        form.setNote(note);
        if (note != null && note.length() > 1000) {
            errors.put("note", "備考は1000文字以内で入力してください。");
        }

        return errors;
    }

    public boolean isValidManagementNoFormat(String managementNo) {
        return managementNo != null && MANAGEMENT_NO_PATTERN.matcher(managementNo).matches();
    }

    public java.sql.Date toSqlDate(String value) {
        if (isEmpty(value)) {
            return null;
        }
        Date parsed = parseDate(value);
        if (parsed == null) {
            return null;
        }
        return new java.sql.Date(parsed.getTime());
    }

    private Date parseDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean isFuture(Date date) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return date.after(today.getTime());
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public List<String> toMessageList(Map<String, String> errors) {
        return new ArrayList<String>(errors.values());
    }
}
