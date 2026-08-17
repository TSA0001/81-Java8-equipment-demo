package com.example.equipment.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.equipment.model.Item;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.LoanService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ItemDetailServlet", urlPatterns = {"/items/detail"})
public class ItemDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ItemService itemService = new ItemService();
    private final LoanService loanService = new LoanService();

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
            request.setAttribute("activeLoan", loanService.findActiveByItemId(id));
            request.setAttribute("loanHistory", loanService.findByItemId(id));
            String flash = (String) request.getSession().getAttribute("flashMessage");
            if (flash != null) {
                request.setAttribute("flashMessage", flash);
                request.getSession().removeAttribute("flashMessage");
            }
            request.getRequestDispatcher("/WEB-INF/jsp/itemDetail.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/itemDetail.jsp").forward(request, response);
        }
    }
}
