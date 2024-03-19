package com.CreateAPI.WebApplication.Controller;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserDTO;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import com.CreateAPI.WebApplication.service.AuthenticationService;
import com.CreateAPI.WebApplication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userservice;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthenticationService authenticationService;

    private static final Logger logger = LogManager.getLogger(HealthzController.class);
    @PutMapping("/v1/user/self")
    public ResponseEntity<Null> updateUser(@RequestBody UserRequestDTO updateUserInfo) {
        System.out.println("User PutMapping");
        String email = authenticationService.getCredentialsFromRequest(request)[0];
        logger.debug("Email retrieved from request: {}", email);
        System.out.println(email);
        User user = userservice.findByUsername(email);
        userservice.updateUser(user, updateUserInfo);
        logger.info("User information updated for email: {}", email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    @PostMapping("/v1/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {

        System.out.println("User PostMapping");
        if (StringUtils.isEmpty(user.getPassword())) {
            logger.error("Password field is empty. Cannot create user");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User existingUser = userservice.findByUsername(user.getUsername());

        if (existingUser != null) {
            logger.error("User Already Exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserDTO userdto = new UserDTO();
        System.out.println("User PostMapping" + user);
        userservice.createUser(user);
        System.out.println("User PostMapping");
        BeanUtils.copyProperties(user, userdto);
        System.out.println(user);
        logger.info("New user created with username: {}", user.getUsername());
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
            logger.info("User information retrieved for email: {}", email);
            return ResponseEntity.ok(userdto);
        } else {
            logger.error("User is not Found");
            return ResponseEntity.notFound().build();
        }
    }
}
