package com.CreateAPI.WebApplication.Controller;

import com.CreateAPI.WebApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class VerificationController {

//    @Autowired
//    private EmailVerificationService verificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable("id") UUID id) throws Exception {
        try {
            if (userService.verifyUser(id)) {
//                logger.info("User verification successful for ID: {}", id);
                return new ResponseEntity<>("User verification successful!", HttpStatus.OK);
            } else {
//                logger.warn("Invalid verification ID: {}", id);
                System.out.println("Invalid Verification:"+id);
                return new ResponseEntity<>("Invalid verification ID", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
//            logger.error("Error verifying user: {}", e.getMessage());
            return new ResponseEntity<>("Error verifying user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
