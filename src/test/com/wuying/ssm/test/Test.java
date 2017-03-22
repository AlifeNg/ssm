package com.wuying.ssm.test;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.wuying.ssm.model.Country;
import com.wuying.ssm.service.CountryService;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类  
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})  
  
public class Test {  
    private static Logger logger = Logger.getLogger(Test.class);  
//    private ApplicationContext ac = null;  
    @Resource  
    private CountryService countryService = null;  
  
//  @Before  
//  public void before() {  
//      ac = new ClassPathXmlApplicationContext("spring-mybatis.xml");  
//      countryService = (CountryService) ac.getBean("countryService");  
//  }  
  
    @org.junit.Test
    public void test1() {  
    	Country country = countryService.selectById(1);  
        // System.out.println(user.getUserName());  
        // logger.info("值："+user.getUserName());  
        logger.info(JSON.toJSONString(country));  
    }  
}  