package com.wuying.ssm.controller;

import com.wuying.ssm.service.UserInfoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userInfo")
public class UserInfoController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserInfoService userInfoService;

}