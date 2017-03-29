package com.wuying.ssm.util.mongo.dao;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.wuying.ssm.util.DynamicSqlParameter;
import org.mongodb.morphia.MapreduceResults;
import org.mongodb.morphia.MapreduceType;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Map;

public interface IMongoDao<T> {

    MapreduceResults<T> mapReduce(Class<?> clz, MapreduceType type, Query<?> q,
                                  String map, String reduce, String finalize,
                                  Map<String, Object> scopeFields, Class<T> outputType);

    WriteResult delete(Query<T> q, WriteConcern wc);

    /**
     * 删除collection
     * 
     * @param clz
     *            collection所对应的Entity
     */
    public void dropCollection(Class<T> clz);

    public void save(T paramT) throws Exception;

    public void save(T paramT, WriteConcern paramWriteConcern) throws Exception;

    public void insertBatch(Iterable<T> paramIterable, Class<T> paramClass)
        throws Exception;

    public void insertBatch(Iterable<T> paramIterable, Class<T> paramClass,
                            WriteConcern paramWriteConcern) throws Exception;

    public T get(Class<T> paramClass, Object paramObject) throws Exception;

    public void delete(Class<T> paramClass, Object paramObject)
        throws Exception;

    public void update(T paramT) throws Exception;

    /**
     * 此接口调用真正调用的是save接口，mongo的save接口会比较id，如果id存在，则更新这条记录，如果id对应的记录不存在，
     * 则会插入一条新的记录， 所以在更新的时候，需要确保id是存在的（需要注意String与ObjectId类型不同）
     * 
     * @param paramIterable
     * @param paramClass
     * @throws Exception
     */
    public void updateBatch(Iterable<T> paramIterable, Class<T> paramClass)
        throws Exception;

    public Query<T> getQuery(Class<T> paramClass);

    public List<T> query(Class<T> paramClass, Query<T> paramQuery)
        throws Exception;

    public List<T> query(Class<T> paramClass,
                         DynamicSqlParameter paramDynamicSqlParameter) throws Exception;

    public long getCount(Query<T> paramQuery) throws Exception;

    public long getCount(Class<T> paramClass,
                         DynamicSqlParameter paramDynamicSqlParameter) throws Exception;

    public void setDatasource(String paramString);

    public String getDatasource();
}
