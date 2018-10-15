package com.aethercoder.core.util;

import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.entity.wallet.Contract;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 31/01/2018.
 */
@RunWith(SpringRunner.class)
public class BeanUtilsTest {

    @Test
    public void testCopyPropertiesWithoutNull() {
        Contract contractSrc = new Contract();
        contractSrc.setName("111");
        contractSrc.setAddress("address");
        contractSrc.setIshow(true);
        Contract contractTar = new Contract();
        contractTar.setName("222");
        contractTar.setApiAddress("api");
        contractTar.setIshow(false);
        BeanUtils.copyPropertiesWithoutNull(contractSrc, contractTar, "ishow");
        Assert.assertEquals(contractTar.getAddress(), "address");
        Assert.assertEquals(contractTar.getName(), "111");
        Assert.assertEquals(contractTar.getApiAddress(), "api");
        Assert.assertEquals(contractTar.getIshow(), false);
        Assert.assertNull(contractTar.getContractDecimal());
    }
}
