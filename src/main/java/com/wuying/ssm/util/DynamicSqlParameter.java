/* 
 *
 * Copyright (C) 1999-2012 IFLYTEK Inc.All Rights Reserved. 
 * 
 * FileName：DynamicRequest.java
 * 
 * Description：动态查询请求参数，后续进行封装便于查询
 * 
 * History：
 * Version   Author      Date            Operation 
 * 1.0    baoxu   2015年3月10日上午9:55:15          Create   
 */
package com.wuying.ssm.util;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author baoxu
 *
 * @version 1.0
 * 
 */
public class DynamicSqlParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数使用Like方式查询设置key与value{字段名：值}
     */
    private Map<String, String> like;

    /**
     * 参数使用==方式查询设置key与value{字段名：值}
     */
    private Map<String, String> equal;

    /**
     * 参数使用!=方式查询设置key与value{字段名：值}
     */
    private Map<String, String> notequal;

    /**
     * 参数使用以xxx开始的方式查询设置key与value{字段名：值}
     */
    private Map<String, String> startwith;

    /**
     * 参数使用以xxx结束方式查询设置key与value{字段名：值}
     */
    private Map<String, String> endwith;

    /**
     * 参数使用>=的方式查询
     */
    private Map<String, Object> greaterThanOrEqual;
    
    /**
     * 参数使用<=的方式查询
     */
    private Map<String, Object> lessThanOrEqual;
    /**
     * 参数使用in方式查询ID{值}
     */
    private Map<String, Object> inMap;

    /**
     * 参数使用not in方式查询ID{值}
     */
    private Map<String, Object> notInMap;

    /** 去除自身ID情况下使用 */
    private String noId;
    
    /**参数使用IS NOT NULL*/
    private List<String> notNull;

    /** 开始页码 */
    private Integer pageNo = 1;

    /** 每页多少 */
    private Integer pageSize = 10;

    /** 排序字段 */
    private String order;

    private String sort;

    /**
     * 
     * @param equalKey
     *            等于的条件
     * @param equalIdValue
     *            等于的值
     * @param updateValueMap
     *            需要修改的键值对
     * @return
     * @throws CtfoAppException
     */
    public static DynamicSqlParameter getUpdateDynamicSqlParameter(
            String equalKey, String equalIdValue,
            Map<String, Object> updateValueMap) {

        DynamicSqlParameter parameter = null;
        if (null == equalKey || null == equalIdValue || null == updateValueMap
                || "".equals(equalKey) || "".equals(equalIdValue)) {
            return parameter;
        }

        parameter = new DynamicSqlParameter();
        Map<String, String> equalMap = new HashMap<String, String>();
        equalMap.put(equalKey, equalIdValue);
        parameter.setEqual(equalMap);
        return parameter;
    }

    public String getNoId() {
        return noId;
    }

    public void setNoId(String noId) {
        this.noId = noId;
    }

    /**
     * 开始的记录数
     * 
     * @return
     */
    public Integer getStartNum() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 结束的记录数
     * 
     * @return
     */
    public Integer getEndNum() {
        // 页数＊每页行数
        return this.getStartNum() + pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        // 更改为数据库相应的order名称
        this.order = order;
    }

    public Map<String, String> getLike() {
        return like;
    }

    public void setLike(Map<String, String> like) {
        this.like = like;
    }

    public Map<String, String> getEqual() {
        return equal;
    }

    public void setEqual(Map<String, String> equal) {
        this.equal = equal;
    }

    public Map<String, Object> getInMap() {
        return inMap;
    }

    public void setInMap(Map<String, Object> inMap) {
        this.inMap = inMap;
    }

    public Map<String, Object> getNotInMap() {
        return notInMap;
    }

    public void setNotInMap(Map<String, Object> notInMap) {
        this.notInMap = notInMap;
    }

    public Map<String, String> getNotequal() {
        return notequal;
    }

    public void setNotequal(Map<String, String> notequal) {
        this.notequal = notequal;
    }

    public Map<String, String> getStartwith() {
        return startwith;
    }

    public void setStartwith(Map<String, String> startwith) {
        this.startwith = startwith;
    }

    public Map<String, String> getEndwith() {
        return endwith;
    }

    public void setEndwith(Map<String, String> endwith) {
        this.endwith = endwith;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        if (!(like == null || like.size() == 0)) {
            buff.append("[包含条件:").append(like).append("]");
        }
        if (!(equal == null || equal.size() == 0)) {
            buff.append("[等于条件:").append(equal).append("]");
        }
        if (!(notequal == null || notequal.size() == 0)) {
            buff.append("[不等于条件:").append(notequal).append("]");
        }
        if (!(startwith == null || startwith.size() == 0)) {
            buff.append("[以...条件开始:").append(startwith).append("]");
        }
        if (!(endwith == null || endwith.size() == 0)) {
            buff.append("[以...条件结束:").append(endwith).append("]");
        }
        if (!(inMap == null || inMap.size() == 0)) {
            buff.append("[在...中条件:").append(inMap).append("]");
        }
        if (!(notInMap == null || notInMap.size() == 0)) {
            buff.append("[不在...中条件:").append(notInMap).append("]");
        }
        return buff.toString();
    }
    
    public DynamicSqlParameter copy() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
        return (DynamicSqlParameter) ois.readObject();
    }

    /**
     * @return the pageNo
     */
    public Integer getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo
     *            the pageNo to set
     */
    public void setPageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 1) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            this.pageSize = 20;
        } else {
            this.pageSize = pageSize;
        }
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getNotNull() {
        return notNull;
    }

    public void setNotNull(List<String> notNull) {
        this.notNull = notNull;
    }

    /**
     * @return the greaterThanOrEqual
     */
    public Map<String, Object> getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /**
     * @param greaterThanOrEqual the greaterThanOrEqual to set
     */
    public void setGreaterThanOrEqual(Map<String, Object> greaterThanOrEqual) {
        this.greaterThanOrEqual = greaterThanOrEqual;
    }

    /**
     * @return the lessThanOrEqual
     */
    public Map<String, Object> getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /**
     * @param lessThanOrEqual the lessThanOrEqual to set
     */
    public void setLessThanOrEqual(Map<String, Object> lessThanOrEqual) {
        this.lessThanOrEqual = lessThanOrEqual;
    }
}
