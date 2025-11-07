package com.softsuave.crud.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Memory Comment: This is your main security class. It serves TWO purposes:
 * 1. @Entity: It's a Hibernate entity that maps to the 'users' table in your database.
 * 2. implements UserDetails: It's the object Spring Security uses internally to
 * represent an authenticated user (a "principal").
 *
 * This is a very common and efficient pattern. When your CustomUserDetailsService
 * fetches a 'Users' object from the repository, it can return it directly to
 * Spring Security without any conversion, because it *is* a UserDetails object.
 *
 * @Entity - Maps this class to the 'users' table.
 * @Getter/@Setter/@AllArgsConstructor/@NoArgsConstructor - Lombok annotations for
 * boilerplate code.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Memory: These are the core columns for authentication.
    private String username;
    private String email;
    private String password;
    private String role;
    private String otp;
    private LocalDateTime otpExpirationTime;














    /**
     * Memory Comment: This is the MOST IMPORTANT method for security.
     *
     * Spring Security calls this method to find out what "roles" or "permissions"
     * this user has. The @PreAuthorize("hasRole('ADMIN')") annotation works
     * by checking the list of authorities returned by this method.
     *
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We create a new SimpleGrantedAuthority using the 'role' string.
        // Spring Security will automatically add the "ROLE_" prefix for checks.
        // For example, if 'role' is "ADMIN", Spring Security sees "ROLE_ADMIN".
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * Memory Comment: These four methods are "flags" required by the UserDetails
     * interface. For most simple applications, we just return 'true' to indicate
     * that all accounts are always active and valid.
     *
     * You *could* add fields to your database (e.g., a boolean 'isLocked')
     * and return that value here to implement account locking.
     */

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account is never expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password is never expired
    }

    @Override
    public boolean isEnabled() {
        return true; // Account is always enabled
    }

}
