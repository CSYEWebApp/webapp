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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final long VERIFICATION_TOKEN_EXPIRY_MINUTES = 1;

    @Override
    public boolean isVerified(UUID id) {
        Optional<User> optionalUser = userrepository.findById(id);
        return optionalUser.map(User::isVerified).orElse(false);
    }

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




    @Override
    public boolean verifyUser(UUID id) throws Exception {
        try {
            Optional<User> optionalUser = userrepository.findById(id);
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (isVerificationTokenValid(user)) {
                    user.setVerified(true);
                    userrepository.save(user);
                    return true;
                } else {
                    System.out.println("Verification ID expired for user: {}"+ user.getUsername());
//                    logger.warn("Verification ID expired for user: {}", user.getUsername());
                    return false;
                }
            }

//            System.out.println(UUID.fromString(id));
//            if (user != null) {
//                if (isVerificationTokenValid(user)) {
//                    user.setVerified(true);
//                    userrepo.save(user);
//                    return true;
//                } else {
//                    System.out.println("Verification ID expired for user: {}"+ user.getUsername());
////                    logger.warn("Verification ID expired for user: {}", user.getUsername());
//                    return false;
//                }
            else {
                System.out.println("User not found with verification ID: {}"+  id);
//                logger.warn("User not found with verification ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error verifying user: {}" + e.getMessage());
//            logger.error("Error verifying user: {}", e.getMessage());
            throw new Exception("Error during user verification"); //
        }
    }
    private boolean isVerificationTokenValid(User user) {
        LocalDateTime tokenCreatedAt = user.getAccountCreated();
        if (tokenCreatedAt == null) {
//            logger.warn("User verification token creation time not set");
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = Duration.between(tokenCreatedAt, now).toMinutes();
        return minutesElapsed <= VERIFICATION_TOKEN_EXPIRY_MINUTES;
    }
}
