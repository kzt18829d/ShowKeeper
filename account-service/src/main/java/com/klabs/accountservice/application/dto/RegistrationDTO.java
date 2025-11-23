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
public class RegistrationDTO {
    private String registrationID;
    private String email;
    private String login;
    private LocalDateTime expiresAt;
}
