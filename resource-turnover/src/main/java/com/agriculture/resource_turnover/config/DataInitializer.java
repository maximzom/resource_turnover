package com.agriculture.resource_turnover.config;

import com.agriculture.resource_turnover.models.*;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(ResourceRepository resourceRepo,
                                      SupplierRepository supplierRepo) {
        return args -> {
            Resource wheat = new Resource();
            wheat.setName("Wheat");
            wheat.setUnit("kg");
            wheat.setType("Grain");
            wheat.setQuantity(1000);
            wheat.setPrice(15.5);
            resourceRepo.save(wheat);

            Supplier supplier1 = new Supplier();
            supplier1.setName("AgroSupply Inc.");
            supplier1.setContactInfo("contact@agrosupply.com");
            supplierRepo.save(supplier1);
        };
    }
}