package com.CreateAPI.WebApplication.service;
import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User findByUsername(String username);

    public void updateUser(User existingUser, UserRequestDTO updateuserinfo);

    public User createUser(User user);

}
