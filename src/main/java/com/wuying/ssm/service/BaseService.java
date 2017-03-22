package com.wuying.ssm.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.entity.Example;

public interface BaseService<T> {

	int save(T entity);

	int delete(Object id);

	int update(T entity);

	int merge(T entity);

	List<T> selectAll();

	T selectOne(T t);

	T selectById(Object id);

	PageInfo<T> selectPageByExample(Example example, int pageNum, int pageSize);

	PageInfo<T> selectPage(int pageNum, int pageSize);

}
