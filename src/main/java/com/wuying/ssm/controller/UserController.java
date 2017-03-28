package com.wuying.ssm.controller;

import com.wuying.ssm.model.User;
import com.wuying.ssm.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserService userService;

	@RequestMapping("/loginUI")
	public String loginUI(){
		return "login";
	}

	@RequestMapping("/login")
	public String login(User user, Model model){
		User queryUser = userService.selectOne(user);
		if(queryUser != null){
			model.addAttribute("user", queryUser);
			model.addAttribute("msg", "登录成功!");
		} else {
			model.addAttribute("msg", "账号或密码错误!");
		}
		return "index";
	}

	@RequestMapping("/changeTheme")
	public String changeTheme(User user, Model model){
		int a = userService.update(user);
		if(a > 0){
			model.addAttribute("msg", "修改主题成功!");
		} else {
			model.addAttribute("msg", "修改主题失败!");
		}
		return "index";
	}

}