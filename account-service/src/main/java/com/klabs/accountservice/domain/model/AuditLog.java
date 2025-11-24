package com.klabs.accountservice.domain.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

    private Long id;

    private UUID accountUuid;

    private String action;

    private String ipAddress;

    private String userAgent;

    private String detailsJson;

    private LocalDateTime createdAt;

    public static AuditLog create(UUID accountUuid, String action, String ipAddress, String userAgent, Map<String, Object> details) {
        if (accountUuid == null)
            throw new IllegalArgumentException("Account UUID can't be null");
        if (action == null || action.isBlank())
            throw  new IllegalArgumentException("Action can't be null or blank");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(details);

            return new AuditLog(null, accountUuid, action, ipAddress, userAgent, json, LocalDateTime.now());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuditLog login(UUID accountUuid, String ipAddress, String userAgent) {
        return create(accountUuid, "LOGIN", ipAddress, userAgent, null);
    }

    public static AuditLog passwordChanged(UUID accountUuid, String ipAddress, String userAgent) throws JsonProcessingException {
        return create(accountUuid, "PASSWORD_CHANGED", ipAddress, userAgent, null);
    }

    public static AuditLog emailUpdated(UUID accountUuid, String oldEmail, String newEmail, String ipAddress, String userAgent) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("oldEmail", oldEmail);
        map.put("newEmail", newEmail);
        return create(accountUuid, "EMAIL_UPDATED", ipAddress, userAgent, map);
    }

    public static AuditLog loginUpdated(UUID accountUuid, String oldLogin, String newLogin, String ipAddress, String userAgent) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("oldLogin", oldLogin);
        map.put("newLogin", newLogin);
        return create(accountUuid, "LOGIN_UPDATED", ipAddress, userAgent, map);
    }

}
