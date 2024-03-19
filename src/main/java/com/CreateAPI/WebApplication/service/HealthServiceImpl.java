package com.CreateAPI.WebApplication.service;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthServiceImpl implements HealthService{


    @Autowired
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(HealthServiceImpl.class);

    @Transactional
    @Override
    public boolean checkDatabaseConnectivity(){
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
//            System.out.println("Database connectivity check successful.");
            logger.info("Database connectivity check successful.");

            return true;
        } catch (Exception e) {
//            System.err.println("Database connectivity check failed.");
            logger.error("Database connectivity check failed.",e);
//            e.printStackTrace();
            return false;
        }
    }




}
