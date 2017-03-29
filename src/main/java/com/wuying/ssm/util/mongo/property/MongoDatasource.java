package com.wuying.ssm.util.mongo.property;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;
import com.wuying.ssm.util.EnvironmentUtil;
import org.apache.commons.lang.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MongoDatasource {
    private static Logger logger = LoggerFactory
            .getLogger(MongoDatasource.class);

    private static EnvironmentUtil env = EnvironmentUtil
            .getInstance("mongodb.properties");
    private static Map<String, MongoProperties> props;
    private static Map<String, MongoClient> mongos;
    private static Map<String, MongoDatasource> mongoDSMap = new HashMap<>();
    private static Morphia morphia;
    private static Map<String, Datastore> datastores;
    private final String datasourceName;

    private MongoDatasource() {
        this.datasourceName = getDefaultDatasource();
        syncInit();
    }

    public static synchronized MongoDatasource getInstance() {
        return getInstance(getDefaultDatasource());
    }

    public static synchronized MongoDatasource getInstance(
            String datasourceName) {
        MongoDatasource mongoDatasource = mongoDSMap.get(datasourceName);
        if (mongoDatasource == null) {
            mongoDatasource = new MongoDatasource(datasourceName);
            mongoDSMap.put(datasourceName, mongoDatasource);
        }
        return mongoDatasource;
    }

    private MongoDatasource(String datasourceName) {
        this.datasourceName = (StringUtils.isBlank(datasourceName)
                ? getDefaultDatasource() : datasourceName);

        syncInit();
    }

    private synchronized void syncInit() {
        if (props == null) {
            Map<String, MongoProperties> propsLocal = new HashMap<String, MongoProperties>();
            String[] datasources = env.getPropertyValue("DATA_SOURCES")
                    .split(",");

            for (String datasource : datasources)
                propsLocal.put(datasource,
                        convertProperties(datasource, env.getProperties()));
            setProps(propsLocal);
        }
    }

    public boolean checkDBConfig(String dbName) {
        if (props == null) {
            syncInit();
        }

        return props.containsKey(dbName);
    }

    public synchronized MongoClient getMongo() {
        if (mongos == null)
            mongos = new HashMap<String, MongoClient>();
        if ((mongos.get(this.datasourceName) == null)) {
            // || (mongos.get(this.datasourceName).getConnector() == null)
            // || (!mongos.get(this.datasourceName).getConnector().isOpen())) {
            try {
                // mongos.put(this.datasourceName,
                // new Mongo(getProps().get(this.datasourceName)
                // .getAddresses(), getProps()
                // .get(this.datasourceName).getMongoOption()));
                MongoClient mc = new MongoClient(
                        getProps().get(this.datasourceName).getAddresses(),
                        getProps().get(this.datasourceName)
                                .getMongoClientOption());
                mongos.put(this.datasourceName, mc);
            } catch (NullPointerException e) {
                throw new MongoException(
                        "Failed to initialize MongoDB connection pool called \""
                                + this.datasourceName
                                + "\", please check the configuration file \"mongodb.properties\".",
                        e);
            }

            logger.info(
                    "Completed to initialize MongoDB connection pool called \""
                            + this.datasourceName + "\"......");
        }

        return mongos.get(this.datasourceName);
    }

    public synchronized Datastore getDatastore(Class<?> clazz) {
        MongoProperties properties = getProps().get(this.datasourceName);
        String dbName = properties.getDbName();
        return getDatastore(dbName, clazz);
    }

    // TODO
    // 这一块要优化，不能每次执行mongo操作都要去检查索引等。应该在服务启动的时候，把所有的@Entity一次性加载进去。索引在加载的时候处理。
    public synchronized Datastore getDatastore(String mongoDBName,
            Class<?> clazz) {
        if (datastores == null) {
            datastores = new HashMap<String, Datastore>();
        }
        Datastore ds = datastores.get(this.datasourceName);
        if ((ds == null)
                || (!ds.getDB().getName().equalsIgnoreCase(mongoDBName))) {
            // Mongo mongo = getMongo();
            MongoClient mongo = getMongo();
            ds = getMorphia().createDatastore(mongo, mongoDBName);
            logger.info("Create datastore connection called " + mongoDBName
                    + "......");
            datastores.put(this.datasourceName, ds);
        }
        if (clazz != null) {
            // 判断类对应的集合是否已经处理过，如果处理过了，则不需要在去执行ensureCaps
            boolean isExist = getMorphia().getMapper().isMapped(clazz);
            if (!isExist) {
                getMorphia().map(new Class[] { clazz });
                ds.ensureCaps();
            }
            // Doing this on an existing system, with existing indexes and
            // capped collections, should take no time (and do nothing). by
            // morphia wiki
            // 即使之前已经处理过索引，此处也必须执行索引，因为可以随时允许在Entity上添加 @Indexed注解
            ds.ensureIndexes();
        }
        return ds;
    }

    public synchronized Morphia getMorphia() {
        if (morphia == null) {
            morphia = new Morphia();
        }
        return morphia;
    }

    public synchronized DB getDB() {
        return getDB(getProps().get(this.datasourceName).getDbName());
    }

    public synchronized DB getDB(String dbName) {
        return dbName == null ? getDB() : getMongo().getDB(dbName);
    }

    public synchronized GridFS getGridFS() {
        return getGridFS(getProps().get(this.datasourceName).getDbName(),
                getProps().get(this.datasourceName).getPicAddress());
    }

    public synchronized GridFS getGridFS(String dbName, String fileTableName) {
        return new GridFS(getDB(dbName), fileTableName);
    }

    private static MongoProperties convertProperties(String datasource,
            Properties properties) {
        MongoProperties prop = new MongoProperties();
        try {
            prop.setAddresses(getAddresses((String) properties
                    .get(datasource + ".MONGODB-ADDRESSES")));

            prop.setConnectionsPerHost(Integer.parseInt((String) properties
                    .get(datasource + ".CONNECTIONS_PER_HOST")));

            prop.setConnectTimeout(Integer.parseInt(
                    (String) properties.get(datasource + ".CONNECT_TIMEOUT")));

            prop.setDbName(
                    (String) properties.get(datasource + ".MONGO_DB_DBNAME"));

            prop.setDocAddress((String) properties
                    .get(datasource + ".MONGO_DB_DOC_ADDRESS"));

            prop.setDocExt((String) properties.get(datasource + ".DOC_EXT"));
            prop.setDomainName(
                    (String) properties.get(datasource + ".DOMAIN_NAME"));

            prop.setPicAddress((String) properties
                    .get(datasource + ".MONGO_DB_PIC_ADDRESS"));

            prop.setFileExt((String) properties.get(datasource + ".FILE_EXT"));
            prop.setMaxWaitTime(Integer.parseInt(
                    (String) properties.get(datasource + ".MAX_WAIT_TIME")));

            prop.setPicExt((String) properties.get(datasource + ".PIC_EXT"));
            prop.setSocketTimeout(Integer.parseInt(
                    (String) properties.get(datasource + ".SOCKET_TIMEOUT")));

            prop.setThreadsAllowedToBlockForConnectionMultiplier(
                    Integer.parseInt((String) properties
                            .get(datasource + ".THREADS_ALLOWED_TO_BLOCK")));
        } catch (NullPointerException e) {
            throw new MongoException(
                    "The configurations of Mongodb are invalid, please check the configuration file \"mongodb.properties\".",
                    e);
        } catch (NumberFormatException e) {
            throw new MongoException(
                    "The configurations of Mongodb are invalid, please check the configuration file \"mongodb.properties\".",
                    e);
        }

        return prop;
    }

    private static List<ServerAddress> getAddresses(String addresses) {
        List<ServerAddress> result = new ArrayList<ServerAddress>();
        for (String address : addresses.split(" ")) {
            String[] add = address.split(":");
            try {
                ServerAddress serverAddress = new ServerAddress(add[0],
                        Integer.parseInt(add[1]));

                result.add(serverAddress);
            } catch (NumberFormatException e) {
                throw new MongoException(
                        "The configurations of Mongodb address are invalid, please check the configuration file \"mongodb.properties\".!",
                        e);
            }

        }

        return result;
    }

    public Map<String, MongoProperties> getProps() {
        return props;
    }

    public static void setProps(Map<String, MongoProperties> props) {
        MongoDatasource.props = props;
    }

    public static String getDefaultDatasource() {
        String datasources = env.getPropertyValue("DATA_SOURCES");
        if (!StringUtils.isBlank(datasources)) {
            return datasources.split(",")[0];
        }
        return "";
    }
}
