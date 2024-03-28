package com.CreateAPI.WebApplication.service;
import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    public User findByUsername(String username);

    public void updateUser(User existingUser, UserRequestDTO updateuserinfo);

    public User createUser(User user);
    public boolean verifyUser(UUID id) throws Exception;

    boolean isVerified(UUID id);

//    boolean verifyUser(UUID id) throws Exception;
}
