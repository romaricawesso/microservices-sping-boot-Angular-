package org.romaric.customerservice.graphql;

import graphql.GraphQLError;
import org.romaric.customerservice.domain.Customer;
import org.romaric.customerservice.repository.CustomerRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;


@Controller
public class CustomerGraphQLService {

    private final CustomerRepository customerRepository;

    public CustomerGraphQLService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @QueryMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @QueryMapping
    public Customer getCustomerById(@Argument Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @MutationMapping
    public Customer saveCustomer(@Argument("customer") CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.name)
                .email(customerDto.email)
                .build();
        return customerRepository.save(customer);
    }

    record CustomerDto(String name, String email){

    }

    @GraphQlExceptionHandler
    public GraphQLError exceptionHandler(Exception e){
        return GraphQLError.newError().message(e.getMessage()).build();
    }


}
