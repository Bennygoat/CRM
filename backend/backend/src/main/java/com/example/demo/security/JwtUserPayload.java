package com.example.demo.security;

import com.example.demo.entity.Authority;
import com.example.demo.entity.CCustomer;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserPayload {
    private Long id;
    private String account;
    private String name;
    private String role;
    private List<String> authorities;

    public static JwtUserPayload fromCustomer(CCustomer c) {
        return new JwtUserPayload(
                c.getCustomerId(),
                c.getAccount(),
                c.getCustomerName(), //TODO(joshkuei): Changed from customerName() to name().
                "CUSTOMER",
                null
        );
    }

    public static JwtUserPayload fromUser(User u) {
        List<String> codes = u.getAuthorities().stream()
                .map(Authority::getCode)
                .collect(Collectors.toList());
        return new JwtUserPayload(
                u.getUserId(),
                u.getAccount(),
                u.getUserName(),
                u.getRoleName(),
                codes
        );
    }

}
