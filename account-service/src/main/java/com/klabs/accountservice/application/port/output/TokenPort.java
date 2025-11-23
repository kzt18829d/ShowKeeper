package com.klabs.accountservice.application.port.output;

import com.klabs.accountservice.domain.valueobject.Token;

import java.util.UUID;

public interface TokenPort {

    /**
     * @throws com.klabs.accountservice.shared.exception.TokenExpiredException
     * @throws com.klabs.accountservice.shared.exception.InvalidTokenException
     */
    Token parseAndValidateToken(String tokenString);

    Token generateAccessToken(UUID accountUUID);

    Token generateRefreshToken(UUID accointUUID);

    UUID extractAccountUUID(String tokenString);

}
