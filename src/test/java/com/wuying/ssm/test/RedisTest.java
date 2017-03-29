package com.wuying.ssm.test;

import com.wuying.ssm.util.DataCacheClient;
import org.junit.*;

/**
 * Created by wuying on 2017/3/29.
 */
public class RedisTest {


    @org.junit.Test
    public void test() throws Exception {
        System.out.println(DataCacheClient.getCacheValue("a"));
    }
}
