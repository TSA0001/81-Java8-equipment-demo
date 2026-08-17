package com.example.equipment.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.equipment.model.ItemForm;

/**
 * リクエストから ItemForm を組み立てる。
 */
public final class ItemFormBinder {

    private ItemFormBinder() {
    }

    public static ItemForm bind(HttpServletRequest request) {
        ItemForm form = new ItemForm();
        form.setManagementNo(request.getParameter("managementNo"));
        form.setItemName(request.getParameter("itemName"));
        form.setStorageLocation(request.getParameter("storageLocation"));
        form.setPurchaseDate(request.getParameter("purchaseDate"));
        form.setStatus(request.getParameter("status"));
        form.setNote(request.getParameter("note"));

        String categoryId = request.getParameter("categoryId");
        if (categoryId != null && categoryId.trim().length() > 0) {
            try {
                form.setCategoryId(Long.valueOf(categoryId.trim()));
            } catch (NumberFormatException e) {
                form.setCategoryId(null);
            }
        }

        String itemId = request.getParameter("itemId");
        if (itemId != null && itemId.trim().length() > 0) {
            try {
                form.setItemId(Long.valueOf(itemId.trim()));
            } catch (NumberFormatException e) {
                form.setItemId(null);
            }
        }

        String version = request.getParameter("version");
        if (version != null && version.trim().length() > 0) {
            try {
                form.setVersion(Integer.parseInt(version.trim()));
            } catch (NumberFormatException e) {
                form.setVersion(0);
            }
        }
        return form;
    }

    public static Long parseId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void storeErrors(HttpServletRequest request, Map<String, String> errors) {
        request.setAttribute("errors", errors);
        request.setAttribute("errorMessages", errors.values());
    }
}
