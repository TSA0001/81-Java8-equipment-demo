package com.example.equipment.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.equipment.model.Item;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.model.LoanForm;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.LoanService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "LoanServlet", urlPatterns = {"/loans/new"})
public class LoanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LoanService loanService = new LoanService();
    private final ItemService itemService = new ItemService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long itemId = ItemFormBinder.parseId(request.getParameter("itemId"));
        if (itemId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            Item item = itemService.findById(itemId);
            if (item.getStatus() != ItemStatus.AVAILABLE) {
                request.getSession().setAttribute("flashMessage", "利用可能な備品のみ貸し出せます。");
                response.sendRedirect(request.getContextPath() + "/items/detail?id=" + itemId);
                return;
            }
            LoanForm form = new LoanForm();
            form.setItemId(itemId);
            form.setLoanDate(loanService.today());
            form.setPlannedReturnDate(loanService.today());
            request.setAttribute("item", item);
            request.setAttribute("form", form);
            request.setAttribute("users", loanService.findUsers());
            request.getRequestDispatcher("/WEB-INF/jsp/loanForm.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/loanForm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LoanForm form = bindLoanForm(request);
        try {
            Item item = itemService.findById(form.getItemId());
            Map<String, String> errors = loanService.validateLoanForm(form);
            if (!errors.isEmpty()) {
                request.setAttribute("item", item);
                request.setAttribute("form", form);
                request.setAttribute("users", loanService.findUsers());
                ItemFormBinder.storeErrors(request, errors);
                request.getRequestDispatcher("/WEB-INF/jsp/loanForm.jsp").forward(request, response);
                return;
            }
            loanService.loan(form);
            request.getSession().setAttribute("flashMessage", "備品を貸し出しました。");
            response.sendRedirect(request.getContextPath() + "/items/detail?id=" + form.getItemId());
        } catch (ServiceException e) {
            try {
                request.setAttribute("item", itemService.findById(form.getItemId()));
                request.setAttribute("users", loanService.findUsers());
            } catch (ServiceException ignored) {
                // ignore
            }
            request.setAttribute("form", form);
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/loanForm.jsp").forward(request, response);
        }
    }

    private LoanForm bindLoanForm(HttpServletRequest request) {
        LoanForm form = new LoanForm();
        form.setItemId(ItemFormBinder.parseId(request.getParameter("itemId")));
        form.setUserId(ItemFormBinder.parseId(request.getParameter("userId")));
        form.setLoanDate(request.getParameter("loanDate"));
        form.setPlannedReturnDate(request.getParameter("plannedReturnDate"));
        form.setLoanNote(request.getParameter("loanNote"));
        return form;
    }
}
