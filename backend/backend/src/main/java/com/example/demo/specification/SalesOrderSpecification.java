package com.example.demo.specification;

import com.example.demo.entity.SalesOrder;
import com.example.demo.enums.SalesOrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class SalesOrderSpecification {

    public static Specification<SalesOrder> findByCriteria(
            Long customerId,
            SalesOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String keyword){

        return (root, query, criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();

            if(customerId != null){
                predicates.add(criteriaBuilder.equal(root.get("customer").get("customerId"), customerId));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderStatus"), status));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }


            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("orderNumber")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("customerName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), likePattern)
                ));
            }


//            query.orderBy(criteriaBuilder.desc(root.get("orderDate")), criteriaBuilder.desc(root.get("salesOrderId")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}