package com.klabs.accountservice.application.usecase.registration;

import com.klabs.accountservice.application.port.output.CachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelRegistrationUseCase {
    private final CachePort cachePort;

    private void cleanRegistrationCache(String registrationID) {
        cachePort.deleteVerificationCode("registration:verification:" + registrationID);
        cachePort.deleteValue("registration:data:" + registrationID);
    }

    public void execute(String registrationID) {
        if (registrationID != null && !registrationID.isBlank())
            cleanRegistrationCache(registrationID);
    }
}
