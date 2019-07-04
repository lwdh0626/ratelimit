package com.july.service.impl;

import com.july.dao.UserDao;
import com.july.model.User;
import com.july.service.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserserviceImpl implements Userservice {

	@Autowired
	private UserDao userDao;

	@Override
	public void save(User user)  {
		userDao.save(user);
	}
	

}

