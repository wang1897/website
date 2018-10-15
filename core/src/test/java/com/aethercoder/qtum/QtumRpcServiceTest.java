package com.aethercoder.qtum;

import com.aethercoder.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class QtumRpcServiceTest {

//    @Autowired
//    private QtumService qtumService;
//
//    String address = "QMhttetMYzkjdPsSjFG3gz7g3ekgML5C2n";

    @Test
    public void testGetHistory() throws Exception {
        System.out.println("qqqq");
//        qtumService.getTransHistoryByAddress(new String[]{address}, 10, 1, null, null);
//        qtumService.getTransHistoryByAddress(new String[]{address}, 10, 1, 1000L, 10000L);
    }
}
