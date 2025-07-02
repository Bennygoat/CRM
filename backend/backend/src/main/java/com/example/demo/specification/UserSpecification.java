package com.example.demo.specification;

import com.example.demo.dto.request.UserQueryRequest;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> build(UserQueryRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.getAccount() != null && !req.getAccount().isBlank()) {
                predicates.add(cb.like(root.get("account"), "%" + req.getAccount() + "%"));
            } else {
                System.out.println(req.getAccount());
            }

            if (req.getRoleName() != null) {
                predicates.add(cb.equal(root.get("roleName"), req.getRoleName()));
            } else {
                System.out.println(req.getRoleName());
            }

            if (req.getAuthorityCode() != null) {
                Join<User, Authority> join = root.join("authorities", JoinType.LEFT);
                predicates.add(cb.equal(join.get("code"), req.getAuthorityCode()));
            } else {
                System.out.println(req.getAuthorityCode());
            }
            //1. 你是要我這樣加?印出東西?

            if (req.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), req.getIsActive()));
            }

            if (req.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("accessStartDate"), req.getStartDateFrom()));
            }

            if (req.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("accessStartDate"), req.getStartDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
