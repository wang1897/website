package com.aethercoder.error;

import com.aethercoder.TestApplication;
import com.aethercoder.core.entity.error.QtumTxError;
import com.aethercoder.core.service.error.QtumTxErrorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 22/03/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class QtumTxErrorTest {
    @Autowired
    private QtumTxErrorService qtumTxErrorService;

    @Test
    public void testSave(){
        QtumTxError qtumTxError = new QtumTxError();
        qtumTxError.setAccountNo("111111");
        qtumTxError.setErrorCode("-26");
        qtumTxError.setErrorMessage("dust");
        qtumTxError.setTxHash("testHash");
        qtumTxError.setTxRaw("testRaw");
        qtumTxError.setFee("aa");
        qtumTxError.setDeviceType("IOS");
        qtumTxError.setAppVersion("2.4.1");
        qtumTxErrorService.saveError(qtumTxError);
    }
}
