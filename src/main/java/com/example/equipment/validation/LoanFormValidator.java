package com.example.equipment.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.equipment.model.ItemStatus;
import com.example.equipment.model.LoanForm;

public class LoanFormValidator {

    public Map<String, String> validateLoan(LoanForm form) {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (form.getItemId() == null) {
            errors.put("itemId", "備品が指定されていません。");
        }
        if (form.getUserId() == null) {
            errors.put("userId", "利用者は必須です。");
        }

        String loanDate = trim(form.getLoanDate());
        form.setLoanDate(loanDate);
        Date loan = parseDate(loanDate);
        if (isEmpty(loanDate)) {
            errors.put("loanDate", "貸出日は必須です。");
        } else if (loan == null) {
            errors.put("loanDate", "貸出日は yyyy-MM-dd 形式で入力してください。");
        }

        String planned = trim(form.getPlannedReturnDate());
        form.setPlannedReturnDate(planned);
        Date plannedDate = parseDate(planned);
        if (isEmpty(planned)) {
            errors.put("plannedReturnDate", "返却予定日は必須です。");
        } else if (plannedDate == null) {
            errors.put("plannedReturnDate", "返却予定日は yyyy-MM-dd 形式で入力してください。");
        } else if (loan != null && plannedDate.before(loan)) {
            errors.put("plannedReturnDate", "返却予定日は貸出日以降を指定してください。");
        }

        String note = trim(form.getLoanNote());
        form.setLoanNote(note);
        if (note != null && note.length() > 1000) {
            errors.put("loanNote", "備考は1000文字以内で入力してください。");
        }
        return errors;
    }

    public Map<String, String> validateReturn(LoanForm form) {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (form.getLoanId() == null) {
            errors.put("loanId", "貸出情報が指定されていません。");
        }

        String actual = trim(form.getActualReturnDate());
        form.setActualReturnDate(actual);
        if (isEmpty(actual)) {
            errors.put("actualReturnDate", "返却日は必須です。");
        } else if (parseDate(actual) == null) {
            errors.put("actualReturnDate", "返却日は yyyy-MM-dd 形式で入力してください。");
        }

        String status = trim(form.getReturnStatus());
        form.setReturnStatus(status);
        if (isEmpty(status)) {
            errors.put("returnStatus", "返却後の状態は必須です。");
        } else {
            try {
                ItemStatus itemStatus = ItemStatus.fromCode(status);
                if (itemStatus != ItemStatus.AVAILABLE && itemStatus != ItemStatus.REPAIRING) {
                    errors.put("returnStatus", "返却後の状態は利用可能または修理中を選択してください。");
                }
            } catch (IllegalArgumentException e) {
                errors.put("returnStatus", "返却後の状態が不正です。");
            }
        }

        String note = trim(form.getReturnNote());
        form.setReturnNote(note);
        if (note != null && note.length() > 1000) {
            errors.put("returnNote", "備考は1000文字以内で入力してください。");
        }
        return errors;
    }

    public java.sql.Date toSqlDate(String value) {
        Date parsed = parseDate(value);
        return parsed == null ? null : new java.sql.Date(parsed.getTime());
    }

    public String todayString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(Calendar.getInstance().getTime());
    }

    private Date parseDate(String value) {
        if (isEmpty(value)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
