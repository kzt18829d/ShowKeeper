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
public class VerificationDTO {
    private String verificationID;
    private String email;
    private LocalDateTime expiredAt;
    private String message;
}
