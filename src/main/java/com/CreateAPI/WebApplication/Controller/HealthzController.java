package com.CreateAPI.WebApplication.Controller;


import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.service.HealthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthzController {


    @Autowired
    private HealthService healthservice;

    @RequestMapping(value = "/healthz", method = {RequestMethod.HEAD,RequestMethod.PATCH,RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<User> saveHealthCheck() {
        System.out.println("Method Not Allowed Healthz Mapping");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }
    @GetMapping("/healthz")
    public ResponseEntity<Void> fetchHealthChecklist(@RequestBody(required = false) Object payload, HttpServletRequest request) {
        System.out.println("Healthz GetMapping");
        if (payload != null || !request.getParameterMap().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        try {
            healthservice.checkDatabaseConnectivity();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }
    @RequestMapping(value = "/**", method = {RequestMethod.HEAD,RequestMethod.POST,RequestMethod.PATCH,RequestMethod.DELETE,RequestMethod.PUT,RequestMethod.OPTIONS,RequestMethod.GET})
    public ResponseEntity<Void> InvalidUrl(){
        System.out.println("Not Found Healthz Mapping");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }


}

