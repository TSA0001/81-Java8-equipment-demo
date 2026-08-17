package com.example.equipment.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.equipment.model.ItemForm;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ItemCreateServlet", urlPatterns = {
        "/items/new", "/items/confirm", "/items/create", "/items/cancel"
})
public class ItemCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String SESSION_FORM = "itemCreateForm";

    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if ("/items/cancel".equals(path)) {
                clearForm(request);
                response.sendRedirect(request.getContextPath() + "/items");
                return;
            }
            prepareCommon(request);
            ItemForm form = (ItemForm) request.getSession().getAttribute(SESSION_FORM);
            if ("/items/confirm".equals(path)) {
                if (form == null) {
                    response.sendRedirect(request.getContextPath() + "/items/new");
                    return;
                }
                request.setAttribute("form", form);
                request.getRequestDispatcher("/WEB-INF/jsp/itemConfirm.jsp").forward(request, response);
                return;
            }
            if (form == null) {
                form = new ItemForm();
                form.setStatus(ItemStatus.AVAILABLE.name());
            }
            request.setAttribute("form", form);
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if ("/items/confirm".equals(path)) {
                ItemForm form = ItemFormBinder.bind(request);
                Map<String, String> errors = itemService.validateForm(form);
                prepareCommon(request);
                request.setAttribute("form", form);
                if (!errors.isEmpty()) {
                    ItemFormBinder.storeErrors(request, errors);
                    request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
                    return;
                }
                try {
                    itemService.assertBusinessRules(form);
                } catch (ServiceException e) {
                    request.setAttribute("errorMessage", e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
                    return;
                }
                request.getSession().setAttribute(SESSION_FORM, form);
                response.sendRedirect(request.getContextPath() + "/items/confirm");
                return;
            }

            if ("/items/create".equals(path)) {
                HttpSession session = request.getSession(false);
                ItemForm form = session == null ? null : (ItemForm) session.getAttribute(SESSION_FORM);
                if (form == null) {
                    response.sendRedirect(request.getContextPath() + "/items/new");
                    return;
                }
                long id = itemService.create(form);
                clearForm(request);
                session.setAttribute("flashMessage", "備品を登録しました。管理番号: " + form.getManagementNo());
                response.sendRedirect(request.getContextPath() + "/items/complete?id=" + id);
                return;
            }

            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } catch (ServiceException e) {
            try {
                prepareCommon(request);
            } catch (ServiceException ignored) {
                // ignore
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("form", ItemFormBinder.bind(request));
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
        }
    }

    private void prepareCommon(HttpServletRequest request) throws ServiceException {
        request.setAttribute("categories", itemService.findCategories());
        request.setAttribute("statuses", ItemStatus.values());
        request.setAttribute("mode", "create");
    }

    private void clearForm(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_FORM);
        }
    }
}
