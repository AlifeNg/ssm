package com.wuying.ssm.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.wuying.ssm.model.User;
import com.wuying.ssm.service.UserService;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

}