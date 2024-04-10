package com.CreateAPI.WebApplication.Controller;

import com.CreateAPI.WebApplication.Configure.PubSubConfig;
import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.entity.UserDTO;
import com.CreateAPI.WebApplication.entity.UserRequestDTO;
import com.CreateAPI.WebApplication.service.AuthenticationService;
import com.CreateAPI.WebApplication.service.UserService;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userservice;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    Publisher publisher;

    private static final Logger logger = LogManager.getLogger(HealthzController.class);
    @PutMapping("/v2/user/self")
    public ResponseEntity<Null> updateUser(@RequestBody UserRequestDTO updateUserInfo) {
        System.out.println("User PutMapping");
        String email = authenticationService.getCredentialsFromRequest(request)[0];
        logger.debug("Email retrieved from request: {}", email);

        try {
            User user = userservice.findByUsername(email);
            boolean isVerified = user.isVerified();
            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println(email);
        User user = userservice.findByUsername(email);
        userservice.updateUser(user, updateUserInfo);
        logger.info("User information updated for email: {}", email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    @PostMapping("/v2/user")
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
        publishMessageToPubSub(user.getUsername(),user.getId());
        logger.info("New user created with username: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(userdto);
    }

    @GetMapping("/v2/user/self")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {

        String email = authenticationService.getCredentialsFromRequest(request)[0];
        System.out.println(email);
        try {
            User user = userservice.findByUsername(email);
            boolean isVerified = user.isVerified();
            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    private void publishMessageToPubSub(String username, UUID uuid) {
        String messageData = "{\"email\": \"" + username + "\",\"id\": \"" + uuid + "\" }";
        byte[] data = messageData.getBytes(StandardCharsets.UTF_8);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFrom(data))
                .build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        ApiFutures.addCallback(
                messageIdFuture,
                new ApiFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Failed to publish message to Pub/Sub: {}", t.getMessage());
                    }
                    @Override
                    public void onSuccess(String messageId) {
                        logger.info("Message published to Pub/Sub with ID: {}", messageId);
                    }
                },
                MoreExecutors.directExecutor()
        );
    }
}
