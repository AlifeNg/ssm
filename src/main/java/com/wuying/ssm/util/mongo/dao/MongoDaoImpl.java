package com.wuying.ssm.util.mongo.dao;

import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.wuying.ssm.util.DynamicSqlParameter;
import com.wuying.ssm.util.mongo.property.MongoDatasource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.MapreduceResults;
import org.mongodb.morphia.MapreduceType;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class MongoDaoImpl<T> implements IMongoDao<T> {
    private static final Log logger = LogFactory.getLog(MongoDaoImpl.class);
    private String datasource = null;

    @Override
    public MapreduceResults<T> mapReduce(Class<?> clz, MapreduceType type,
            Query<?> q, String map, String reduce, String finalize,
            Map<String, Object> scopeFields, Class<T> outputType) {
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clz);

        return ds.mapReduce(type, q, map, reduce, finalize, scopeFields,
                outputType);
    }

    @Override
    public WriteResult delete(Query<T> q, WriteConcern wc) {
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(q.getEntityClass());
        if (wc == null) {
            wc = WriteConcern.FSYNC_SAFE;
        }
        return ds.delete(q, wc);
    }

    @Override
    public void save(T entity) throws Exception {
        save(entity, WriteConcern.NORMAL);
    }

    @Override
    public void save(T entity, WriteConcern wc) throws Exception {
        if (entity == null) {
            throw new Exception("entity can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(entity.getClass());

        ds.save(entity, wc);
    }

    @Override
    public void insertBatch(Iterable<T> it, Class<T> clazz) throws Exception {
        insertBatch(it, clazz, WriteConcern.NORMAL);
    }

    @Override
    public void insertBatch(Iterable<T> it, Class<T> clazz, WriteConcern wc)
        throws Exception {
        if (it == null) {
            throw new Exception("entity can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        ds.save(it, wc);
    }

    @Override
    public T get(Class<T> clazz, Object id) throws Exception {
        if (id == null) {
            throw new Exception("id can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        if ((id instanceof String))
            return (T) ((Query) ds.createQuery(clazz).field("_id").equal(id))
                    .get();
        if ((id instanceof ObjectId)) {
            return (T) ((Query) ds.createQuery(clazz).field("_id").equal(id))
                    .get();
        }
        return null;
    }

    @Override
    public void delete(Class<T> clazz, Object id) throws Exception {
        if (id == null) {
            throw new Exception("id can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        if ((id instanceof String))
            ds.delete((Query) ds.createQuery(clazz).field("_id").equal(id));
        else if ((id instanceof ObjectId))
            ds.delete((Query) ds.createQuery(clazz).field("_id").equal(id));
    }

    @Override
    public void update(T entity) throws Exception {
        if (entity == null)
            throw new Exception("entity can not be null!");
        try {
            Class clazz = entity.getClass();
            Datastore ds = MongoDatasource.getInstance(getDatasource())
                    .getDatastore(clazz);
            Field[] fields = clazz.getDeclaredFields();

            Object idValue = getId(entity, fields);
            if (idValue == null) {
                throw new Exception(
                        "The field named \"id\" in the entity can not be a null value!");
            }

            ds.save(entity, WriteConcern.FSYNC_SAFE);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void updateBatch(Iterable<T> paramIterable, Class<T> paramClass)
        throws Exception {
        if (paramIterable == null) {
            throw new Exception("entities can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(paramClass);
        Iterator<T> iterator = paramIterable.iterator();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            Field[] fields = paramClass.getDeclaredFields();
            Object idValue = getId(entity, fields);
            if (idValue == null) {
                throw new Exception(
                        "The field named \"id\" in the entity can not be a null value!");
            }
        }
        ds.save(paramIterable, WriteConcern.FSYNC_SAFE);
    }

    @Override
    public List<T> query(Class<T> clazz, Query<T> query) throws Exception {
        if (query == null) {
            throw new Exception(
                    "parameter named \"query\" is null, please call method \"getQuery\" to get a new Query Object first!");
        }

        logger.debug("调用MongoDao执行query：" + query.toString());
        return query.asList();
    }

    @Override
    public Query<T> getQuery(Class<T> clazz) {
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        return ds.createQuery(clazz);
    }

    @Override
    public List<T> query(Class<T> clazz, DynamicSqlParameter param)
        throws Exception {
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        Query<T> query = ds.createQuery(clazz);
        try {
            if (param != null)
                convertParam(query, param);
        } catch (Exception e) {
            logger.error(e);
        }
        logger.debug("调用MongoDao执行query：" + query.toString());
        return query.asList();
    }

    private Object getId(T entity, Field[] fields) throws Exception {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Annotation[] b = field.getDeclaredAnnotations();
            for (int j = 0; j < b.length; j++) {
                if (b[j].annotationType().equals(
                        Class.forName("org.mongodb.morphia.annotations.Id"))) {
                    return getValue(entity, field);
                }
            }
        }
        return null;
    }

    private Object getValue(T entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);

            // Method getMethod = entity.getClass()
            // .getDeclaredMethod("get"
            // + StringUtils.capitalize(field
            // .getName()),
            // new Class[0]);
            //
            // return getMethod.invoke(entity, new Object[0]);
        } catch (Exception e) {
            logger.warn("Invoke field error", e);
        }
        return null;
    }

    private void convertParam(Query<T> query, DynamicSqlParameter param) {
        if (param.getEqual() != null) {
            Map<String, String> map = param.getEqual();
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    try {
                        query = query.field(key).equal(map.get(key));
                    } catch (Exception e) {
                        // TODO: handle exception
                        logger.error(e);
                    }
            }
        }
        if (param.getLike() != null) {
            Map<String, String> map = param.getLike();
            Iterator<String> it = map.keySet().iterator();
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
                    try {
                        query = query.field(key).greaterThanOrEq(map.get(key));
                    } catch (Exception e) {
                        // TODO: handle exception
                        logger.error(e);
                    }

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
        if (param.getGreaterThanOrEqual() != null) {
            Map map = param.getGreaterThanOrEqual();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    query = query.field(key).greaterThanOrEq(map.get(key));
            }
        }
        if (param.getLessThanOrEqual() != null) {
            Map map = param.getLessThanOrEqual();
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if ((key != null) && (map.get(key) != null))
                    query = query.field(key).lessThanOrEq(map.get(key));
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
                && (param.getPageNo().intValue() > 0)) {
            query.offset((param.getPageNo().intValue() - 1)
                    * param.getPageSize().intValue());
            query.limit(param.getPageSize().intValue());
        }
    }

    @Override
    public long getCount(Query<T> query) throws Exception {
        if (query == null) {
            throw new Exception(
                    "parameter named \"query\" is null, please call method \"getQuery\" to get a new Query Object first!");
        }
        return query.countAll();
    }

    @Override
    public long getCount(Class<T> clazz, DynamicSqlParameter param)
        throws Exception {
        if (param == null) {
            throw new Exception("param can not be a null value!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clazz);
        Query query = ds.createQuery(clazz);
        convertParam(query, param);
        return query.countAll();
    }

    @Override
    public String getDatasource() {
        return this.datasource;
    }

    @Override
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    @Override
    public void dropCollection(Class<T> clz) {
        if (clz == null) {
            throw new IllegalArgumentException("Entity can not be null!");
        }
        Datastore ds = MongoDatasource.getInstance(getDatasource())
                .getDatastore(clz);
        DBCollection collection = ds.getCollection(clz);
        if (collection != null) {
            collection.drop();
        }
    }
}
