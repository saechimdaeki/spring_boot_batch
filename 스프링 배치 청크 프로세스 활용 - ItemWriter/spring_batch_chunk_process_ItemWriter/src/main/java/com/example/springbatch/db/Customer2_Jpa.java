package com.example.springbatch.db;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter @Setter
@Entity
@NoArgsConstructor
public class Customer2_Jpa {

    @Id
    private long id;
    private String firstName;
    private String lastName;
    private Date birthdate;
}