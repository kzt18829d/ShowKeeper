package com.klabs.accountservice.application.mapper;

import com.klabs.accountservice.application.dto.AccountDTO;
import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.model.OAuthProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDTO(Account account);

    @Named("mapOAuthProviders")
    default List<String> mapOAuthProviders(List<OAuthProvider> providers) {
        if (providers == null || providers.isEmpty())
            return Collections.emptyList();
        return providers.stream().map(OAuthProvider::getProviderName).collect(Collectors.toList());
    }

    List<AccountDTO> toDTOList(List<Account> accounts);

}
