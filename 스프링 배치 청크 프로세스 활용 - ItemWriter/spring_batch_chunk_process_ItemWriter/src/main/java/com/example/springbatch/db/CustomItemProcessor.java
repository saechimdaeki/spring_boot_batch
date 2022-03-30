package com.example.springbatch.db;

import org.modelmapper.ModelMapper;

import java.util.function.Function;

public class CustomItemProcessor implements Function<Customer_Jdbc, Customer2_Jpa> {

    ModelMapper modelMapper=new ModelMapper();

    @Override
    public Customer2_Jpa apply(Customer_Jdbc item) {

        return modelMapper.map(item, Customer2_Jpa.class);
    }
}
