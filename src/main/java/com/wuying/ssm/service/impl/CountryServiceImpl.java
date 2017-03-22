package com.wuying.ssm.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.wuying.ssm.model.Country;
import com.wuying.ssm.service.CountryService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

@Service
public class CountryServiceImpl extends BaseServiceImpl<Country> implements CountryService {

	@Override
    public List<Country> selectByCountry(Country country, int page, int rows) {
        Example example = new Example(Country.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(country.getCountryname())) {
            criteria.andLike("countryname", "%" + country.getCountryname() + "%");
        }
        if (StringUtil.isNotEmpty(country.getCountrycode())) {
            criteria.andLike("countrycode", "%" + country.getCountrycode() + "%");
        }
        if (country.getId() != null) {
            criteria.andEqualTo("id", country.getId());
        }
        //分页查询
        PageHelper.startPage(page, rows);
        return (List<Country>) mapper.selectByExample(example);
    }
}
