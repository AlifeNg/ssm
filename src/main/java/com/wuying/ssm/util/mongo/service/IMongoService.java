package com.wuying.ssm.util.mongo.service;

import com.mongodb.WriteConcern;
import com.wuying.ssm.util.DynamicSqlParameter;
import org.mongodb.morphia.query.Query;

import java.util.List;

public interface IMongoService<T> {

    public void save(T paramT) throws Exception;

    public void save(T paramT, WriteConcern paramWriteConcern) throws Exception;

    public void insertBatch(Iterable<T> paramIterable, Class<T> paramClass)
        throws Exception;

    public void insertBatch(Iterable<T> paramIterable, Class<T> paramClass,
                            WriteConcern paramWriteConcern) throws Exception;

    public void insertBatch(Iterable<T> paramIterable) throws Exception;

    public T get(Class<T> paramClass, Object paramObject) throws Exception;

    public void delete(Class<T> paramClass, Object paramObject)
        throws Exception;

    public void delete(Query<T> q, WriteConcern wc) throws Exception;

    public void update(T paramT) throws Exception;

    public Query<T> getQuery(Class<T> paramClass);

    public List<T> query(Class<T> paramClass, Query<T> paramQuery)
        throws Exception;

    public List<T> query(Class<T> paramClass,
                         DynamicSqlParameter paramDynamicSqlParameter) throws Exception;

    public long getCount(Query<T> paramQuery) throws Exception;

    public long getCount(Class<T> paramClass,
                         DynamicSqlParameter paramDynamicSqlParameter) throws Exception;

    public Query<T> convertParamToQuery(Query<T> paramQuery,
                                        DynamicSqlParameter paramDynamicSqlParameter);

    public void setDatasource(String paramString);

    public String getDatasource();
}