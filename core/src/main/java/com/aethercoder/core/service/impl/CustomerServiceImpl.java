package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.MD5Util;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.CountryInformationDao;
import com.aethercoder.core.dao.CustomerDao;
import com.aethercoder.core.entity.wallet.CountryInformation;
import com.aethercoder.core.entity.wallet.Customer;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.core.service.URLDecipheringService;
import com.aethercoder.core.util.BarcodeUtil;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.dao.MessageDao;
import com.aethercoder.foundation.entity.i18n.Message;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private String customerTable = "t_customer";
    private String addressField = "address";
    private String customerDesField = "customer_des";
    private String customerNameField = "customer_name";
    private String countryInformationTable = "country_information";
    private String countryNameField = "country_name";
    private Integer qrCodeWidth = 260;
    private Integer qrCodeHeight = 260;

    @Value("${upload.iconPath}")
    private String iconPath;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CountryInformationDao countryInformationDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    private CountryInformationDao contactInformationDao;

    @Autowired
    private URLDecipheringService urlDecipheringService;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<Customer> getCustomerList(Integer page, Integer size, String customerNo, String customerName, Integer status, String language) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Customer> customers = customerDao.findAll(new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != customerNo && !"".equals(customerNo)) {
                    list.add(criteriaBuilder.equal(root.get("customerNo").as(String.class), customerNo));
                }
//                if (null != customerName && !"".equals(customerName)) {
//                    list.add(criteriaBuilder.like(root.get("customerName").as(String.class),  customerName + "%"));
//                }
                if (StringUtils.isNotBlank(customerName)) {
                    List<Object> idList = localeMessageService.getMessagesByTableField(customerTable, customerNameField, language, "%" + customerName + "%");
                    if (idList != null && idList.size() > 0) {
                        CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id").as(Long.class));
                        for (Object s : idList) {
                            in.value(s);
                        }
                        list.add(in);
                    } else {
                        list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), 0));
                    }
                }
                if (null != status) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        customers.getContent().forEach(customer -> translateCustomer(customer, language, CommonConstants.I18N_SHOW_DEFAULT));
        return customers;
    }

    private void translateCustomer(Customer customer, String language, int type) {
        String address = localeMessageService.getMessageByTableFieldId(customerTable, addressField, customer.getId() + "", type, language);
        customer.setAddress(address);

        String customerDes = localeMessageService.getMessageByTableFieldId(customerTable, customerDesField, customer.getId() + "", type, language);
        customer.setCustomerDes(customerDes);

        String customerName = localeMessageService.getMessageByTableFieldId(customerTable, customerNameField, customer.getId() + "", type, language);
        customer.setCustomerName(customerName);

        CountryInformation countryInformation = contactInformationDao.findOne(customer.getCountryInfoId());
        if (countryInformation == null) {
            customer.setCountry(WalletConstants.LANGUAGE_TYPE_ZH);
            customer.setCountryName(WalletConstants.LANGUAGE_TYPE_ZH_NAME);
            customer.setAreaCode(WalletConstants.DEFAULT_NUMBER);
        } else {
            customer.setCountry(countryInformation.getCountry());
            String countryName = localeMessageService.getMessageByTableFieldId(countryInformationTable, countryNameField, countryInformation.getId() + "", type, language);
            customer.setCountryName(countryName);
            customer.setAreaCode(countryInformation.getTelNumber());
        }
    }

    @Override
    public synchronized void saveCustomer(Customer customer) {

        if (customer.getPhoneNumber() != null && customer.getPhoneNumber().length() > 20) {
            throw new AppException(ErrorCode.PHONE_NUMBER_TOO_LONG);
        }
        //生成商户编号
        String customerNo = "";
        Customer customerMax = customerDao.getFirstByOrderByCreateTimeDesc();
        if (customerMax == null) {
            customer.setCustomerNo("M000001000");
        } else {
            String oldCustomerNo = customerMax.getCustomerNo();
            String substringEnd = oldCustomerNo.substring(1, 7);
            Integer customerNoInt = Integer.parseInt(substringEnd) + 1;
            substringEnd = NumberUtil.formatNumberToString(customerNoInt, "000000");
            customerNo = "M" + substringEnd + "000";
            customer.setCustomerNo(customerNo);
        }
        //生成uuid
        String uuid = UUID.randomUUID().toString();
        customer.setCustomerUuid(uuid);
        System.out.println(uuid);
        //生成私钥（随机字符串，16位）
        String randomString = randomString(16);
        customer.setGenerateString(randomString);
        //默认状态
        customer.setStatus(WalletConstants.CUSTOMER_STATUS_ACTIVE);
        //调生成二维码的方法 传入商户地址，宽，高，logo地址，二维码地址，是否补白。
        //生成商户的地址 默认宽高为350
        String customerPath = "qbao://qbaopay/nativelink?" + "customer_no=" + customerNo + "&nonce_str=" + randomString;
        //生成logo地址，默认补白
        String logoPath = iconPath + File.separator + customer.getLogo();
        //生成二维码的地址
        String qrName = customerNo + "qrCode.png";
        String qrCodePath = iconPath + File.separator + qrName;

        //返回二维码图片的绝对路径
        BarcodeUtil.encode(customerPath, qrCodeWidth, qrCodeHeight, logoPath, qrCodePath, true);
        customer.setQrCodePic(qrName);
        customer.setQrCodeUrl(customerPath);
        //生成密码
        String str = randomString(10);
        System.out.println("这是商户密码哈哈哈哈哈哈哈哈哈：" + str);
        String password = "";
        try {
            password = MD5Util.encodeMD5(str);
            password = MD5Util.myMD5(password);
        } catch (Exception e) {
            System.out.println(e);
        }
        //String password = myMD5WithSalt(myMD5Password);
        customer.setPassword(password);
        customer.setFirstLogin(true);
        customerDao.save(customer);
        //向客服发送商户密码
        urlDecipheringService.sendSmsPwd(customerNo, str);
        //添加多语言
        saveCustomerMessage(customer);
    }

    private void saveCustomerMessage(Customer customer) {
        if (customer.getAddress() != null) {
            Message addressMsg = new Message();
            addressMsg.setMessage(customer.getAddress());
            addressMsg.setLanguage(customer.getLanguage());
            addressMsg.setTable(customerTable);
            addressMsg.setField(addressField);
            addressMsg.setResourceId(customer.getId() + "");
            localeMessageService.saveMessage(addressMsg);
        }

        if (customer.getCustomerDes() != null) {
            Message customerDesnMsg = new Message();
            customerDesnMsg.setMessage(customer.getCustomerDes());
            customerDesnMsg.setLanguage(customer.getLanguage());
            customerDesnMsg.setTable(customerTable);
            customerDesnMsg.setField(customerDesField);
            customerDesnMsg.setResourceId(customer.getId() + "");
            localeMessageService.saveMessage(customerDesnMsg);
        }

        if (customer.getCustomerName() != null) {
            Message CustomerNameMsg = new Message();
            CustomerNameMsg.setMessage(customer.getCustomerName());
            CustomerNameMsg.setLanguage(customer.getLanguage());
            CustomerNameMsg.setTable(customerTable);
            CustomerNameMsg.setField(customerNameField);
            CustomerNameMsg.setResourceId(customer.getId() + "");
            localeMessageService.saveMessage(CustomerNameMsg);
        }
    }

    @Override
    public void cancelCustomer(String customerNo) {
        Customer customer = customerDao.findByCustomerNoAndStatus(customerNo, WalletConstants.CUSTOMER_STATUS_ACTIVE);
        if (customer == null) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        customer.setStatus(WalletConstants.CUSTOMER_STATUS_UNACTIVE);
        customer.setCancellationTime(new Date());
        customerDao.save(customer);
    }

    private String randomString(int length) {
        String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getCustomerNo() == null) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        Customer targetCustomer = customerDao.findByCustomerNo(customer.getCustomerNo());
        if (targetCustomer == null) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        com.aethercoder.basic.utils.BeanUtils.copyPropertiesWithoutNull(customer, targetCustomer);
        customerDao.save(targetCustomer);
        //更新i18n
        saveCustomerMessage(customer);

    }

    @Override
    public String queryCustomerQrCode(String customerNo) {

        String qrCodeUrl = "qbao://qbaopay/nativelink?";
        Customer customer = customerDao.findByCustomerNo(customerNo);
        if (StringUtils.isNotBlank(customer.getQrCodeUrl())) {
            qrCodeUrl = customer.getQrCodeUrl();
        } else {
            qrCodeUrl = qrCodeUrl + "customer_no=" + customer.getCustomerNo() + "&nonce_str=" + customer.getGenerateString();
            customer.setQrCodeUrl(qrCodeUrl);
            customerDao.save(customer);
        }
        return qrCodeUrl;
    }

//    @Override
//    public String getCustomerQrCode(String customerNo) {
//
//        Customer customer = customerDao.findByCustomerNo(customerNo);
//        String qrCodeUrlName = customer.getQrCodeUrl();
//
//        return customer.getQrCodeUrl();
//    }

    @Override
    public Customer findCustomerByCustomerNo(String customerNo) {
        Customer customer = customerDao.findByCustomerNo(customerNo);
        if (customer == null) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        return customer;
    }

    @Override
    @Transactional
    public Customer updateCustomerPwd(Customer customer) {
        Customer customerUpdate = findCustomerByCustomerNo(customer.getCustomerNo());
        if (customerUpdate.getStatus().equals(WalletConstants.CUSTOMER_STATUS_UNACTIVE)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        if (customerUpdate.getFirstLogin()) {
            //生成密码
            customerUpdate.setPassword(MD5Util.myMD5(customer.getPassword()));
            customerUpdate.setFirstLogin(false);
            customerDao.save(customerUpdate);
        }else {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        return customerUpdate;

    }

    @Override
    @Transactional
    public Customer resetCustomerPwd(Customer customer) {
        logger.info("resetCustomerPwd: CustomerNo："+customer.getCustomerNo()+" PhoneNumber："+customer.getPhoneNumber()+" Code："+customer.getCode());
        String customerNo = customer.getCustomerNo();
        Customer customerUpdate = findCustomerByCustomerNo(customer.getCustomerNo());
        if (customerUpdate.getStatus().equals(WalletConstants.CUSTOMER_STATUS_UNACTIVE)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        //定义语言地区信息
        CountryInformation countryInformation = countryInformationDao.findOne(customerUpdate.getCountryInfoId());
        //商户绑定手机号
        String customerPhone = countryInformation.getTelNumber() + customerUpdate.getPhoneNumber();
        if (!customer.getPhoneNumber().equals(customerPhone)) {
            throw new AppException(ErrorCode.CUSTOMER_LOGIN_RESET_PWD_ERROR_PHONE);
        }
        //check code
        Set codeCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE + customerNo);
        if (codeCustomer == null || codeCustomer.size() == 0 || !customer.getCode().equals(codeCustomer.toArray()[0])) {
            throw new AppException(ErrorCode.CUSTOMER_LOGIN_RESET_PWD_ERROR_CODE);
        }
        //RESET PWD
        //生成密码
        String str = randomString(10);
        String password = "";
        try {
            password = MD5Util.encodeMD5(str);
            password = MD5Util.myMD5(password);
        } catch (Exception e) {
            logger.error("resetCustomerPwd_error:" + e);
        }
        //String password = myMD5WithSalt(myMD5Password);
        customerUpdate.setPassword(password);
        customerUpdate.setFirstLogin(true);
        urlDecipheringService.sendSmsResetPwd(customer.getCustomerNo(), str);
        customerDao.save(customerUpdate);
        //判断该用户是否锁定
        Set lockCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo);
        //已经锁定用户 24小时内不能登陆
        if (lockCustomer != null && lockCustomer.size() != 0) {
            logger.info("resetCustomerPwd: lockCustomerBefore:"+lockCustomer);
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    redisTemplate.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo);
                    redisTemplate.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN + customerNo);
                    operations.exec();
                    return null;
                }
            });

        }
        logger.info("resetCustomerPwd: lockCustomerAfter:" + redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo));
        return customerUpdate;
    }

    @Override
    @Transactional
    public Map getCodeSms(Customer customer) {
        Map map = new HashMap();
        String customerNo = customer.getCustomerNo();
        Customer customerUpdate = findCustomerByCustomerNo(customerNo);
        if (customerUpdate.getStatus().equals(WalletConstants.CUSTOMER_STATUS_UNACTIVE)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        //定义语言地区信息
        CountryInformation countryInformation = countryInformationDao.findOne(customerUpdate.getCountryInfoId());
        //商户绑定手机号
        String customerPhone = countryInformation.getTelNumber() + customerUpdate.getPhoneNumber();
        if (!customer.getPhoneNumber().equals(customerPhone)) {
            throw new AppException(ErrorCode.CUSTOMER_LOGIN_RESET_PWD_ERROR_PHONE);
        }
        //生成验证码
//        String codeStr = "";
//        Set codeCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE + customerNo);
      /*  if (codeCustomer != null && codeCustomer.size() != 0) {
            codeStr = codeCustomer.toArray()[0].toString();
        } else {*/
            //随机6位数字
            String codeStr = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
//        }
//        String finalCodeStr = codeStr;
        Integer time = 10;
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                operations.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE + customerNo);
                operations.opsForSet().add(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE + customerNo, codeStr);
                operations.expire(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE + customerNo, time, TimeUnit.MINUTES);
                operations.exec();
                return null;
            }
        });
        urlDecipheringService.sendSmsCode(codeStr, time, customerNo);
        map.put("code", codeStr);
        return map;
    }

    @Override
    @Transactional
    public Customer loginCustomer(String customerNo, String password) {
        logger.info("loginCustomer:customerNo：" +customerNo+" loginTime："+new Date());
        String pwd = MD5Util.myMD5(password);
        Customer customer = customerDao.findCustomerByCustomerNoAndPassword(customerNo, pwd);

        Set lockCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo);
        //已经锁定用户 24小时内不能登陆
        if (lockCustomer != null && lockCustomer.size() != 0) {
            logger.info("loginCustomer:" + " customerNo:" + customerNo + ",已锁定。");
            throw new AppException(ErrorCode.CUSTOMER_LOGIN_LOCK);
        }
        if (customer != null && customer.getStatus().equals(WalletConstants.CUSTOMER_STATUS_ACTIVE)) {
            customer.setLastLoginTime(new Date());
            customerDao.save(customer);
            logger.info("loginCustomer:success customerNo:" + customerNo);
            return customer;
        } else {
            logger.info("----------------输入错误超过五次后一小时内不能登录START----------------");
            Set object = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN + customerNo);
            List list = new ArrayList(object);
            List dateList = new ArrayList();
            if (list != null && list.size() != 0) {
                dateList = (List) list.get(0);
            }
            List newList = new ArrayList();
            //获取当前时间前30分钟的时间
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date beforeDate = DateUtil.addDateMinut(nowDate, -30);
            try {
                for (int i = 0; i < dateList.size(); i++) {
//                        Date date =  formatter.parse(o.toString());
                    Date date = new Date();
                    date = sdf.parse(dateList.get(i).toString());
                    //                    Date date = sdf.parse(((ArrayList) list.get(i)).get((((ArrayList) list.get(i))).size()-1).toString());
                    if (date.compareTo(beforeDate) <= 0) {
                        continue;
                    }
                    newList.add(sdf.format(date));
                }
            } catch (Exception e) {
                logger.error("loginAdminAccount-exception", e);
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }

            if (newList.size() == 5) {
                logger.info("loginCustomer:loginSize=5" + " size" + newList.size() + "  list:" + newList);
                redisTemplate.execute(new SessionCallback() {
                    @Override
                    public Object execute(RedisOperations operations)
                            throws DataAccessException {
                        operations.multi();
                        //已经输错5次 不能登陆
                        operations.opsForSet().add(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo, newList);
                        // redis过期时间 设置1小时后过期
                        operations.expire(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN_LOCK + customerNo, 24, TimeUnit.HOURS);
                        operations.exec();
                        return null;
                    }
                });
                throw new AppException(ErrorCode.CUSTOMER_LOGIN_LOCK);
            } else {
                newList.add(sdf.format(nowDate));
                logger.info("loginCustomer:loginSize<5 list:" + newList.toString() + "   size" + newList.size());
                redisTemplate.execute(new SessionCallback() {
                    @Override
                    public Object execute(RedisOperations operations)
                            throws DataAccessException {
                        operations.multi();
                        //已经输错n<5次
                        operations.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN + customerNo);
                        operations.opsForSet().add(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN + customerNo, newList);
                        // redis过期时间 设置30分钟后过期
                        operations.expire(RedisConstants.REDIS_NAME_CUSTOMER_LOGIN + customerNo, 30, TimeUnit.MINUTES);
                        operations.exec();
                        return null;
                    }
                });
            }
            logger.error("loginAdminAccount-fail", "customerNo:" + customerNo + ", loginTime:" + nowDate);
            logger.info("----------------输入错误超过五次后一小时内不能登录END----------------");
            throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
        }

    }


    private static String myMD5WithSalt(String password) {
        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;
                // 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
}
