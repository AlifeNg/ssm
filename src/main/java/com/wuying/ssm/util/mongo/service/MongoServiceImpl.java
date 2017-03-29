package com.wuying.ssm.util.mongo.service;

import com.mongodb.WriteConcern;
import com.wuying.ssm.util.DynamicSqlParameter;
import com.wuying.ssm.util.mongo.dao.IMongoDao;
import com.wuying.ssm.util.mongo.dao.MongoDaoImpl;
import org.apache.commons.lang.StringUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class MongoServiceImpl<T> implements IMongoService<T> {

    @Autowired
    private IMongoDao<T> mongoDao;
    private String datasource;

    @Override
    public void save(T entity) throws Exception {
        getMongoDao().save(entity);
    }

    @Override
    public void save(T entity, WriteConcern wc) throws Exception {
        getMongoDao().save(entity, wc);
    }

    @Override
    public void insertBatch(Iterable<T> it, Class<T> clazz) throws Exception {
        getMongoDao().insertBatch(it, clazz);
    }

    @Override
    public void insertBatch(Iterable<T> it, Class<T> clazz, WriteConcern wc)
        throws Exception {
        getMongoDao().insertBatch(it, clazz, wc);
    }

    @Override
    public void insertBatch(Iterable<T> it) throws Exception {
        getMongoDao().insertBatch(it, null);
    }

    @Override
    public T get(Class<T> clazz, Object id) throws Exception {
        return getMongoDao().get(clazz, id);
    }

    @Override
    public void delete(Class<T> clazz, Object id) throws Exception {
        getMongoDao().delete(clazz, id);
    }

    @Override
    public void delete(Query<T> q, WriteConcern wc) throws Exception {
        getMongoDao().delete(q, wc);
    }

    @Override
    public void update(T entity) throws Exception {
        getMongoDao().update(entity);
    }

    @Override
    public Query<T> getQuery(Class<T> clazz) {
        return getMongoDao().getQuery(clazz);
    }

    @Override
    public List<T> query(Class<T> clazz, Query<T> query) throws Exception {
        return getMongoDao().query(clazz, query);
    }

    @Override
    public List<T> query(Class<T> clazz, DynamicSqlParameter param)
        throws Exception {
        return getMongoDao().query(clazz, param);
    }

    @Override
    public long getCount(Query<T> query) throws Exception {
        return getMongoDao().getCount(query);
    }

    @Override
    public long getCount(Class<T> clazz, DynamicSqlParameter param)
        throws Exception {
        return getMongoDao().getCount(clazz, param);
    }

    @Override
    public Query<T> convertParamToQuery(Query<T> query,
            DynamicSqlParameter param) {
        if (param.getEqual() != null) {
            Map map = param.getEqual();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    query = query.field(key).equal(map.get(key));
            }
        }
        if (param.getLike() != null) {
            Map map = param.getLike();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null)) {
                    String value = (String) map.get(key);
                    value = StringUtils.removeEnd(value, "%");
                    value = StringUtils.removeStart(value, "%");
                    query = query.field(key).contains(value);
                }
            }
        }
        if (param.getInMap() != null) {
            Map map = param.getInMap();
            Iterator it = map.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null)) {
                    Object value = map.get(key);
                    if ((value instanceof Iterable)) {
                        if (((Iterable) map.get(key)).iterator().hasNext())
                            query = query.field(key).in((Iterable) value);
                    } else if ((value instanceof String[])) {
                        List temList = new ArrayList();
                        for (String val : ((String[]) value)[0].split(",")) {
                            temList.add(val);
                        }
                        query = query.field(key).in(temList);
                    }
                }
            }
        }
        if (param.getStartwith() != null) {
            Map map = param.getStartwith();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    query = query.field(key).greaterThanOrEq(map.get(key));
            }
        }
        if (param.getEndwith() != null) {
            Map map = param.getEndwith();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    query = query.field(key).lessThan(map.get(key));
            }
        }
        if (param.getOrder() != null) {
            if ((param.getSort() != null)
                    && (param.getSort().equalsIgnoreCase("desc"))
                    && (!StringUtils.startsWith(param.getOrder(), "-"))) {
                query.order("-" + param.getOrder());
            } else
                query.order(param.getOrder());
        }
        if ((param.getPageSize().intValue() != 1)
                && (param.getPageNo().intValue() != 0)) {
            query.offset((param.getPageNo().intValue() - 1)
                    * param.getPageSize().intValue());
            query.limit(param.getPageSize().intValue());
        }
        return query;
    }

    @Override
    public String getDatasource() {
        return this.datasource;
    }

    @Override
    public synchronized void setDatasource(String datasource) {
        if (StringUtils.isBlank(datasource)) {
            return;
        }
        this.datasource = datasource;
        // 确保线程安全
        if (getMongoDao() == null
                || !datasource.equals(getMongoDao().getDatasource())) {
            setMongoDao(new MongoDaoImpl());
        }
        getMongoDao().setDatasource(datasource);
    }

    /**
     * @return the mongoDao
     */
    public IMongoDao<T> getMongoDao() {
        if (mongoDao == null) {
            mongoDao = new MongoDaoImpl<>();
        }
        return mongoDao;
    }

    /**
     * @param mongoDao
     *            the mongoDao to set
     */
    public void setMongoDao(IMongoDao<T> mongoDao) {
        this.mongoDao = mongoDao;
    }
}