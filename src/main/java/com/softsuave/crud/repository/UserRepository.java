package com.softsuave.crud.repository;

import com.softsuave.crud.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);



}
