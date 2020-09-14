package com.arfat.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @GetMapping
    public String hello() {
        return "Hello from customer-service";
    }

    @PostMapping
    public Customer hello(@RequestBody Customer customer) {
        final Object authorizedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("authorizedUser = " + authorizedUser);
        return customer;
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    String id = "";
    String name = "";
}
