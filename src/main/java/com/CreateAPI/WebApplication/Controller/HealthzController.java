package com.CreateAPI.WebApplication.Controller;


import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.service.HealthService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthzController {


    @Autowired
    private HealthService healthservice;
    private static final Logger logger = LogManager.getLogger(HealthzController.class);

//    Logger LOG = LoggerFactory.getLogger(HealthzController.class);

    @RequestMapping(value = "/healthz", method = {RequestMethod.HEAD,RequestMethod.PATCH,RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<User> saveHealthCheck() {
        System.out.println("Method Not Allowed Healthz Mapping");
        logger.warn("Received a request with an unsupported HTTP method for health check.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }
    @GetMapping("/healthz")
    public ResponseEntity<Void> fetchHealthChecklist(@RequestBody(required = false) Object payload, HttpServletRequest request) {
        System.out.println("Healthz GetMapping");
        logger.info("Received a GET request for health check.");

        if (payload != null || !request.getParameterMap().isEmpty()) {
            logger.warn("Invalid request received for health check - non-empty payload or query parameters present.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).cacheControl(CacheControl.noCache()).build();
        }
        try {
            healthservice.checkDatabaseConnectivity();
            logger.info("Health check successful. Database connectivity verified.");
        } catch (Exception e) {
            logger.error("Error occurred while checking database connectivity: {}", e.getMessage());
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
        logger.warn("Received a request for an invalid URL");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .cacheControl(CacheControl.noCache())
                .body(null);
    }
}

