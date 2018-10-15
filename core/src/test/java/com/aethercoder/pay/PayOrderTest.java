package com.aethercoder.pay;

import com.aethercoder.TestApplication;
import com.aethercoder.foundation.util.NetworkUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @auther Guo Feiyan
 * @date 2018/3/21 下午3:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class PayOrderTest {

    /**
     * [bithumb]
     * apikey = f7fa5605c34744c805d9b3b968e555fe
     * apisecret = c9752f7c184ae1e6db6a4fd4fa5fc673
     */

    /**
     * [coinone]
     * Access Token=8b7fe685cc474ff789cae69b00414e35
     * Secret Key=9b3f97b36bd24d76b42626f3e81c2b01
     */

    private String apikeyBit = "fc3a198305f02ec04dc2dd94a8f9382b";
    private String secretKeyBit = "7cdd814d776c5f81fe13a06814be994d";
    private String apikeyCoin = "8b7fe685cc474ff789cae69b00414e35";
    private String apisecretCoin = "9b3f97b36bd24d76b42626f3e81c2b01";

    private String bitApi = "https://api.bithumb.com/info/account";//"https://api.bithumb.com/trade/market_sell";
    private String coinApi = "https://api.coinone.co.kr/v2/order/limit_buy/";



/*
    @Autowired
    private OrderService orderService;*/

    @Test
    public void test() {

        Api_Client api = new Api_Client(apikeyBit,
                secretKeyBit);

        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("units", "0.1");
        rgParams.put("currency", "QTUM");
        try {
            String result = api.callApi("/trade/market_sell", rgParams);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.printf("");

        //coinone
        String orderbookCoinOne = "https://api.coinone.co.kr/orderbook/?currency=qtum";
        String responseBodyCoin = NetworkUtil.callHttpReq(HttpMethod.GET, orderbookCoinOne, null, null, String.class);

        System.out.printf(responseBodyCoin);
    }
}
