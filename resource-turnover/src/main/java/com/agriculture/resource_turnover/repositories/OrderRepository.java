package com.agriculture.resource_turnover.repositories;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    @Query("""
        SELECT DISTINCT o FROM Order o 
        LEFT JOIN FETCH o.comments 
        WHERE (:start IS NULL OR o.creationDate >= :start)
        AND (:end IS NULL OR o.creationDate <= :end)
        AND (:status IS NULL OR o.status = :status)
        ORDER BY o.creationDate DESC
        """)
    List<Order> findByFilters(
            @Param("start") @Nullable LocalDate start,
            @Param("end") @Nullable LocalDate end,
            @Param("status") @Nullable OrderStatus status);

    @EntityGraph(attributePaths = {"resource", "supplier", "comments"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findFullOrderById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"comments"})
    Optional<Order> findWithCommentsById(Long id);

    boolean existsByIdAndStatus(Long id, OrderStatus status);
}