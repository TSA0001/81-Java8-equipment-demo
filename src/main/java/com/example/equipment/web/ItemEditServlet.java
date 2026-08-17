package com.example.equipment.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.equipment.model.Item;
import com.example.equipment.model.ItemForm;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ItemEditServlet", urlPatterns = {"/items/edit"})
public class ItemEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ItemFormBinder.parseId(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            Item item = itemService.findById(id);
            prepareCommon(request);
            request.setAttribute("form", itemService.toForm(item));
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ItemForm form = ItemFormBinder.bind(request);
        try {
            Map<String, String> errors = itemService.validateForm(form);
            if (!errors.isEmpty()) {
                prepareCommon(request);
                request.setAttribute("form", form);
                ItemFormBinder.storeErrors(request, errors);
                request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
                return;
            }
            itemService.update(form);
            request.getSession().setAttribute("flashMessage", "備品を更新しました。");
            response.sendRedirect(request.getContextPath() + "/items/detail?id=" + form.getItemId());
        } catch (ServiceException e) {
            try {
                prepareCommon(request);
            } catch (ServiceException ignored) {
                // ignore
            }
            request.setAttribute("form", form);
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/itemForm.jsp").forward(request, response);
        }
    }

    private void prepareCommon(HttpServletRequest request) throws ServiceException {
        request.setAttribute("categories", itemService.findCategories());
        request.setAttribute("statuses", ItemStatus.values());
        request.setAttribute("mode", "edit");
    }
}
