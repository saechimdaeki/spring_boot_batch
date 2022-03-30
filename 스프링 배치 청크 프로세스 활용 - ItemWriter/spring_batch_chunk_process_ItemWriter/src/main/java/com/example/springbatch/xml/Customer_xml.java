package com.example.springbatch.xml;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Customer_xml {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final Date birthdate;
}