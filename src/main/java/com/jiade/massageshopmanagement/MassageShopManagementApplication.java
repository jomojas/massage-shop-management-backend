package com.jiade.massageshopmanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jiade.massageshopmanagement.mapper")
public class MassageShopManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MassageShopManagementApplication.class, args);
    }

}
