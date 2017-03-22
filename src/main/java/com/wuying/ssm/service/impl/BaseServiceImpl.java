package com.wuying.ssm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuying.ssm.service.BaseService;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

	@Autowired
	protected Mapper<T> mapper;

	public int save(T entity) {
		return mapper.insertSelective(entity);
	}

	public int delete(Object id) {
		return mapper.deleteByPrimaryKey(id);
	}

	public int update(T entity) {
		return mapper.updateByPrimaryKeySelective(entity);
	}

	public int merge(T entity) {
		return mapper.updateByPrimaryKey(entity);
	}

	public List<T> selectAll() {
		return mapper.selectAll();
	}

	public T selectOne(T t) {
		return mapper.selectOne(t);
	}

	public T selectById(Object id) {
		return mapper.selectByPrimaryKey(id);
	}
	
	@Override
	public PageInfo<T> selectPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
	
	@Override
    public PageInfo<T> selectPageByExample(Example example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = mapper.selectByExample(example);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
	
}
