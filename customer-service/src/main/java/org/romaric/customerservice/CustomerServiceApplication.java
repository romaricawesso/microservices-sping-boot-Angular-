package org.romaric.customerservice;

import org.romaric.customerservice.domain.Customer;
import org.romaric.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository){
        return args -> {
            customerRepository.save(Customer.builder().name("Elon Musk").email("musk@gmail.com").build());
            customerRepository.save(Customer.builder().name("Larry fink").email("larry@gmail.com").build());
            customerRepository.save(Customer.builder().name("Jeff Bezos").email("jeff@gmail.com").build());
        };
    }

}
