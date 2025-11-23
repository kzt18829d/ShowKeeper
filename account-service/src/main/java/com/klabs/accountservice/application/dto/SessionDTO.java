package com.klabs.accountservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDTO {
    private String sessionID;
    private String ipAdders;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private boolean current;
}
