package com.softsuave.crud.dto;

import lombok.Getter;
import lombok.Setter;

// RegistrationRequestDTO.java (New File)
@Getter
@Setter
public class UserRegistrationRequestDTO {
    private String username;
    private String password;
    // Note: There is NO 'role' field here.
}
