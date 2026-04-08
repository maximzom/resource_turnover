package com.agriculture.resource_turnover.config;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderSpecifications {
    public static Specification<Order> creationDateAfterOrEqual(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("creationDate"), startDate);
    }

    public static Specification<Order> creationDateBeforeOrEqual(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("creationDate"), endDate);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}