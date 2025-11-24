package com.klabs.accountservice.application.port.output;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CachePort {

    void saveVerificationCode(String key, String code, Duration ttl);

    Optional<String> getVerificationCode(String key);

    Optional<Duration> getTTL(String key);

    void deleteVerificationCode(String key);

    void saveToken(String tokenID, String accountUUID, Duration ttl);

    boolean isTokenValid(String tokenID);

    void revokeToken(String tokenID, Duration ttl);

    void saveSession(String sessionID, String accountUUID, String ipAdders, String userAgent, Duration ttl);

    List<String> getActiveSessions(String accountUUID);

    void deleteSession(String sessionID, String accountUUID);

    void saveValue(String key, String json, Duration ttl);

    Optional<String> getValue(String key);

    void deleteValue(String key);

}
