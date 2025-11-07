package com.softsuave.crud.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupApiRequestDTO {
    @NonNull
    private String username;
    @NonNull
    private String email;
}
