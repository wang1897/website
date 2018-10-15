package com.aethercoder.cache;

import com.aethercoder.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 10/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class SysConfigTest {
//    @Autowired
//    private SysConfigDao sysConfigDao;
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testConfigCache() {
        String configName = "remote_url";
        System.out.println(configName);
//        sysConfigDao.findSysConfigByName(configName);
//        Assert.assertTrue(redisTemplate.hasKey(RedisConstants.REDIS_KEY_SYS_CONFIG + configName));
//
//        SysConfig sysConfig = new SysConfig();
//        sysConfig.setName("test");
//        sysConfig.setValue("aaa");
//        SysConfig ps = sysConfigDao.save(sysConfig);
//        Assert.assertFalse(redisTemplate.hasKey(RedisConstants.REDIS_KEY_SYS_CONFIG + configName));
//
//        sysConfigDao.delete(ps);
    }
}
