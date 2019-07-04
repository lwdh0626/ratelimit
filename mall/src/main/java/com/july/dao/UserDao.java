package com.july.dao;

import com.july.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

//交给springboot管理的注解
@Repository
public class UserDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void save(User use) {
		// TODO Auto-generated method stub
		jdbcTemplate.update("insert into user (name) values(?)", use.getName());
	}
}