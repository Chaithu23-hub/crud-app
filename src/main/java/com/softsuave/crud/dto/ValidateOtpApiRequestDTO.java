package com.softsuave.crud.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpApiRequestDTO {
    @NonNull
    private String otp;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
