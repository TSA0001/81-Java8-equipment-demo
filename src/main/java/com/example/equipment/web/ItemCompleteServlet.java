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

@WebServlet(name = "ItemCompleteServlet", urlPatterns = {"/items/complete"})
public class ItemCompleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ItemFormBinder.parseId(request.getParameter("id"));
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/items");
            return;
        }
        try {
            Item item = itemService.findById(id);
            request.setAttribute("item", item);
            request.getRequestDispatcher("/WEB-INF/jsp/itemComplete.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.getSession().setAttribute("flashMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/items");
        }
    }
}
