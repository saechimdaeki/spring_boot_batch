package com.example.springbatch.db;

import com.example.springbatch.json.Customer_json;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer_Jdbc> {
	@Override
	public Customer_Jdbc mapRow(ResultSet rs, int i) throws SQLException {
		return new Customer_Jdbc(rs.getLong("id"),
				rs.getString("firstName"),
				rs.getString("lastName"),
				rs.getDate("birthdate"));
	}
}