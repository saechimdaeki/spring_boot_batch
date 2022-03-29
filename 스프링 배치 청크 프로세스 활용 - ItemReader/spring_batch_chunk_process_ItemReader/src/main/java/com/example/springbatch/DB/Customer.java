package com.example.springbatch.DB;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Customer {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    private String birthdate;
}
