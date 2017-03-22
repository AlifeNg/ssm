package com.wuying.ssm.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.wuying.ssm.model.Country;
import com.wuying.ssm.service.CountryService;

@Controller
public class TestController {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private CountryService countryService;

    @RequestMapping("/test")
    public String test() {
        Country country = new Country();
        country.setCountrycode("123");

        // System.out.println(JSONArray.toJSONString(countryService.selectAll()));
        System.out.println(JSONArray.toJSONString(countryService.selectByCountry(country, 1, 1)));
        // System.out.println(JSONArray.toJSONString(countryService.selectPage(1,2)));
        logger.info("----------------------------------------------"
                + JSONArray.toJSONString(countryService.selectByCountry(country, 1, 1)));
        // System.out.println(country.getCountrycode());
        return "test";
    }
}
