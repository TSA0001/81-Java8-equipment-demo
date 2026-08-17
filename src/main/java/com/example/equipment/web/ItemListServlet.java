package com.example.equipment.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.equipment.model.ItemSearchCriteria;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ItemListServlet", urlPatterns = {"/items"})
public class ItemListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ItemSearchCriteria criteria = bindCriteria(request);
        try {
            request.setAttribute("criteria", criteria);
            request.setAttribute("categories", itemService.findCategories());
            request.setAttribute("statuses", ItemStatus.values());
            request.setAttribute("items", itemService.search(criteria));
            request.setAttribute("searched", Boolean.valueOf(criteria.hasAnyCondition()));

            String flash = (String) request.getSession().getAttribute("flashMessage");
            if (flash != null) {
                request.setAttribute("flashMessage", flash);
                request.getSession().removeAttribute("flashMessage");
            }
            request.getRequestDispatcher("/WEB-INF/jsp/itemList.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("criteria", criteria);
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.setAttribute("categories", itemService.findCategories());
                request.setAttribute("statuses", ItemStatus.values());
            } catch (ServiceException ignored) {
                // ignore secondary failure
            }
            request.getRequestDispatcher("/WEB-INF/jsp/itemList.jsp").forward(request, response);
        }
    }

    private ItemSearchCriteria bindCriteria(HttpServletRequest request) {
        ItemSearchCriteria criteria = new ItemSearchCriteria();
        criteria.setManagementNo(trim(request.getParameter("managementNo")));
        criteria.setItemName(trim(request.getParameter("itemName")));
        criteria.setStorageLocation(trim(request.getParameter("storageLocation")));
        criteria.setStatus(trim(request.getParameter("status")));

        String categoryId = trim(request.getParameter("categoryId"));
        if (categoryId != null) {
            try {
                criteria.setCategoryId(Long.valueOf(categoryId));
            } catch (NumberFormatException e) {
                criteria.setCategoryId(null);
            }
        }
        return criteria;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
