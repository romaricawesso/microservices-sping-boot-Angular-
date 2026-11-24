package org.romaric.customerservice.rest;

import org.romaric.customerservice.domain.Customer;
import org.romaric.customerservice.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

//CQRS : Command Query Responsability Sefregation
@RestController
@RequestMapping("/api")
public class CustomerRestService {

    private CustomerRepository customerRepository;

    public CustomerRestService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("customer not found"));
    }

    @PostMapping("/customers")
    public Customer saveCustomer(@RequestBody Customer customer){
        return customerRepository.save(customer);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String exceptionHandler(Exception e){
        return e.getMessage();
    }


}
