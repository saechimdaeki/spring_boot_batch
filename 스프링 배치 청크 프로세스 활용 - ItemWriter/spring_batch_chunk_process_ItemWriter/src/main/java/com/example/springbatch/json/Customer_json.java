package com.example.springbatch.json;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Customer_json {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final Date birthdate;
}