package com.wuying.ssm.test;

import com.wuying.ssm.model.UserInfo;
import com.wuying.ssm.util.mongo.service.IMongoService;
import com.wuying.ssm.util.mongo.service.MongoServiceFactory;

/**
 * Created by wuying on 2017/3/29.
 */
public class MongoTest {

    protected static IMongoService<UserInfo> mongoService = (IMongoService<UserInfo>) MongoServiceFactory
            .getMongoService("LOG");

    public static void main(String[] args){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1231321);
        userInfo.setUsername("张三");
        userInfo.setEmail("408112489@qq.com");
        try {
            mongoService.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
