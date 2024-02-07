package com.CreateAPI.WebApplication.service;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import com.CreateAPI.WebApplication.repository.UserRepository;
import jakarta.persistence.EntityManager;
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


    @Override
    public User findByUsername(String username) {
        return userrepository.findByUsername(username);
    }

    @Override
    public void updateUser(User existingUser, UserRequestDTO updateuserinfo) {
        existingUser.setFirstName(updateuserinfo.getFirstName());
        existingUser.setLastName(updateuserinfo.getLastName());
        String hashedPassword = passwordEncoder.encode(updateuserinfo.getPassword());
        existingUser.setPassword(hashedPassword);
        userrepository.save(existingUser);

    }

    @Override
    public User createUser(User user) {
        System.out.println(user.getPassword());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return  userrepository.save(user);
    }
}
