package com.klabs.accountservice.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class OAuthProvider {

    Long id;

    @NotNull
    private String providerName;

    @NotNull
    private String providerUserID;

    @NotNull
    private LocalDateTime linkedAt;


    public static OAuthProvider create(String providerName, String providerUserID) {
        if (!(providerName != null && (providerUserID != null && !providerUserID.isBlank())))
            throw new IllegalStateException("Invalid provider data");
        return new OAuthProvider(null, providerName, providerUserID, LocalDateTime.now());
    }

    public boolean isSameProvider(String providerName) {
        return this.providerName.equalsIgnoreCase(providerName);
    }

    public boolean isSameUser(String providerUserID) {
        return this.providerUserID.equals(providerUserID);
    }


}
