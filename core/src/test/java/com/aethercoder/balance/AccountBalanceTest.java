package com.aethercoder.balance;

import com.aethercoder.TestApplication;
import com.aethercoder.core.dao.CustomerDao;
import com.aethercoder.core.dao.GuessRecordDao;
import com.aethercoder.core.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

/**
 * @auther Guo Feiyan
 * @date 2018/1/17 下午5:02
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class AccountBalanceTest {


    @Autowired
    private ExchangeLogService exchangeLogService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private GuessNumberGameService guessNumberGameService;

    @Autowired
    private GamesService gamesService;

    @Autowired
    private GuessRecordDao guessRecordDao;

    @Autowired
    private ContractService contractService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private URLDecipheringService urlDecipheringService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AndroidService androidService;
    /**
     * Qbao Energy概况
     */
    @Test
    public void getExchangeLogProfile() throws ParseException, NoSuchAlgorithmException {
       /* Android android = new Android();
        android.setDescriptionJa("ja");
        android.setDescription("zh");
        android.setDescriptionEn("en");
        android.setDescriptionKo("ko");
        android.setForceUpdate(false);
        android.setPath("");
        android.setSource(true);
        android.setVersionCode("3.1");
        android.setVersionName("3.1");
        androidService.saveAndroid(android);*/
       /* Locale currentLocale = new Locale("en");
        LocaleContextHolder.setLocale(currentLocale);
        Android androids = androidService.findIOSLatest();*/
      // Page<Customer> customers = orderService.findAllOrdersByPage(1, 20, null, null, DateUtil.stringToDateFormat("2018-04-01"));
        //System.out.println("customers"+customers.getContent().toString());
       /* Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.parse("2018-04-10");

        Page<Customer> customers =orderService.findAllOrdersByPage(0,100,null,null,date);*/
        /*try {
            String pass = MD5Util.encodeMD5("f2FxmQZcBn");
            System.out.printf("-----pass:   "+pass+"   -----");
           Customer customer = customerService.loginCustomer("M000017000", pass);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        //获取code

       /* Customer customer = new Customer();
        customer.setCustomerNo("M000015000");
        customer.setPhoneNumber("17621396727");
        Map map = customerService.getCodeSms(customer);
        System.out.printf("map:"+map.toString());
        customer.setCode(map.get("code").toString());
      customerService.resetCustomerPwd(customer);*/
       // urlDecipheringService.sendSms("O2018049903142700A2YPV4");
//        Customer customer = customerDao.findOne(20L);
        //urlDecipheringService.sendSmsPwd(customer);
        //Map map = exchangeLogService.getExchangeLogProfile();
       /* Customer customer = new Customer();
        customer.setCustomerNo("M000015000");
        String pass = MD5Util.encodeMD5("qwertyuiop");
        customer.setPassword(pass);
        customerService.updateCustomerPwd(customer);*/
//        System.out.println(map.toString());
        System.out.println("11");

    }

    @Test
    public void test() {
//        Gson gson = new Gson();
//        String apiUrl = WalletConstants.QBAO_PAY_RATE_URL;
//        String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, apiUrl, null, null, String.class);
//        Map<String, LinkedTreeMap> map = gson.fromJson(responseBody, Map.class);
//        LinkedTreeMap<String,List> mapData = map.get("data");
//        List mapBids = mapData.get("bids");
//        List mapAsks =  mapData.get("asks");
//        //获取bids的price
//        String priceBids = ((LinkedTreeMap) mapBids.get(0)).get("price").toString();
//        //获取asks的price
//        String priceAsks = ((LinkedTreeMap) mapAsks.get(0)).get("price").toString();
//
//        //转换成bigdecimal
//        BigDecimal rate  = new BigDecimal(priceBids).add(new BigDecimal(priceAsks)).divide(new BigDecimal(2));
        System.out.println("11");
    }

    /**
     * Q用户概况
     */
    @Test
    public void getAccountsProfile() {

//        Account accounts = accountService.getAccountsProfile();
//
//        System.out.println(accounts.toString());
        System.out.println("1111");

    }

    /**
     * 报表——充值余额概况
     */
    @Test
    public void getRechargeMoneyProfile() {

        // Map map = accountBalanceService.getRechargeMoneyProfile();

//        System.out.println(map.toString());

        System.out.println("11111");
    }

    @Test
    public void getExchangeQBETest() {
        System.out.println("1111");
        //Contract contract = contractService.findContractByName(WalletConstants.QBAO_ENERGY);
        //accountBalanceService.getExchangeQBE("882273",contract.getId());
    }

    @Test
    public void getExchangeQBE() throws ParseException {

        System.out.println("121");
//
//      Map guessRecords = guessNumberGameService.getWinnerList(1,2,66L,"268092");
//        System.out.println(guessRecords.toString());
//        accountBalanceService.getExchangeQBE("268092",20L);
//       org.springframework.data.domain.Page<Games> gamesPage = gamesService.findActivatedGame(0,10);
//        System.out.println(gamesPage);

/*
        GuessNumberGame guessNumberGame = new GuessNumberGame();
        guessNumberGame.setGameEndTime(sdf.parse("2018-02-02 00:00:00"));
        guessNumberGame.setGameStartTime(sdf.parse("2018-02-01 00:00:00"));
        guessNumberGame.setZhName("888");
        guessNumberGame.setUnit(44L);
        guessNumberGame.setTotalAmount(new BigDecimal(100));
        guessNumberGame.setGameId(33L);
        guessNumberGameService.saveGuessNumberGame(guessNumberGame);*/

      /*  GuessRecord guessRecord = new GuessRecord();
        guessRecord.setGuessNumberId(66L);
        guessRecord.setAccountNo("208004");
        guessRecord.setDrawNumber("54367");
        guessNumberGameService.guessNumberByAccount(guessRecord);

        GuessRecord guessRecord1 = new GuessRecord();
        guessRecord1.setGuessNumberId(66L);
        guessRecord1.setAccountNo("894696");
        guessRecord1.setDrawNumber("02345");
        guessNumberGameService.guessNumberByAccount(guessRecord1);

        GuessRecord guessRecord2 = new GuessRecord();
        guessRecord2.setGuessNumberId(66L);
        guessRecord2.setAccountNo("468020");
        guessRecord2.setDrawNumber("12456");
        guessNumberGameService.guessNumberByAccount(guessRecord2);

        GuessRecord guessRecord3 = new GuessRecord();
        guessRecord3.setGuessNumberId(66L);
        guessRecord3.setAccountNo("268092");
        guessRecord3.setDrawNumber("12345");
        guessNumberGameService.guessNumberByAccount(guessRecord3);*/
//        guessNumberGameService.deleteGuessNumberGame(66L);
//        guessRecordDao.delete(guessRecord.getId());

      /*  GuessRecord guessRecord1 = new GuessRecord();
        guessRecord1.setGuessNumberId(59L);
        guessRecord1.setAccountNo("468020");
        guessRecord1.setDrawNumber("12045");
        guessNumberGameService.guessNumberByAccount(guessRecord1);

        GuessRecord guessRecord2 = new GuessRecord();
        guessRecord2.setGuessNumberId(59L);
        guessRecord2.setAccountNo("894696");
        guessRecord2.setDrawNumber("12099");
        guessNumberGameService.guessNumberByAccount(guessRecord2);*/


    }

}
