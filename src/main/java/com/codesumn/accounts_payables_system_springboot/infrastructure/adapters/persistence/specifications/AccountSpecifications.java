package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.persistence.specifications;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountSpecifications {

    public static Specification<AccountModel> searchWithTerm(String searchTerm) {
        return (root, query, cb) -> {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("description")), likePattern);
        };
    }

    public static Specification<AccountModel> hasStatus(String status) {
        return (root, query, cb) -> cb
                .equal(cb.lower(root.get("status")), status.toLowerCase());
    }

    public static Specification<AccountModel> hasMinimumAmount(BigDecimal amount) {
        return (root, query, cb) -> cb
                .greaterThanOrEqualTo(root.get("amount"), amount);
    }

    public static Specification<AccountModel> dueDateBetween(java.util.Date startDate, java.util.Date endDate) {
        return (root, query, cb) -> cb
                .between(root.get("dueDate"), startDate, endDate);
    }

    public static Specification<AccountModel> filterPaidBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> cb.and(
                cb.equal(cb.lower(root.get("status")), "paid"),
                cb.between(root.get("dueDate"), startDate, endDate)
        );
    }
}
