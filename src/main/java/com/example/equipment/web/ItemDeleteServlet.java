package com.example.equipment.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.equipment.model.Item;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ItemDeleteServlet", urlPatterns = {"/items/delete"})
public class ItemDeleteServlet extends HttpServlet {

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
            request.setAttribute("item", item);
            request.getRequestDispatcher("/WEB-INF/jsp/itemDeleteConfirm.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/itemDeleteConfirm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ItemFormBinder.parseId(request.getParameter("itemId"));
        String versionText = request.getParameter("version");
        if (id == null || versionText == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            int version = Integer.parseInt(versionText.trim());
            itemService.delete(id, version);
            request.getSession().setAttribute("flashMessage", "備品を削除しました。");
            response.sendRedirect(request.getContextPath() + "/items");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ServiceException e) {
            request.getSession().setAttribute("flashMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/items/detail?id=" + id);
        }
    }
}
