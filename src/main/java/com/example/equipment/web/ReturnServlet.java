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
import com.example.equipment.model.Loan;
import com.example.equipment.model.LoanForm;
import com.example.equipment.service.ItemService;
import com.example.equipment.service.LoanService;
import com.example.equipment.service.ServiceException;

@WebServlet(name = "ReturnServlet", urlPatterns = {"/loans/return"})
public class ReturnServlet extends HttpServlet {

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
            Loan loan = loanService.findActiveByItemId(itemId);
            if (loan == null) {
                request.getSession().setAttribute("flashMessage", "返却対象の貸出がありません。");
                response.sendRedirect(request.getContextPath() + "/items/detail?id=" + itemId);
                return;
            }
            LoanForm form = new LoanForm();
            form.setLoanId(loan.getLoanId());
            form.setItemId(itemId);
            form.setLoanVersion(loan.getVersion());
            form.setItemVersion(item.getVersion());
            form.setActualReturnDate(loanService.today());
            form.setReturnStatus(ItemStatus.AVAILABLE.name());
            request.setAttribute("item", item);
            request.setAttribute("loan", loan);
            request.setAttribute("form", form);
            request.getRequestDispatcher("/WEB-INF/jsp/returnForm.jsp").forward(request, response);
        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/returnForm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LoanForm form = bindReturnForm(request);
        try {
            Map<String, String> errors = loanService.validateReturnForm(form);
            Loan loan = loanService.findById(form.getLoanId());
            Item item = itemService.findById(loan.getItemId());
            if (!errors.isEmpty()) {
                request.setAttribute("item", item);
                request.setAttribute("loan", loan);
                request.setAttribute("form", form);
                ItemFormBinder.storeErrors(request, errors);
                request.getRequestDispatcher("/WEB-INF/jsp/returnForm.jsp").forward(request, response);
                return;
            }
            loanService.returnLoan(form);
            request.getSession().setAttribute("flashMessage", "備品を返却しました。");
            response.sendRedirect(request.getContextPath() + "/items/detail?id=" + loan.getItemId());
        } catch (ServiceException e) {
            try {
                if (form.getLoanId() != null) {
                    Loan loan = loanService.findById(form.getLoanId());
                    request.setAttribute("loan", loan);
                    request.setAttribute("item", itemService.findById(loan.getItemId()));
                }
            } catch (ServiceException ignored) {
                // ignore
            }
            request.setAttribute("form", form);
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/returnForm.jsp").forward(request, response);
        }
    }

    private LoanForm bindReturnForm(HttpServletRequest request) {
        LoanForm form = new LoanForm();
        form.setLoanId(ItemFormBinder.parseId(request.getParameter("loanId")));
        form.setItemId(ItemFormBinder.parseId(request.getParameter("itemId")));
        form.setActualReturnDate(request.getParameter("actualReturnDate"));
        form.setReturnStatus(request.getParameter("returnStatus"));
        form.setReturnNote(request.getParameter("returnNote"));
        String loanVersion = request.getParameter("loanVersion");
        String itemVersion = request.getParameter("itemVersion");
        if (loanVersion != null && loanVersion.trim().length() > 0) {
            try {
                form.setLoanVersion(Integer.parseInt(loanVersion.trim()));
            } catch (NumberFormatException ignored) {
                form.setLoanVersion(0);
            }
        }
        if (itemVersion != null && itemVersion.trim().length() > 0) {
            try {
                form.setItemVersion(Integer.parseInt(itemVersion.trim()));
            } catch (NumberFormatException ignored) {
                form.setItemVersion(0);
            }
        }
        return form;
    }
}
