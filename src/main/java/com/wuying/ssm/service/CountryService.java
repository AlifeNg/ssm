package com.wuying.ssm.service;

import java.util.List;

import com.wuying.ssm.model.Country;

public interface CountryService extends BaseService<Country>{

	List<Country> selectByCountry(Country country, int page, int rows);
}
