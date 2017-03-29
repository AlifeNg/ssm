package com.wuying.ssm.util.mongo.property;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

import java.io.Serializable;
import java.util.List;

public class MongoProperties implements Serializable {
    private static final long serialVersionUID = -3445670775427110216L;
    private String domainName = "file.4000966666.com";

    private String dbName = "chpt2";

    private String picAddress = "photo";

    private String docAddress = "file";

    private String fileExt = "jpg,jpeg,png,gif,bmp,doc,docx,xls,xlsx,ppt,pptx,txt,pdf,zip,rar,apk";

    private String docExt = "doc,docx,xls,xlsx,ppt,pptx,txt,pdf,zip,rar,apk";

    private String picExt = "jpg,jpeg,png,gif,bmp";
    private List<ServerAddress> addresses;
    private int connectionsPerHost = 100;

    private int threadsAllowedToBlockForConnectionMultiplier = 10;

    private int maxWaitTime = 5000;

    private int socketTimeout = 2000;

    private int connectTimeout = 1500;

    public MongoOptions getMongoOption() {
        MongoOptions options = new MongoOptions();
        // options.autoConnectRetry = true;
        options.socketKeepAlive = true;
        // options.maxAutoConnectRetryTime = 0L;
        options.connectionsPerHost = this.connectionsPerHost;
        options.maxWaitTime = this.maxWaitTime;
        options.socketTimeout = this.socketTimeout;
        options.connectTimeout = this.connectTimeout;
        options.threadsAllowedToBlockForConnectionMultiplier = this.threadsAllowedToBlockForConnectionMultiplier;

        return options;
    }

    public MongoClientOptions getMongoClientOption() {
        MongoClientOptions.Builder bulid = new Builder();
        // bulid.autoConnectRetry(true)
        bulid.socketKeepAlive(true)
                // .maxAutoConnectRetryTime(0L)
                .connectionsPerHost(this.connectionsPerHost)
                .maxWaitTime(this.maxWaitTime).socketTimeout(this.socketTimeout)
                .connectTimeout(this.connectTimeout)
                .threadsAllowedToBlockForConnectionMultiplier(
                        this.threadsAllowedToBlockForConnectionMultiplier);
        MongoClientOptions options = bulid.build();

        return options;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDocAddress() {
        return this.docAddress;
    }

    public void setDocAddress(String docAddress) {
        this.docAddress = docAddress;
    }

    public String getFileExt() {
        return this.fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getDocExt() {
        return this.docExt;
    }

    public void setDocExt(String docExt) {
        this.docExt = docExt;
    }

    public String getPicExt() {
        return this.picExt;
    }

    public void setPicExt(String picExt) {
        this.picExt = picExt;
    }

    public int getConnectionsPerHost() {
        return this.connectionsPerHost;
    }

    public void setConnectionsPerHost(int connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public int getMaxWaitTime() {
        return this.maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getThreadsAllowedToBlockForConnectionMultiplier() {
        return this.threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(
            int threadsAllowedToBlockForConnectionMultiplier) {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    public List<ServerAddress> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(List<ServerAddress> addresses) {
        this.addresses = addresses;
    }

    public String getPicAddress() {
        return this.picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }
}
