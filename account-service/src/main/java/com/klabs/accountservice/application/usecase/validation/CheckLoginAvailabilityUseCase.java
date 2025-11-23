package com.klabs.accountservice.application.usecase.validation;

import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.repository.DeletedAccountRepository;
import com.klabs.accountservice.domain.valueobject.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckLoginAvailabilityUseCase {

    private final AccountRepository accountRepository;
    private final DeletedAccountRepository deletedAccountRepository;

    public boolean execute(String login) {
        if (login == null || login.isBlank()) return false;

        try {
            Login loginVO = new Login(login);
            if (accountRepository.existsByLogin(loginVO))
                return false;
            return !accountRepository.existsByLogin(loginVO)
                    && !deletedAccountRepository.existsByOriginalLogin(login);
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}
