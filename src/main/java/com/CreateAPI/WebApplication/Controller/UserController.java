package com.CreateAPI.WebApplication.Controller;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserDTO;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import com.CreateAPI.WebApplication.service.AuthenticationService;
import com.CreateAPI.WebApplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userservice;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthenticationService authenticationService;


    @PutMapping("/v1/user/self")
    public ResponseEntity<Null> updateUser(@RequestBody UserRequestDTO updateUserInfo) {
        System.out.println("User PutMapping");
        String email = authenticationService.getCredentialsFromRequest(request)[0];
        System.out.println(email);
        User user = userservice.findByUsername(email);
        userservice.updateUser(user, updateUserInfo); //wef
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PostMapping("/v1/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {

        System.out.println("User PostMapping");
        User existingUser = userservice.findByUsername(user.getUsername());

        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserDTO userdto = new UserDTO();
        System.out.println("User PostMapping" + user);
        userservice.createUser(user);
        System.out.println("User PostMapping");
        BeanUtils.copyProperties(user, userdto);
        System.out.println(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userdto);
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
        String email = authenticationService.getCredentialsFromRequest(request)[0];
        System.out.println(email);
        User user = userservice.findByUsername(email);

        UserDTO userdto = new UserDTO();
        BeanUtils.copyProperties(user, userdto);
        if (user != null) {
            return ResponseEntity.ok(userdto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
