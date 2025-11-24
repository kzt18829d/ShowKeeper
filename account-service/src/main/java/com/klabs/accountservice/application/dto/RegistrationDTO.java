package com.klabs.accountservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RegistrationDTO {
    private String registrationID;
    private String email;
    private String login;
    private LocalDateTime expiresAt;
}
