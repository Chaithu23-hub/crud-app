package com.softsuave.crud.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Memory Comment: This class is a DTO (Data Transfer Object), NOT an Entity.
 * Its only purpose is to model the *shape* of the JSON object we expect
 * from the user when they try to log in.
 *
 * Notice there is no @Entity or @Id annotation. This object is never
 * saved directly to the database. It's just a temporary container for
 * the login data as it travels from the client's request to our controller.
 *
 * @Getter - Lombok annotation to automatically generate all getter methods.
 * @Setter - Lombok annotation to automatically generate all setter methods.
 * @AllArgsConstructor - Lombok annotation for a constructor with all fields.
 * @NoArgsConstructor - Lombok annotation for an empty constructor. Jackson (for JSON)
 * often needs this to create the object before populating it.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

    // Memory: These fields must *exactly* match the keys in the JSON body
    // that the client sends to the /auth/login endpoint.
    // e.g., { "username": "user", "password": "pass", "role": "ADMIN" }
    // Note: The 'role' field here isn't used by your login logic,
    // but it could be used for registration.

    private String username;
    private String password;
    private String role;
}
