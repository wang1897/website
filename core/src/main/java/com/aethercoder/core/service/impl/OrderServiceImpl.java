package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.MD5Util;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.json.OrderClose;
import com.aethercoder.core.entity.json.OrderCloseTitle;
import com.aethercoder.core.entity.json.OrderCloseTotal;
import com.aethercoder.core.entity.pay.ContractCurrencyPrice;
import com.aethercoder.core.entity.pay.CustomerBalance;
import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.*;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import io.rong.RongCloud;
import io.rong.messages.QbaoPayMessage;
import io.rong.models.CodeSuccessResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @auther Guo Feiyan
 * @date 2018/3/20 下午4:49
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ContractService contractService;

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private AccountBalanceService accountBalanceService;
    @Autowired
    private CustomerBalanceService customerBalanceService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LocaleMessageService localeMessageUtil;
    @Autowired
    private URLDecipheringService urlDecipheringService;

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private ContractCurrencyPriceDao contractCurrencyPriceDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CountryInformationService countryInformationService;

    @Autowired
    private CountryInformationDao contactInformationDao;

    private String customerTable = "t_customer";

    private String customerNameField = "customer_name";


    @Override
    public Map findOrdersByCustomer(Integer page, Integer size, String customerId, String cleanTime, String beginDate, String endDate, Integer status, String orderId) {
        Map map = new HashMap();
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Order> orders = orderDao.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (StringUtils.isNotBlank(cleanTime)) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderTime").as(String.class), cleanTime + " 00:00:00"));
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderTime").as(String.class), cleanTime + " 23:59:59"));
                } else {
                    if (StringUtils.isNotBlank(beginDate)) {
                        //大于或等于传入时间
                        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderTime").as(String.class), beginDate));
                    }
                    if (StringUtils.isNotBlank(endDate)) {
                        //小于或等于传入时间
                        list.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderTime").as(String.class), endDate));
                    }
                }
                if (StringUtils.isNotBlank(customerId)) {
                    list.add(criteriaBuilder.equal(root.get("customerId").as(String.class), customerId));
                }

                if (null != status) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                } else {
                    list.add(criteriaBuilder.greaterThan(root.get("status").as(Integer.class), 0));
                }
                if (StringUtils.isNotBlank(orderId)) {
                    list.add(criteriaBuilder.equal(root.get("orderId").as(String.class), orderId));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        map = search(customerId, cleanTime, beginDate, endDate, status, orderId);
        orders.getContent().forEach(order -> {
            Account account = accountDao.findByAccountNo(order.getAccountNo());
            if (account != null) {
                order.setAccountName(account.getAccountName());
            }

        });
        map.put("orders", orders);
        return map;
    }

    @PersistenceContext
    private EntityManager em;

    //    @SuppressWarnings("unchecked")
    public Map search(String customerId, String cleanTime, String beginDate, String endDate, Integer status, String orderId) {
        Map map = new HashMap();
        String sumAmountSql = "SELECT sum(o.amount) FROM Order o where 1=1 ";
        String countAllSql = "SELECT count(*) FROM Order o where 1 = 1 ";
        String sumFeeAmountSql = "SELECT sum(o.fee) FROM Order o where 1=1 ";

        if (StringUtils.isNotEmpty(customerId)) {
            sumAmountSql += " and o.customerId = ?1 ";
            countAllSql += " and o.customerId = ?1 ";
            sumFeeAmountSql += " and o.customerId = ?1 ";
        }
        if (StringUtils.isNotEmpty(cleanTime)) {
            sumAmountSql += " and o.orderTime >= ?2 and o.orderTime <= ?3 ";
            countAllSql += " and o.orderTime >= ?2 and o.orderTime <= ?3";
            sumFeeAmountSql += " and o.orderTime >= ?2 and o.orderTime <= ?3 ";
        }
        if (StringUtils.isNotEmpty(beginDate)) {
            sumAmountSql += " and o.orderTime >= ?4";
            countAllSql += " and o.orderTime >= ?4 ";
            sumFeeAmountSql += "and o.orderTime >= ?4 ";
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sumAmountSql += " and o.orderTime <= ?5 ";
            countAllSql += " and o.orderTime <= ?5 ";
            sumFeeAmountSql += " and o.orderTime <= ?5 ";
        }
        if (status != null) {
            sumAmountSql += " and o.status = ?6 ";
            countAllSql += " and o.status = ?6 ";
            sumFeeAmountSql += " and o.status = ?6";
        } else {
            sumAmountSql += " and o.status = 1 ";
            countAllSql += " and o.status = 1 ";
            sumFeeAmountSql += " and o.status = 1";
        }
        if (StringUtils.isNotEmpty(orderId)) {
            sumAmountSql += " and o.orderId = ?7 ";
            countAllSql += " and o.orderId = ?7 ";
            sumFeeAmountSql += " and o.orderId = ?7 ";
        }

        Query sumAmountQuery = em.createQuery(sumAmountSql);
        Query countAllQuery = em.createQuery(countAllSql);
        Query sumFeeAmountQuery = em.createQuery(sumFeeAmountSql);

        if (StringUtils.isNotEmpty(customerId)) {
            sumAmountQuery.setParameter(1, customerId);
            countAllQuery.setParameter(1, customerId);
            sumFeeAmountQuery.setParameter(1, customerId);
        }
        if (StringUtils.isNotEmpty(cleanTime)) {
            Date beginTime = DateUtil.stringToDate(cleanTime + " 00:00:00");
            Date endTime = DateUtil.stringToDate(cleanTime + " 23:59:59");
            sumAmountQuery.setParameter(2, beginTime);
            countAllQuery.setParameter(2, beginTime);
            sumFeeAmountQuery.setParameter(2, beginTime);
            sumAmountQuery.setParameter(3, endTime);
            countAllQuery.setParameter(3, endTime);
            sumFeeAmountQuery.setParameter(3, endTime);
        }
        if (StringUtils.isNotEmpty(beginDate)) {
            Date beginTime = DateUtil.stringToDate(beginDate);
            sumAmountQuery.setParameter(4, beginTime);
            countAllQuery.setParameter(4, beginTime);
            sumFeeAmountQuery.setParameter(4, beginTime);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            Date endTime = DateUtil.stringToDate(endDate);
            sumAmountQuery.setParameter(5, endTime);
            countAllQuery.setParameter(5, endTime);
            sumFeeAmountQuery.setParameter(5, endTime);
        }
        if (status != null) {
            sumAmountQuery.setParameter(6, status);
            countAllQuery.setParameter(6, status);
            sumFeeAmountQuery.setParameter(6, status);
        }
        if (StringUtils.isNotEmpty(orderId)) {
            sumAmountQuery.setParameter(7, orderId);
            countAllQuery.setParameter(7, orderId);
            sumFeeAmountQuery.setParameter(7, orderId);
        }
        long count = (long) countAllQuery.getSingleResult();
        BigDecimal totalAmount = (BigDecimal) sumAmountQuery.getSingleResult();
        BigDecimal feeAmount = (BigDecimal) sumFeeAmountQuery.getSingleResult();
        map.put("count", count);
        map.put("totalAmount", totalAmount == null ? new BigDecimal(0) : totalAmount);
        map.put("feeAmount", feeAmount == null ? new BigDecimal(0) : feeAmount);
        return map;
//        Page<User> page = new Page();
//        page.setTotalSize(totalSize);
//        List<User> data = dataQuery.getResultList();
//        page.setData(data);
//        return page;
    }


    @Override
    public Page<Customer> findAllOrdersByPage(Integer page, Integer size, String customerNo, String customerName, Date cleanTime) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
//            customers = customerDao.findActivited(customerId + "%", customerName + "%", pageable);
        Page<Customer> customers = customerDao.findAll(new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                //带查询条件的分页in的用法
                List<Object> ids = customerDao.findCustomerIds(cleanTime, WalletConstants.CUSTOMER_STATUS_UNACTIVE);
                if (ids != null && ids.size() > 0) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
                    for (Object id : ids) {
                        in.value(id);
                    }
                    list.add(in.not());
                }

                if (StringUtils.isNotBlank(customerNo)) {
                    list.add(criteriaBuilder.equal(root.get("customerNo").as(String.class), customerNo));
                }

//                if (null != customerName && !"".equals(customerName)) {
//                    list.add(criteriaBuilder.like(root.get("customerName").as(String.class), customerName + "%"));
//                }
                if (StringUtils.isNotBlank(customerName)) {
                    String language = LocaleContextHolder.getLocale().getLanguage();
                    List<Object> idist = localeMessageService.getMessagesByTableField(customerTable, customerNameField, language, "%" + customerName + "%");
                    if (idist != null && idist.size() > 0) {
                        CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
                        for (Object s : idist) {
                            in.value(s);
                        }
                        list.add(in);
                    } else {
                        list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), 0));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        for (Customer customer : customers) {
            CountryInformation countryInformation = contactInformationDao.findOne(customer.getCountryInfoId());
            customer.setAreaCode(countryInformation.getTelNumber());
        }
        return customers;
    }


    @Override
    public Map getOrdersDay(String customerId, String cleanTime) {
        Customer customer = customerService.findCustomerByCustomerNo(customerId);
        countryInformationService.setLocale(customer.getCountryInfoId());

        String templateCode = null;
        Map map = new HashMap();
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalTokenAmount = new BigDecimal(0);
        //title
        String language = localeMessageService.getLanguage();
        OrderCloseTitle orderClose = new OrderCloseTitle(language);

        //data
        //获取商户信息
        templateCode = localeMessageUtil.getLocalMessage("PAY_TYPE");
        List<OrderClose> orderCloseList = new ArrayList<>();
        Date beginTime = DateUtil.stringToDate(cleanTime + " 00:00:00");
        Date endTime = DateUtil.stringToDate(cleanTime + " 23:59:59");
        List<Order> orderList = orderDao.findOrdersByCustomerId(beginTime, endTime, customerId);
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);

            OrderClose orderCloseData = new OrderClose();
            orderCloseData.setOrderNo(order.getOrderId());
            orderCloseData.setCustomerNo(order.getCustomerId());
            orderCloseData.setCleanDate(cleanTime);
            orderCloseData.setOrderDate(order.getOrderTime());
            orderCloseData.setAccountNo(order.getAccountNo());
            orderCloseData.setType(order.getType() == 0 ? templateCode : "");
            Contract tokenUnit = contractDao.findOne(order.getTokenUnit());
//            orderCloseData.setTokenUnit(tokenUnit == null ? "" : tokenUnit.getName());
//            orderCloseData.setUnit(order.getUnit());
            orderCloseData.setUnit(tokenUnit == null ? "" : tokenUnit.getName());
            orderCloseData.setAmount(order.getAmount());
            orderCloseData.setTokenUnit(order.getUnit());
            orderCloseData.setTokenAmount(order.getTokenAmount());
            orderCloseList.add(orderCloseData);
            totalAmount = totalAmount.add(order.getAmount());
            totalTokenAmount = totalTokenAmount.add(order.getTokenAmount());
        }
        //totalAmount
        OrderCloseTotal orderCloseTotal = new OrderCloseTotal(language);
        orderCloseTotal.setAmount(totalAmount);
        orderCloseTotal.setTokenAmount(totalTokenAmount);
        orderCloseTotal.setNumber(orderCloseList.size());

        map.put("orderTitle", orderClose);
        map.put("orderList", orderCloseList);
        map.put("orderTotal", orderCloseTotal);
        return map;
    }

    @Override
    public Order preCreateOrder(Customer customer, String accountNo) {
        Order order = new Order();
        if (validationCustomer(customer)) {
            Customer customerDB = customerDao.findByCustomerNo(customer.getCustomerNo());
            // 生成支付token
            String tokenaccountNo = StringUtils.leftPad(accountNo, 8, '0');

            String[] uuids = customerDB.getCustomerUuid().split("-");
            String token = "";
            for (int i = 0; i < uuids.length; i++) {
                token += uuids[i] + tokenaccountNo.substring(i, i + 2);
            }

            try {
                token = MD5Util.YiMaMD5(WalletConstants.ORDER_TOKEN_KEY, token);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            String sign = customerDB.getCustomerNo() + token;

            order.setToken(token);

            order.setAccountNo(accountNo);
            order.setCustomerId(customerDB.getCustomerNo());
            order.setCustomerName(customerDB.getCustomerName());
            order.setCustomerLogo(customerDB.getLogo());

            logger.info("----------------------------------汇率start------------------------------------------------------");

            List<CurrencyRate> currencyRateList = currencyRateService.getCurrencyRateList();

            BigDecimal cnyRatePrice = null;
            BigDecimal krwRatePrice = null;
            for (int i = 0; i < currencyRateList.size(); i++) {
                CurrencyRate currencyRate = currencyRateList.get(i);
                if (currencyRate.getCurrency().equals(WalletConstants.CURRENCY_CNY)) {
                    cnyRatePrice = currencyRate.getRate();
                } else if (currencyRate.getCurrency().equals(WalletConstants.CURRENCY_KRW)) {
                    krwRatePrice = currencyRate.getRate();
                }
                if (cnyRatePrice != null && krwRatePrice != null) {
                    break;
                }
            }
            //获取最新QTUM／KRW 汇率
            Contract contract = contractDao.findContractByNameAndIsDeleteIsFalse(WalletConstants.QTUM_TOKEN_NAME);
            Long qtumId = contract.getId();
            ContractCurrencyPrice contractCurrencyPrice = contractCurrencyPriceDao.getFirstByContractAndCurrencyOrderByIdDesc(qtumId, WalletConstants.CURRENCY_KRW);

            Date nowDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowDate);
            cal.add(Calendar.MINUTE, -3);
            if (contractCurrencyPrice.getCreateTime().compareTo(cal.getTime()) < 0) {
                throw new AppException(ErrorCode.PAY_RATE_NOT_GET);
            }
            //对应韩元汇率
            BigDecimal krwRate = getQbaoPayRate(contractCurrencyPrice.getRate());
            //对应美元汇率
            BigDecimal usdRate = krwRate.divide(krwRatePrice, 6, BigDecimal.ROUND_FLOOR);
            //对应人民币汇率
            BigDecimal rmbRate = usdRate.divide(cnyRatePrice, 6, BigDecimal.ROUND_FLOOR);

            logger.info("----------------------------------汇率end------------------------------------------------------");

            // 同时给当前Qtum汇率默认;
//            Contract qtumContract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);

            // 获取汇率
            List<Object> rateTokenList = new ArrayList<>();
            Map<String, String> rateMap = new HashMap<>();
            rateMap.put(WalletConstants.CURRENCY_KRW, krwRate.toString());
            rateMap.put(WalletConstants.CURRENCY_CNY, rmbRate.toString());
            rateMap.put(WalletConstants.CURRENCY_USD, usdRate.toString());
            rateMap.put("unit", qtumId.toString());
            rateTokenList.add(rateMap);
            order.setRateList(rateTokenList);

            order.setType(WalletConstants.ORDER_NATIVE_PAY);
            // 保存到redis的key就是商户号加token
            redisTemplate.opsForValue().set(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + sign, order);
            redisTemplate.expire(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + sign, 3, TimeUnit.MINUTES);

        }
        return order;
    }

    private BigDecimal getQbaoPayRate(BigDecimal rate) {
        SysConfig sysConfig = sysConfigService.findSysConfigByName(WalletConstants.QBAO_PAY_RATE_BONUS);
        BigDecimal feeRate = new BigDecimal(100-Integer.valueOf(sysConfig.getValue())).divide(new BigDecimal(100));
        return rate.multiply(feeRate).setScale(6,BigDecimal.ROUND_FLOOR);
    }

    private boolean validationCustomer(Customer customer) {
        boolean result = false;
        Customer customerDB = customerDao.findByCustomerNo(customer.getCustomerNo());

        if (customerDB == null) {
            // customerId 不对。
            throw new AppException(ErrorCode.INPUT_QRCODE_INVALID);
        } else if (!customerDB.getGenerateString().equals(customer.getGenerateString())) {
            throw new AppException(ErrorCode.INPUT_QRCODE_INVALID);
        } else if (WalletConstants.CUSTOMER_STATUS_UNACTIVE.equals(customerDB.getStatus())) {
            throw new AppException(ErrorCode.INPUT_QRCODE_INVALID);
        }

        result = true;
        return result;
    }

    @Override
    @Transactional
    public String payOrder(Order order) {
        if (validationOrder(order)) {
            Customer customerDB = customerService.findCustomerByCustomerNo(order.getCustomerId());
            if (WalletConstants.CUSTOMER_STATUS_UNACTIVE.equals(customerDB.getStatus())) {
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }
            // check token和商户号是否一致
            String sign = customerDB.getCustomerNo() + order.getToken();
            Object redisSign = redisTemplate.opsForValue().get(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + sign);
            if (redisSign == null) {
                throw new AppException(ErrorCode.ORDER_OVERTIME);
            }
            Order preOrder = (Order) redisSign;
            // check accountNo是不是之前的
            if (!order.getAccountNo().equals(preOrder.getAccountNo())) {
                // 扫码人和支付人不一致。
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }

            // 支付密碼不正确
            Account account = accountService.findAccountByAccountNo(preOrder.getAccountNo());
            if (StringUtils.isBlank(account.getWords()) || !account.getWords().equals(order.getNickWords())) {
                // 扫码人和支付人不一致。
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }

            // 金额小于0则报错
            if (new BigDecimal(0).compareTo(order.getAmount()) >= 0 || new BigDecimal(0).compareTo(order.getTokenAmount()) >= 0) {
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }
            // 金额超过上限
            SysConfig perLimit = sysConfigService.findSysConfigByName(WalletConstants.QBAO_PAY_LIMIT_PER);
            if (perLimit != null && new BigDecimal(perLimit.getValue()).compareTo(order.getAmount()) < 0) {
                throw new AppException(ErrorCode.QBAO_PAY_OVER_LIMIT_PER);
            }

            // 金额超过当日上限
            SysConfig dayLimit = sysConfigService.findSysConfigByName(WalletConstants.QBAO_PAY_LIMIT_DAY);
            Date beginTime = DateUtil.getTodayBegin();
            BigDecimal todayAmount = orderDao.sumTodayAmount(beginTime,WalletConstants.CONFIRMED);
            if (dayLimit != null && todayAmount!=null && new BigDecimal(dayLimit.getValue()).compareTo(todayAmount.add(order.getAmount())) < 0) {
                throw new AppException(ErrorCode.QBAO_PAY_OVER_LIMIT_DAY);
            }

            // 由于当前只支持KRW 和 Qtum
            if(!WalletConstants.CURRENCY_KRW.equals(order.getUnit())){
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }

            BigDecimal payRate = order.getPayRate();
            // 根据汇率计算金额和法币是否是匹配
            if (payRate==null || order.getAmount().divide(payRate, 6, BigDecimal.ROUND_HALF_UP).compareTo(order.getTokenAmount()) != 0) {
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }

            // 判断汇率是否是3分钟内的汇率
            Contract contract = contractDao.findContractByNameAndIsDeleteIsFalse(WalletConstants.QTUM_TOKEN_NAME);
            Long qtumId = contract.getId();
            Date nowDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowDate);
            cal.add(Calendar.MINUTE, -3);
            List<ContractCurrencyPrice> payPriceList = contractCurrencyPriceDao.getByContractAndCurrencyAndCreateTimeAfter(qtumId, WalletConstants.CURRENCY_KRW,cal.getTime());

            //对应韩元汇率
            Boolean hasRate = false;
            for(ContractCurrencyPrice rate : payPriceList) {
                BigDecimal krwRate = rate.getRate();
                if(payRate.compareTo(getQbaoPayRate(krwRate))==0){
                    hasRate = true;
                    break;
                }
            }
            if(!hasRate){
                throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
            }

            // check 用户余额是否足够
            AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(order.getAccountNo(), order.getTokenUnit());
            if (accountBalance == null || accountBalance.getAmount().compareTo(order.getTokenAmount()) < 0) {
                throw new AppException(ErrorCode.NO_ENOUGH_MONEY);
            }

            accountBalance.setAmount(accountBalance.getAmount().subtract(order.getTokenAmount()));
            accountBalanceService.saveAccountBalance(accountBalance);

//            order.setPayRate(order.getPayRate());
            order.setOrderTime(new Date());
            order.setOrderId(generateOrderId(order));
            order.setStatus(WalletConstants.UNCONFIRMED);
            order.setType(WalletConstants.ORDER_NATIVE_PAY);
            Order order1 = orderDao.save(order);
            // 保存到redis的key就是商户号加token
            redisTemplate.opsForValue().set(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + order1.getOrderId(), order1.getOrderId());
            // 添加1分钟后撤销
            Thread cancelOrderT = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("添加1分钟后撤销:" + order1.getOrderId());
                    cancelOrder(order1.getOrderId(), null);
                }
            });
            cancelOrderT.start();
            // 支付收到以後就要刪除token，避免重複提交
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    //add redis
                    redisTemplate.delete(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + sign);
                    operations.exec();
                    return null;

                }
            });

        }
        return order.getOrderId();
    }

    private String generateOrderId(Order order) {
        String shareCode = StringUtils.leftPad(accountService.getShareCode(order.getAccountNo()), 6, '0');
        // "O"订单前缀 1位 + 订单支付时间 14位 "YYYYMMDDHHmmss"
        String orderId = "O" + DateUtils.formatDate(order.getOrderTime(), "yyyyMMddHHmmss") + "00" + shareCode;

        return orderId;
    }

    @Override
    @Transactional
    public Boolean cancelOrder(String orderId, String accountNo) {
        // check如果由APP端发起的cancel是同一个accountNo
        logger.info("cancelOrder doing orderId:" + orderId);


        // 要注意并发，如果cancel和query确认同时执行，所以要加锁。
        String redisOrderId = (String) redisTemplate.opsForValue().get(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + orderId);
        if (redisOrderId == null) {
            logger.info("cancelOrder redis none:" + orderId);
            // 如果redis缓存的不存在了。等待五秒再看最后状态，如果交易还是未确认则关闭。
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 取消订单 退还红包ID
        Order order = orderDao.findOrderByOrderId(orderId);
        if (order == null || !WalletConstants.UNCONFIRMED.equals(order.getStatus())) {
            logger.info("cancelOrder end false:" + orderId);
            return false;
        }
        // 返回金额
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(order.getAccountNo(), order.getTokenUnit());
        accountBalance.setAmount(accountBalance.getAmount().add(order.getTokenAmount()));
        accountBalanceService.saveAccountBalance(accountBalance);

        // 获取 Customer信息
        Customer customer = customerDao.findByCustomerNo(order.getCustomerId());
        order.setCustomerName(customer.getCustomerName());
        order.setCustomerLogo(customer.getLogo());
        order.setCustomerDes(customer.getCustomerDes());

        // 更新 Order状态
        order.setStatus(WalletConstants.FAILED);
        order = orderDao.save(order);
        try {
            // 发送确认给客户端
            String[] toUserNoList = {order.getAccountNo()};
            order.setOrderTimestamp(order.getOrderTime().getTime());

            sendOrderMessage(toUserNoList, order.toString());

            // 发送短信给商户
            urlDecipheringService.sendSms(order.getOrderId());

            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    //add redis
                    redisTemplate.delete(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + orderId);
                    operations.exec();
                    return null;

                }
            });
        } catch (Exception ex) {
            logger.error("cancelOrder时通知用户或者商户失败：" + ex.getStackTrace());
        }

        logger.info("cancelOrder end true orderId:" + orderId);
        return true;
    }

    @Override
    @Transactional
    public Order queryOrder(String orderId, String accountNo) {

        // 获取该Order
        Order order = orderDao.findOrderByOrderId(orderId);
        if (order == null) {
            throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
        }

        // Order的account和query请求人一致
        if (!order.getAccountNo().equals(accountNo)) {
            // 非法查询
            throw new AppException(ErrorCode.INPUT_QPAY_INVALID);
        }

        // 获取 Customer信息
        Customer customer = customerDao.findByCustomerNo(order.getCustomerId());
        order.setCustomerName(customer.getCustomerName());
        order.setCustomerLogo(customer.getLogo());
        order.setCustomerDes(customer.getCustomerDes());
        order.setOrderTimestamp(order.getOrderTime().getTime());
        // Order的status状态
        if (WalletConstants.UNCONFIRMED.equals(order.getStatus())) {
            // redis中没有则报错
            String orderIdInRedis = (String) redisTemplate.opsForValue().get(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + orderId);
            if (orderIdInRedis == null) {
                order.setStatus(WalletConstants.FAILED);
            } else {
                // 成功之后 生成ExchangeLog
                ExchangeLog exchangeLog = new ExchangeLog();
                exchangeLog.setExchangeTime(order.getOrderTime());
                exchangeLog.setAccountNo(order.getAccountNo());
                exchangeLog.setOrderId(order.getOrderId());
                exchangeLog.setType(WalletConstants.NATIVE_PAY);
                exchangeLog.setStatus(WalletConstants.CONFIRMED);
                exchangeLog.setAmount(order.getTokenAmount());
                exchangeLog.setUnit(order.getTokenUnit());
                exchangeLog.setIsDeleted(false);
                ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);

                // customerBalance
                CustomerBalance customerBalance = new CustomerBalance();
                customerBalance.setCustomerId(order.getCustomerId());
                customerBalance.setAmount(order.getTokenAmount());
                customerBalance.setUnit(order.getTokenUnit());
                customerBalanceService.saveCustomerBalanceAmountAdd(customerBalance);

                // 更新 Order状态
                order.setStatus(WalletConstants.CONFIRMED);
                Order order1 = orderDao.save(order);

                // 发送短信
                Thread queryOrderT = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            logger.error("queryOrder时用户或者商户联系");
                            // 发送确认给客户端
                            String[] toUserNoList = {order1.getAccountNo()};

                            sendOrderMessage(toUserNoList, order1.toString());
                            // 发送短信给商户
                            urlDecipheringService.sendSms(order1.getOrderId());

                        } catch (Exception ex) {
                            logger.error("queryOrder时用户或者商户联系失败：" + ex.getStackTrace());
                            throw new AppException(ErrorCode.ORDER_CONTACT_FAIL);
                        }
                    }
                });
                queryOrderT.start();

                try {
                    redisTemplate.execute(new SessionCallback() {
                        @Override
                        public Object execute(RedisOperations operations)
                                throws DataAccessException {
                            operations.multi();
                            //add redis
                            redisTemplate.delete(CommonConstants.REDIS_KEY_NATIVE_PAY_ORDER + orderId);
                            operations.exec();
                            return null;

                        }
                    });

                } catch (Exception ex) {
                    logger.error("redis删除失败：" + ex.getStackTrace());
                    throw new AppException(ErrorCode.ORDER_CONTACT_FAIL);
                }
            }

        }
        logger.info("扫码支付结束");
        return order;
    }

    @Override
    public Boolean queryUser(String accountNo) {
        Boolean result = true;
        Account account = accountService.findAccountByAccountNo(accountNo);
        if (StringUtils.isNotBlank(account.getWords())) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void setNickWords(String accountNo, String words) {
        Account account = accountService.findAccountByAccountNo(accountNo);
        if (StringUtils.isNotBlank(account.getWords())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        } else {
            account.setWords(words);
            accountDao.save(account);
        }
    }

    @Override
    public Page<Order> getOrderListByPage(Integer page, Integer size, String startTime, String endTime, String accountNo, String accountName,
                                          String customerId, String tokenUnit, String status, String orderId, String type) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Order> orders = orderDao.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                List<Predicate> listOr = new ArrayList<Predicate>();
                if (startTime != null) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderTime").as(String.class), startTime));
                }

                if (endTime != null) {
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderTime").as(String.class), endTime));
                }

                if (StringUtils.isNotBlank(accountNo)) {
                    list.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo));
                }

                if (StringUtils.isNotBlank(accountName)) {
                    List<Account> accounts = accountDao.findAccountsByAccountNameLike(accountName + "%");
                    for (Account account : accounts) {
                        String accountNo1 = account.getAccountNo();
                        listOr.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo1));
                    }
                }

                if (StringUtils.isNotBlank(customerId)) {
                    list.add(criteriaBuilder.equal(root.get("customerId").as(String.class), customerId));
                }

                if (StringUtils.isNotBlank(tokenUnit)) {
                    list.add(criteriaBuilder.equal(root.get("tokenUnit").as(String.class), tokenUnit));
                }

                if (StringUtils.isNotBlank(orderId)) {
                    list.add(criteriaBuilder.equal(root.get("orderId").as(String.class), orderId));
                }

                if (StringUtils.isNotBlank(status)) {
                    list.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
                }
                if (StringUtils.isNotBlank(type)) {
                    list.add(criteriaBuilder.equal(root.get("type").as(String.class), type));
                }
                Predicate[] p = new Predicate[list.size()];
                if (listOr.size() > 0) {
                    Predicate[] pOr = new Predicate[listOr.size()];
                    return criteriaBuilder.and(criteriaBuilder.and(list.toArray(p)), criteriaBuilder.or(listOr.toArray(pOr)));
                } else {
                    return criteriaBuilder.and(list.toArray(p));
                }
            }
        }, pageable);
        for (Order order : orders) {
            Account account = accountService.findAccountByAccountNo(order.getAccountNo());
            order.setAccountName(account.getAccountName());
            Customer customer = customerService.findCustomerByCustomerNo(order.getCustomerId());
            order.setCustomerName(customer.getCustomerName());
            Contract contract = contractService.findContractById(order.getTokenUnit());
            order.setTokenName(contract.getName());
            order.setTypeName(WalletConstants.PAY_TYPE);
        }
        return orders;
    }

    private boolean validationOrder(Order order) {
        // TODO 验证
        boolean result = false;


        result = true;
        return result;
    }

    // 发送扫码支付消息
    private void sendOrderMessage(String[] toUserNoList, String extra) {
        Locale locale = accountService.getLocaleByAccount(toUserNoList[0]);
        String pushContent = localeMessageUtil.getLocalMessage("ORDER_PAY_MESSAGE", locale, null);

        QbaoPayMessage txtMessage = new QbaoPayMessage(extra);
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishPrivate(WalletConstants.PAY_ASSISTANT,
                    toUserNoList,
                    txtMessage,
                    pushContent,
                    null,
                    null,
                    null, null, null, null);
            logger.info("----sendOrderMessage :" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("---sendOrderMessage error----", e);
            throw new RuntimeException("sendOrderMessage error:" + e.getMessage());
        }
    }

}
