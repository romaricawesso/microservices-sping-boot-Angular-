package org.romaric.customerservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
}
