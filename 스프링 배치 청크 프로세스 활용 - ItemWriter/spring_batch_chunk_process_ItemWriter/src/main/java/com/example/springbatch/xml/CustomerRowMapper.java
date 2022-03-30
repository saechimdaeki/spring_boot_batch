package com.example.springbatch.xml;

import com.example.springbatch.flatFileItemWriter.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer_xml> {
	@Override
	public Customer_xml mapRow(ResultSet rs, int i) throws SQLException {
		return new Customer_xml(rs.getLong("id"),
				rs.getString("firstName"),
				rs.getString("lastName"),
				rs.getDate("birthdate"));
	}
}