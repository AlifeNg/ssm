package com.wuying.ssm.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.wuying.ssm.model.UserInfo;
import com.wuying.ssm.service.UserInfoService;

@Service
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfo> implements UserInfoService {

	private Logger logger = Logger.getLogger(this.getClass().getName());

}