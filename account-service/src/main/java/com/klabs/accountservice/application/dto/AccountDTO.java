package com.klabs.accountservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    private UUID uuid;
    private String login;
    private String email;
    private String accountStatus;
    private LocalDateTime registrationDate;
    private LocalDateTime lasLogInDate;
    private boolean emailVerified;
    private boolean hasPassword;
    private List<String> oauthProviders;
}
