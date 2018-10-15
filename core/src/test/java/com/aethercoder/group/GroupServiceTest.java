package com.aethercoder.group;

import com.aethercoder.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 12/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class GroupServiceTest {
//    @Autowired
//    private GroupService groupService;

    @Test
    public void testSyncGroup() {
        String accountNo = "268092";
        System.out.println(accountNo);
//        String timestamp = null;
//        UserInfo userInfo = groupService.syncUserGroupInfo(accountNo, timestamp);
   }

}
