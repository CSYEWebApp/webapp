package com.CreateAPI.WebApplication.repository;

import com.CreateAPI.WebApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    User findByUsername(String username);

    public Optional<User> findById(UUID id);
}
