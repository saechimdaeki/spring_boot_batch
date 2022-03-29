package com.example.springbatch.DB;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String location;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
