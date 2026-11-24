package org.romaric.customerservice.mcp;

import org.romaric.customerservice.domain.Customer;
import org.romaric.customerservice.repository.CustomerRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMCPService {

    private final CustomerRepository customerRepository;

    public CustomerMCPService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @McpTool(description = "Récupérer la liste de tous les clients")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @McpTool(description = "Récupérer un client par son ID")
    public Customer getCustomerById(@McpToolParam(description = "L'ID du client") Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID : " + id));
    }

    @McpTool(description = "Créer un nouveau client")
    public Customer saveCustomer(
            @McpToolParam(description = "Le nom du client") String name,
            @McpToolParam(description = "L'adresse email du client") String email) {
        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .build();
        return customerRepository.save(customer);
    }
}
