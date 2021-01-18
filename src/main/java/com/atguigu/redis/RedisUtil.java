package com.atguigu.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Map;
import java.util.Set;

public class RedisUtil {
    public static void main(String[] args) {

        Jedis jedis = RedisUtil.getJedis();

        jedis.set("k1000","v1000");

        Set<String> keyset = jedis.keys("*");
        for (String key : keyset) {
            System.out.println(key);
        }

        Map<String, String> userMap = jedis.hgetAll("user:0101");
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        Set<Tuple> topTuple = jedis.zrevrangeWithScores("article:topn", 0, 2);
        for (Tuple tuple : topTuple) {
            System.out.println(tuple.getElement() + ":" + tuple.getScore());
        }


        //System.out.println(jedis.ping());

        jedis.close();
    }

    private static JedisPool jedisPool = null;

    public static Jedis getJedis() {
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setMinIdle(20);
            jedisPoolConfig.setMaxIdle(30);
            jedisPoolConfig.setBlockWhenExhausted(true);
            jedisPoolConfig.setMaxWaitMillis(5000);

            //从池中去链接后要进行测试
            //导致连接池中的链接坏掉:1.服务器端重启过 2. 网断过 3.服务器端维持空闲连接超时
            jedisPoolConfig.setTestOnBorrow(true);
            //时不时测试一下
            //jedisPoolConfig.setTestWhileIdle(true);
            jedisPool = new JedisPool("hadoop102",6379);
        }
        Jedis jedis = jedisPool.getResource();

        return jedis;
    }

    //could not get resource from pool
    //1.检查地址端口
    //2.检查bind是否注掉了
    //3.检查连接池资源是否耗尽,jedis使用后没有通过close还给池子
}
