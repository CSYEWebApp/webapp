package com.CreateAPI.WebApplication.service;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import com.CreateAPI.WebApplication.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    @Override
    public User findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userrepository.findByUsername(username);
    }

    @Override
    public void updateUser(User existingUser, UserRequestDTO updateuserinfo) {
        logger.debug("Updating user information for user with username: {}", existingUser.getUsername());
        existingUser.setFirstName(updateuserinfo.getFirstName());
        existingUser.setLastName(updateuserinfo.getLastName());
        String hashedPassword = passwordEncoder.encode(updateuserinfo.getPassword());
        existingUser.setPassword(hashedPassword);
        userrepository.save(existingUser);
        logger.info("User information updated successfully for user with username: {}", existingUser.getUsername());
    }

    @Override
    public User createUser(User user) {
        logger.debug("Creating new user with username: {}", user.getUsername());
        System.out.println(user.getPassword());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        logger.info("New user created with username: {}: ", user.getUsername());
        return  userrepository.save(user);
    }
}
