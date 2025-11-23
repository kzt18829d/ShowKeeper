package com.klabs.accountservice.application.usecase.validation;

import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.repository.DeletedAccountRepository;
import com.klabs.accountservice.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckEmailAvailabilityUseCase {

    private final AccountRepository accountRepository;
    private final DeletedAccountRepository deletedAccountRepository;

    public boolean execute(String email) {
        if (email == null || email.isBlank()) return false;

        try {
            Email emailVO = new Email(email);

            return !accountRepository.existsByEmail(emailVO) &&
                    !deletedAccountRepository.existsByOriginalEmail(email);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
