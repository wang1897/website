package com.aethercoder.cache;

import com.aethercoder.TestApplication;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.contants.CommonConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by hepengfei on 05/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ContractCacheTest {
    @Autowired
    private ContractDao contractDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Test
    public void testContractCache() {

        /*ShardedJedis shardedJedis = shardedJedisPool.getResource();
        shardedJedis.set("test", "test");
        String a = shardedJedis.get("test");
        Assert.assertEquals(a, "test");

        System.out.println("testContractCache");
        Contract c = contractDao.findContractByNameAndType("QBT", WalletConstants.CONTRACT_QTUM_TYPE);

        Assert.assertNotNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "QBT"));
        System.out.println(contractDao.findContractByNameAndType("QBT",WalletConstants.CONTRACT_QTUM_TYPE).getAddress());

        Contract c1 = contractDao.findContractByNameAndIsDeleteIsFalseAndIshowIsFalse("QBT");
        Assert.assertNotNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "QBT-false-false"));
        Contract c2 = contractDao.findContractByNameAndIsDeleteIsFalseAndIshowIsFalse("QBT");
        System.out.println(c1.getAddress());
        System.out.println(c2.getAddress());

        Contract c3 = contractDao.findContractsByIsDeleteIsFalseAndIshowIsFalseOrderBySequenceAsc().get(0);
        Assert.assertNotNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "findContractsByIsDeleteIsFalseAndIshowIsFalseOrderBySequenceAsc"));
        Contract c4 = contractDao.findContractsByIsDeleteIsFalseAndIshowIsFalseOrderBySequenceAsc().get(0);
        System.out.println(c3.getAddress());
        System.out.println(c4.getAddress());

        contractDao.findAll();
        Assert.assertNotNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "findAll"));

        contractDao.save(c);
        Assert.assertNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "QBT"));
        Assert.assertNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "QBT-false-false"));
        Assert.assertNull(redisTemplate.opsForValue().get(RedisConstants.REDIS_CACHE_NAME_CONTRACT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_SMART_CONTRACT + "findContractsByIsDeleteIsFalseAndIshowIsFalseOrderBySequenceAsc"));*/
    }
}
