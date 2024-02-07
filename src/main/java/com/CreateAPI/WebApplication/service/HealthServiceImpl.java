package com.CreateAPI.WebApplication.service;

import com.CreateAPI.WebApplication.entity.User;
import com.CreateAPI.WebApplication.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthServiceImpl implements HealthService{


    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Override
    public boolean checkDatabaseConnectivity(){
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("Database connectivity check successful.");
            return true;
        } catch (Exception e) {
            System.err.println("Database connectivity check failed.");
            e.printStackTrace();
            return false;
        }
    }




}
