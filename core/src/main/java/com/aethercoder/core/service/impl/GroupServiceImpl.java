package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.json.Data;
import com.aethercoder.core.entity.social.*;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.ContractService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.core.service.GroupService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.*;
import io.rong.RongCloud;
import io.rong.messages.GroupMessageData;
import io.rong.messages.GroupNtfMessage;
import io.rong.messages.QbaoSocialMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.CodeSuccessResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiawei.tao on 2017/10/10.
 */
@Service
public class GroupServiceImpl implements GroupService {

    private static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    public AccountService accountService;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    private AccountBalanceDao accountBalanceDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private GroupOfTagsDao groupOfTagsDao;

    @Autowired
    private GroupTagsDao groupTagsDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ContractService contractService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private GroupMemberDao getGroupMemberDao;

    private Integer unexisted = 0;
    private Integer inGroup = 1;
    private Integer groupGap = 2;
    private Integer sysBlack = 3;
    private Integer identify = 4;

    @Override
    public Group createGroup(Group group) {
        if (validationGroup(group)) {
            //群号-自动生成6位数 + 前缀Q 共7位
            group.setGroupNo(generateGroupNo());

            setGroupMaxMember(group);
            group.setIsDeleted(false);
            group.setConfirmStatus(WalletConstants.JOIN_GROUP_STATUS_DEFAULT);
            groupDao.save(group);
        }
        return group;
    }

    private void saveHotGroupTags() {

    }

    private void setGroupMaxMember(Group group) {
        Integer maxMember = 500;
        switch (group.getLevel()) {
            case 1:
                maxMember = 1000;
                break;
            case 2:
                maxMember = 1500;
                break;
            case 3:
                maxMember = 1500;
                break;
            case 4:
                maxMember = 2000;
                break;
            case 9:
                maxMember = 3000;
                break;
            default:
                maxMember = 500;
        }
        group.setMaxMember(maxMember);
    }

    @Override
    public Group buildGroup(BuildGroup buildGroup) {
        String qbtFee = sysConfigDao.findSysConfigByName(WalletConstants.BUILD_GROUP_QBT_FEE).getValue();
        String qbeFee = sysConfigDao.findSysConfigByName(WalletConstants.BUILD_GROUP_QBE_FEE).getValue();

        Contract contractQBT = contractDao.findContractByNameAndType(WalletConstants.QBT_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY, WalletConstants.CONTRACT_QTUM_TYPE);
        //如果是支付QBT check手续费是否一致
        if (contractQBT.getId().equals(buildGroup.getUnit())) {
            if (buildGroup.getAmount().compareTo(new BigDecimal(qbtFee)) != 0) {
                throw new AppException(ErrorCode.INPUT_INVALID);
            }
        } else if (contractQBE.getId().equals(buildGroup.getUnit())) {
            //如果是支付QBE check手续费是否一致
            if (buildGroup.getAmount().compareTo(new BigDecimal(qbeFee)) != 0) {
                throw new AppException(ErrorCode.INPUT_INVALID);
            }
        }
        Group group = new Group();
        //checkAmount
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(buildGroup.getAccountNo(), buildGroup.getUnit());
        if (null != accountBalance && accountBalance.getAmount().compareTo(buildGroup.getAmount()) >= 0) {
            //余额充足
            group.setLevel(WalletConstants.GROUP_LEVEL_NORMAL);
            group.setName(buildGroup.getName());
            group.setLogoUrl(buildGroup.getLogoUrl());

            group = createGroup(group);
            String[] accountNo = new String[]{buildGroup.getAccountNo()};
            joinGroupWithEx(group.getGroupNo(), accountNo, buildGroup.getAccountNo(), "",WalletConstants.JOIN_GROUP_STATUS_DEFAULT, true);

            //扣除余额
            BigDecimal decimal = accountBalance.getAmount().subtract(buildGroup.getAmount());
            accountBalance.setAmount(decimal);
            accountBalanceDao.save(accountBalance);
            //交易记录
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setType(WalletConstants.BUILD_GROUP);
            exchangeLog.setStatus(WalletConstants.CONFIRMED);
            exchangeLog.setUnit(buildGroup.getUnit());
            exchangeLog.setAmount(buildGroup.getAmount());
            exchangeLog.setAccountNo(buildGroup.getAccountNo());
            exchangeLogDao.save(exchangeLog);

        } else {
            //余额不足
            throw new AppException(ErrorCode.UNDER_BALANCE);
        }

        return group;
    }

    /***
     * 群号-自动生成6位数 + 前缀Q 共7位
     * @return 群号
     */
    private String generateGroupNo() {
        Integer maxValue = WalletConstants.ACCOUNT_NO_MAX;
        Integer minValue = WalletConstants.ACCOUNT_NO_MIN;
        List<String> reservedRegList = WalletConstants.RESERVED_ACCOUNT_NO_REG_EXP;
        while (true) {
            boolean isReserved = false;
            Random rand = new Random();
            int randNo = rand.nextInt(maxValue - minValue + 1) + minValue;
            String formatRandNo = NumberUtil.formatNumberToString(randNo, "000000");
            for (String reservedRegExp : reservedRegList) {
                isReserved = Pattern.matches(reservedRegExp, formatRandNo);
            }
            if (!isReserved) {
                Group group = groupDao.findByGroupNo(formatRandNo);
                if (group == null) {
                    return "Q" + formatRandNo;
                }
            }
        }
    }

    /**
     * 检测Group的是否合法
     */
    private Boolean validationGroup(Group group) {
        Boolean result = false;
        if (group == null) {
            return result;
        }
        // 判断群名字中是否含有非法字符。
        Pattern pattern = Pattern.compile("[&<>\"'/]");
        Matcher matcher = pattern.matcher(group.getName());
        if (matcher.find()) {
            throw new AppException(ErrorCode.INPUT_INVALID, new String[]{group.getName()});
        }
        // 判断群备注中是否含有非法字符。
        if (!StringUtils.isEmpty(group.getComment())) {
            matcher = pattern.matcher(group.getComment());
            if (matcher.find()) {
                throw new AppException(ErrorCode.INPUT_INVALID, new String[]{group.getComment()});
            }
        }
        result = true;
        return result;
    }

    @Override
    public Group updateGroup(Group group) {

        Group pGroup = findByGroupNo(group.getGroupNo());
        if (validationGroup(group)) {
            setGroupMaxMember(group);
            BeanUtils.copyPropertiesWithoutNull(group, pGroup);
            //判断群级别是5 并且群标签不为空时 GROUP_ROLE_HOSTER==火热群
            if (group.getLevel().equals(WalletConstants.GROUP_ROLE_HOSTER) && !org.springframework.util.StringUtils.isEmpty(group.getTag())) {
                GroupTags groupTags = groupTagsDao.findGroupTagsByNameAndAndIsDeleteFalse(group.getTag());
                if (groupTags == null) {
                    throw new AppException(ErrorCode.OPERATION_FAIL);
                }
                pGroup.setTag(groupTags.getName());
            }
            groupDao.save(pGroup);

        }
        // 刷新群组信息方法
        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            CodeSuccessResult groupRefreshResult = rongCloud.group.refresh(pGroup.getId().toString(), pGroup.getName());
            System.out.println("refresh:  " + groupRefreshResult.toString());
            if (groupRefreshResult.getCode() != 200) {
                throw new RuntimeException("refresh group error:" + groupRefreshResult.getErrorMessage());
            }
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("updateGroup error:" + ex.getMessage());
        }
        return pGroup;
    }

    @Override
    public GroupInfo updateGroupInfo(GroupInfo groupInfo) {

        //更新群名片
        if (null != groupInfo.getGroupMember() && (groupInfo.getGroupMember().getDisplayName() != null || !groupInfo.getGroupMember().getDisplayName().isEmpty())) {

            //校验accountName是否包含表情
            if (null != groupInfo.getGroupMember().getDisplayName() && StringUtil.containsEmoji(groupInfo.getGroupMember().getDisplayName())) {
                throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
            }
            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupInfo.getGroupMember().getGroupNo(), groupInfo.getGroupMember().getMemberNo());
            groupMember.setDisplayName(groupInfo.getGroupMember().getDisplayName());
            GroupMember groupMember1 = groupMemberDao.save(groupMember);
            groupInfo.setGroupMember(groupMember1);
        }
        if (null != groupInfo.getUserGroup() && (groupInfo.getUserGroup().getNoDistrub() != null || groupInfo.getUserGroup().getIsTop() != null)) {
            //更新该用户所在群的群知否置顶，免打扰
            UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(groupInfo.getUserGroup().getGroupNo(), groupInfo.getUserGroup().getAccountNo());
            if (null != groupInfo.getUserGroup().getDisplayName()) {
                //校验accountName是否包含表情
                if (null != groupInfo.getGroupMember().getDisplayName() && StringUtil.containsEmoji(groupInfo.getGroupMember().getDisplayName())) {
                    throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
                }
                userGroup.setDisplayName(groupInfo.getUserGroup().getDisplayName());
            }
            if (null != groupInfo.getUserGroup().getNoDistrub()) {
                userGroup.setNoDistrub(groupInfo.getUserGroup().getNoDistrub());
            }
            if (null != groupInfo.getUserGroup().getIsTop()) {
                userGroup.setIsTop(groupInfo.getUserGroup().getIsTop());
            }

            UserGroup userGroup1 = userGroupDao.save(userGroup);
            groupInfo.setUserGroup(userGroup1);
        }


        return groupInfo;
    }

    @Override
    public void updateGroupSequence(String[] groupNoList) {
        if (groupNoList == null || groupNoList.length == 0) {
            return;
        }
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < groupNoList.length; i++) {
            Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNoList[i]);
            group.setSequence(i);
            groups.add(group);
        }
        groupDao.save(groups);
    }

    @Override
    public void deleteGroup(String groupNo) {

        dismissGroup(groupNo, WalletConstants.GROUP_SYSTEM);
    }

    @Override
    public void dismissGroup(String groupNo, String accountNo) {
        Group group = findAvailableGroupByGroupNo(groupNo);
        // 逻辑删除
        group.setIsDeleted(true);
        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);

            // 解散群组方法。（将该群解散，所有用户都无法再接收该群的消息。）
            CodeSuccessResult groupDismissResult = rongCloud.group.dismiss(accountNo, groupNo);
            System.out.println("dismiss:  " + groupDismissResult.toString());
            if (groupDismissResult.getCode() != 200) {
                throw new RuntimeException("dismiss group error:" + groupDismissResult.getErrorMessage());
            }
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("dismissGroup error:" + ex.getMessage());
        }
        groupDao.save(group);

        //更新所有该群成员的UserGroup关系
        List<UserGroup> userGroupsList = userGroupDao.findUserGroupsByGroupNoAndIsDeletedIsFalse(groupNo);
        if (userGroupsList != null && userGroupsList.size() > 0) {
            String[] accountList = new String[userGroupsList.size()];
            for (int i = 0; i < userGroupsList.size(); i++) {
                UserGroup userGroup = userGroupsList.get(i);
                accountList[i] = userGroup.getAccountNo();
                userGroup.setIsDeleted(true);
            }
            String content = localeMessageUtil.getLocalMessage("DISMISS_GROUP", new String[]{"\"" + group.getName() + "\""});

            String pushContent = localeMessageUtil.getLocalMessage("DISMISS_GROUP_TITLE", null);
            String message = combineGroupMessage(content, group, null, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
            sendGroupMessage(accountList, message, pushContent);

            userGroupDao.save(userGroupsList);
        }
        return;
    }

    @Override
    public String joinGroupWithEx(String groupNo, String[] accountNoList, String operator, String confirmInfo,Integer confirmStatus, Boolean flag) {
        Map<Integer, List<String>> result = joinGroup(groupNo, accountNoList, operator, confirmInfo,confirmStatus, flag);
        Set<Integer> keys = result.keySet();
        for (Integer i : keys) {
            List<String> list = result.get(i);
            boolean hasError = !list.isEmpty();
            if (hasError) {
                if (unexisted.equals(i)) {
                    throw new AppException(ErrorCode.FUNDUSER_NOT_EXIST);
                } else if (groupGap.equals(i)) {
                    throw new AppException(ErrorCode.GROUP_BLACK_USER);
                } else if (sysBlack.equals(i)) {
                    throw new AppException(ErrorCode.OPERATION_FAIL);
                } else if (identify.equals(i)) {
                    return list.get(0);
                }
            }
        }
        return "";
    }

    @Override
    public Map<Integer, List<String>> joinGroup(String groupNo, String[] accountNoList, String operator, String confirmInfo,Integer confirmStatus, Boolean flag) {
        List<String> unexistedList = new ArrayList<>();
        List<String> inGroupList = new ArrayList<>();
        List<String> groupGapList = new ArrayList<>();
        List<String> sysBlackList = new ArrayList<>();
        Group group = findAvailableGroupByGroupNo(groupNo);
        Map<Integer, List<String>> result = new HashMap<>();
        result.put(unexisted, unexistedList);
        result.put(inGroup, inGroupList);
        result.put(groupGap, groupGapList);
        result.put(sysBlack, sysBlackList);

        Boolean isFirstUser = false;
        Integer countMember = groupMemberDao.countByGroupNoAndIsDeletedIsFalse(groupNo);
        if (countMember == 0) {
            isFirstUser = true;
        }
        // 操作人合法性check
        GroupMessageData data = new GroupMessageData();
        data.setOperatorNickname("");

        // 确认邀请人是在群里的
        if (operator != null && operator.length() >= 6 && isFirstUser == false) {
            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, operator);
            if (groupMember == null || groupMember.getIsDeleted()) {
                throw new AppException(ErrorCode.INVALID_OPERATOR);
            }
            Account operatorAccount = accountDao.findByAccountNo(operator);
            if (operatorAccount != null) {
                data.setOperatorNickname(operatorAccount.getAccountName());
            }
        }
        // 群成员数
        Integer memberNum = group.getMemberNum() == null ? 0 : group.getMemberNum();
        // 群最大人数
        Integer maxMember = group.getMaxMember() == null ? 0 : group.getMaxMember();

        // 群人数上限限制
        if (maxMember > 0 && (memberNum + accountNoList.length) > maxMember) {

            throw new AppException(ErrorCode.GROUP_OVERUSER);
        }

        List<GroupMember> groupMembersList = new ArrayList<GroupMember>();
        List<UserGroup> userGroupsList = new ArrayList<UserGroup>();
        List<String> accountList = new ArrayList<>();
        List<String> displayNameList = new ArrayList<>();
        Integer role = isFirstUser ? WalletConstants.GROUP_ROLE_HOSTER : WalletConstants.GROUP_ROLE_MEMBER;
        for (String accountNo : accountNoList) {
            Account account = accountDao.findByAccountNo(accountNo);
            if (account == null) {
                unexistedList.add(accountNo);
                continue;
            }
            //判断是否是系统黑名单
            if (account.getSysBlack() != null && account.getSysBlack()) {
                sysBlackList.add(accountNo);
                continue;
//                throw new AppException(ErrorCode.OPERATION_FAIL);
            }

            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, accountNo);
            if (groupMember != null && !groupMember.getIsDeleted()) {
                inGroupList.add(accountNo);
                continue;
            } else if (groupMember == null) {
                groupMember = new GroupMember();
                groupMember.setGroupNo(groupNo);
                groupMember.setMemberNo(account.getAccountNo());
                groupMember.setIsGap(false);
            } else if (groupMember.getIsGap()) {
                groupGapList.add(accountNo);
                continue;
//                throw new AppException(ErrorCode.GROUP_BLACK_USER);
            }

            //调用加群认证方法
            if (flag == false) {
                if (!joinGroupWithCheck(groupNo, accountNo, operator, confirmInfo,confirmStatus)) {
                    List<String> identifies = new ArrayList<>();
                    String identification = localeMessageUtil.getLocalMessage("JOIN_GROUP_WAITING", new String[]{"\"" + group.getName() + "\""});
                    identifies.add(identification);
                    result.put(identify, identifies);
                    return result;
                }

            }

            groupMember.setIsDeleted(false);
            groupMember.setRole(role);
            groupMember.setLevel(account.getLevel().intValue());
            groupMember.setLevelStatus(account.getLevelStatus());
            groupMember.setHeaderUrl(account.getHeader());
            groupMember.setDisplayName(account.getAccountName());
            groupMembersList.add(groupMember);

            UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(groupNo, accountNo);
            if (userGroup != null && !userGroup.getIsDeleted()) {
                continue;
            } else if (userGroup == null) {
                userGroup = new UserGroup();
                userGroup.setGroupNo(groupNo);
                userGroup.setAccountNo(accountNo);
//                    userGroup.setDisplayName(account.getAccountName());
//                    userGroup.setHeaderUrl(account.getHeader());
                userGroup.setIsTop(false);
                userGroup.setNoDistrub(false);
            }
            userGroup.setRole(isFirstUser ? WalletConstants.GROUP_ROLE_HOSTER : WalletConstants.GROUP_ROLE_MEMBER);
            userGroup.setIsDeleted(false);
            userGroupsList.add(userGroup);
            accountList.add(accountNo);
            String displayName = StringUtils.isEmpty(userGroup.getDisplayName()) ? account.getAccountName() : userGroup.getDisplayName();
            displayNameList.add(StringUtils.isEmpty(displayName) ? "" : displayName);
            role = WalletConstants.GROUP_ROLE_MEMBER;
        }

        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            if (isFirstUser) {
                // 创建群组方法（创建群组，并将用户加入该群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，每个群最大至 3000 人，App 内的群组数量没有限制.注：其实本方法是加入群组方法 /group/join 的别名。）
                CodeSuccessResult groupCreateResult = rongCloud.group.create(accountNoList, groupNo, group.getName());
                System.out.println("create:  " + groupCreateResult.toString());
                if (groupCreateResult.getCode() != 200) {
                    throw new RuntimeException("joinGroup error:" + groupCreateResult.getErrorMessage());
                }
            } else {
                // 将用户加入指定群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，每个群最大至 3000 人。
                CodeSuccessResult groupJoinResult = rongCloud.group.join(accountNoList, groupNo, group.getName());
                System.out.println("join:  " + groupJoinResult.toString());
                if (groupJoinResult.getCode() != 200) {
                    throw new RuntimeException("joinGroup error:" + groupJoinResult.getErrorMessage());
                }
            }
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("joinGroup error:" + ex.getMessage());
        }
        if (groupMembersList.size() > 0) {

            groupMemberDao.save(groupMembersList);

            countMember = groupMemberDao.countByGroupNoAndIsDeletedIsFalse(groupNo);
            group.setMemberNum(countMember);
            // 新加用户群表的更新
            userGroupDao.save(userGroupsList);

            groupDao.save(group);
            data.setTargetUserIds(accountList);
            data.setTargetUserDisplayNames(displayNameList);

            if (StringUtils.isEmpty(operator)) {
                operator = WalletConstants.GROUP_SYSTEM;
            }
            sendMessageInGroup(operator, WalletConstants.GROUP_OPERATION_ADD, new String[]{groupNo}, data, "", "", "");

            if (!StringUtils.isEmpty(operator) && !WalletConstants.GROUP_SYSTEM.equals(operator)) {
                String content = localeMessageUtil.getLocalMessage("JOIN_GROUP", new String[]{"\"" + group.getName() + "\""});
                String pushContent = localeMessageUtil.getLocalMessage("JOIN_GROUP_TITLE", null);
                String message = combineGroupMessage(content, group, null, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(accountNoList, message, pushContent);
            }
        }
        return result;
    }

    private Boolean joinGroupWithCheck(String groupNo, String accountNo, String operator, String confirmInfo,Integer confirmStatus1) {
        Boolean result = false;
        Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
        if (group == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXIST);
        }
        Integer confirmStatus = group.getConfirmStatus();
        confirmStatus = confirmStatus == null ? 0 : confirmStatus;
        //老用户confirmStatus1为null 不做check
//        confirmStatus1 = confirmStatus1 == null?confirmStatus:confirmStatus1;
        if (confirmStatus1 == null){
        }else {
            //不是老用户 判断传入的验证状态是否与数据库一致
            if (!confirmStatus.equals(confirmStatus1)){
                throw new AppException(ErrorCode.GROUP_STATUS_HAD_UPDATED);
            }
        }
        if (operator == null) {
            //申请加群
            //判断进群验证状态
            if (WalletConstants.JOIN_GROUP_STATUS_IDENTITY.equals(confirmStatus)) {
                //身份验证
                if (confirmInfo == null) {
                    //老用户 提醒升级新版本
                    throw new AppException(ErrorCode.VERSION_UPGRADE_WARN);
                }
                //获取此人进群申请结果
                Object status = redisTemplate.opsForValue().get(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo);
                if (status == null || WalletConstants.APPLICATION_RESULT_REFUSED.equals(Integer.valueOf(status.toString()))) {
                    //未申请或申请已过期或申请已被拒绝 可以申请
                    checkIdentity(confirmInfo, groupNo, accountNo);
                    return result;
                } else if (WalletConstants.APPLICATION_RESULT_PENDING.equals(Integer.valueOf(status.toString()))) {
                    //申请待处理 不可以重复申请
                    throw new AppException(ErrorCode.APPLICATION_PENDING);
                } else {
                    //申请已同意 不可以重复申请
                    return result;
                }
            } else if (WalletConstants.JOIN_GROUP_STATUS_COMMAND.equals(confirmStatus)) {
                //口令验证
                if (confirmInfo == null) {
                    //老用户 提醒升级新版本
                    throw new AppException(ErrorCode.VERSION_UPGRADE_WARN);
                } else if (!confirmInfo.trim().equalsIgnoreCase(group.getCommandInfo().trim())) {
                    throw new AppException(ErrorCode.GROUP_WRONG_WORD);
                }
            }
        } else {
            //邀请进群
            //进群验证状态为身份验证
            if (WalletConstants.JOIN_GROUP_STATUS_IDENTITY.equals(confirmStatus)) {
                GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, operator);
                if (groupMember == null) {
                    throw new AppException(ErrorCode.GROUP_MEMBER_NOT_EXIST);
                }
                Integer role = groupMember.getRole();
                String displayName = groupMember.getDisplayName();
                if (role == null || role.equals(0)) {
                    //邀请人是普通成员
                    String accountName = accountService.findAccountByAccountNo(accountNo).getAccountName();
                    confirmInfo = localeMessageUtil.getLocalMessage("JOIN_GROUP_CONFIRM_INFO", new String[]{displayName, accountName});
                    checkIdentity(confirmInfo, groupNo, accountNo);
                    return result;
                }
            }
        }
        result = true;
        return result;
    }

    private void checkIdentity(String confirmInfo, String groupNo, String accountNo) {
        //身份验证 向群主或管理员发送身份申请信息 多语言
        String content = confirmInfo;
        //获取群主和管理员
        List<GroupMember> groupMemberList = groupMemberDao.findAllByGroupNoAndRoleGreaterThanEqualAndIsDeletedIsFalse(groupNo, WalletConstants.GROUP_ROLE_ADMIN);
        List<String> memberNoList = new ArrayList<>();
        for (GroupMember groupMember : groupMemberList) {
            String memberNo = groupMember.getMemberNo();
            memberNoList.add(memberNo);
        }
        //如果这个群没有群主和管理员
        if (memberNoList.size() == 0) {
            throw new AppException(ErrorCode.INVALID_OPERATOR);
        }
        //将群主和管理员的accountNo放进数组
        String[] members = memberNoList.toArray(new String[memberNoList.size()]);

        //将请求处理结果 存入redis 默认为0：未处理
        redisTemplate.opsForValue().set(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo, WalletConstants.APPLICATION_RESULT_PENDING);
        //设置申请有效期 为3天
        redisTemplate.expire(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo, 3, TimeUnit.DAYS);

        String accountName = accountService.findAccountByAccountNo(accountNo).getAccountName();
        //向群主和管理员发送申请
        for (String member : members) {
            Locale memberlocalLang = accountService.getLocaleByAccount(member);
            String pushContent = localeMessageUtil.getLocalMessage("JOIN_GROUP_NOTICE", memberlocalLang);
            Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
            String applicationInfo = localeMessageUtil.getLocalMessage("JOIN_GROUP_APPLICATION_INFO", memberlocalLang, new String[]{accountName});

            Account account = accountService.findAccountByAccountNo(accountNo);
            String message = combineGroupMessage(content, group, account, WalletConstants.OPERATION_STATUS_PENDING, applicationInfo);
            sendGroupMessage(new String[]{member}, message.toString(), pushContent);
        }
    }

    @Override
    public void moveBlackListMember(List<GroupMember> groupMembersList, String operator) {
        List<GroupMember> gpMembersList = new ArrayList<>();
        for (GroupMember member : groupMembersList) {
            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(member.getGroupNo(), member.getMemberNo());
            if (groupMember == null || !groupMember.getIsDeleted()) {
                continue;
            }
            groupMember.setIsGap(false);
            gpMembersList.add(groupMember);
        }
        if (gpMembersList.size() > 0) {
            groupMemberDao.save(gpMembersList);
//            if (!StringUtils.isEmpty(operator)) {
//                String message = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP", new String[]{"\"" + group.getName() + "\""});
//                String pushContent = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP_TITLE", null);
//                sendInVite(WalletConstants.GROUP_SYSTEM, accountNoList, message, pushContent, null);
//            }
        }
    }

    @Override
    public void ejectGroups(String[] groupNoList, String[] accountNoList, String operator) {

        // 门槛群的自动踢出群
        List<GroupMember> groupMembersListAll = new ArrayList<GroupMember>();
        List<UserGroup> userGroupsListAll = new ArrayList<UserGroup>();
        List<Group> groupList = new ArrayList<>();
        List<String> accountList = new ArrayList<>();
        List<String> displayNameList = new ArrayList<>();
        for (String groupNo : groupNoList) {
            Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
            if (group == null) {
                continue;
            }
            // 群成员数
            Integer memberNum = group.getMemberNum() == null ? 0 : group.getMemberNum();

            List<GroupMember> groupMembersList = new ArrayList<GroupMember>();
            List<UserGroup> userGroupsList = new ArrayList<UserGroup>();

            for (String accountNo : accountNoList) {
                Account account = accountDao.findByAccountNo(accountNo);
                if (account != null) {
                    GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, accountNo);
                    if (groupMember == null || (groupMember.getIsDeleted())) {
                        continue;
                    }
                    UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(groupNo, accountNo);
                    if (userGroup == null || userGroup.getIsDeleted()) {
                        continue;
                    }
                    groupMember.setIsDeleted(true);
                    groupMembersList.add(groupMember);

                    userGroup.setIsDeleted(true);
                    userGroupsList.add(userGroup);
                    accountList.add(accountNo);
                    String displayName = StringUtils.isEmpty(userGroup.getDisplayName()) ? account.getAccountName() : userGroup.getDisplayName();
                    displayNameList.add(StringUtils.isEmpty(displayName) ? "" : displayName);
                }
            }

            try {
                RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
                CodeSuccessResult groupQuitResult = rongCloud.group.quit(accountNoList, groupNo);
                System.out.println("quit:  " + groupQuitResult.toString());
                if (groupQuitResult.getCode() != 200) {
                    throw new RuntimeException("quit group error:" + groupQuitResult.getErrorMessage());
                }
            } catch (IllegalArgumentException iex) {
                // 参数错误
                throw new AppException(iex.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException("quitGroup error:" + ex.getMessage());
            }
            if (groupMembersList.size() > 0) {

                groupMembersListAll.addAll(groupMembersList);
                userGroupsListAll.addAll(userGroupsList);

                Integer countMember = groupMemberDao.countByGroupNoAndIsDeletedIsFalse(groupNo);
                group.setMemberNum(countMember);
                groupList.add(group);
            }
        }
        if (groupList.size() > 0) {
            groupMemberDao.save(groupMembersListAll);
            // 被删用户群表的更新
            userGroupDao.save(userGroupsListAll);
            groupDao.save(groupList);
            StringBuilder groupNames = new StringBuilder();
            for (Group group : groupList) {
                groupNames.append(",\"" + group.getName() + "\"");
            }
            String groupName = groupNames.toString().substring(1);

            if (!StringUtils.isEmpty(operator)) {
                // 发送群系统消息
                String content = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP", new String[]{groupName});
                String pushContent = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP_TITLE", null);

                String message = combineGroupMessage(content, groupList.get(0), null, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(accountNoList, message, pushContent);
            }
        }
    }

    @Override
    public void ejectGroup(String groupNo, String[] accountNoList, String operator, String isGap, Boolean flagType) {
        Group group = findAvailableGroupByGroupNo(groupNo);
        GroupMember operatorMember = new GroupMember();
        // 判断踢人的人有管理权限
        if (!StringUtils.isEmpty(operator) && !WalletConstants.GROUP_SYSTEM.equals(operator)) {
            operatorMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, operator);
            if (operatorMember == null || null == operatorMember.getRole() || WalletConstants.GROUP_ROLE_MEMBER.equals(operatorMember.getRole())) {
                throw new AppException(ErrorCode.INVALID_OPERATOR);
            }
        }

        List<GroupMember> groupMembersList = new ArrayList<GroupMember>();
        List<UserGroup> userGroupsList = new ArrayList<UserGroup>();
        List<String> accountList = new ArrayList<>();
        List<String> displayNameList = new ArrayList<>();
        for (String accountNo : accountNoList) {
            Account account = accountDao.findByAccountNo(accountNo);
            if (flagType) {
                List<UserGroup> userGroups = userGroupDao.findUserGroupsByAccountNoAndIsDeletedIsFalse(accountNo);
                String[] userGroupNos = new String[userGroups.size()];
                for (int i = 0; i < userGroups.size(); i++) {
                    userGroupNos[i] = userGroups.get(i).getGroupNo();
                }
                account.setSysBlack(true);
                accountDao.save(account);
                ejectGroups(userGroupNos, accountNoList, WalletConstants.GROUP_SYSTEM);

            }
            if (account != null) {
                GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, accountNo);
                if (groupMember == null || (groupMember.getIsDeleted())) {
                    continue;
                }
                UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(groupNo, accountNo);
                if (userGroup == null || userGroup.getIsDeleted()) {
                    continue;
                }
                groupMember.setIsDeleted(true);
                if (WalletConstants.IS_GAP.equals(isGap)) {
                    groupMember.setIsGap(true);
                }
                groupMembersList.add(groupMember);

                userGroup.setIsDeleted(true);
                userGroupsList.add(userGroup);
                accountList.add(accountNo);
                String displayName = StringUtils.isEmpty(userGroup.getDisplayName()) ? account.getAccountName() : userGroup.getDisplayName();
                displayNameList.add(StringUtils.isEmpty(displayName) ? "" : displayName);
            }
        }

        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            CodeSuccessResult groupQuitResult = rongCloud.group.quit(accountNoList, groupNo);
            System.out.println("quit:  " + groupQuitResult.toString());
            if (groupQuitResult.getCode() != 200) {
                throw new RuntimeException("quit group error:" + groupQuitResult.getErrorMessage());
            }
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("quitGroup error:" + ex.getMessage());
        }
        if (groupMembersList.size() > 0) {

            groupMemberDao.save(groupMembersList);
            // 被删用户群表的更新
            userGroupDao.save(userGroupsList);

            Integer countMember = groupMemberDao.countByGroupNoAndIsDeletedIsFalse(groupNo);
            group.setMemberNum(countMember);
            for (GroupMember groupMember : groupMembersList) {
                String accountNo = groupMember.getMemberNo();
                Object result = redisTemplate.opsForValue().get(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo);
                if (result != null) {
                    redisTemplate.delete(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo);
                }
            }
            groupDao.save(group);

            logger.info("-------------------------------发送群内系统消息 start-----------------------------------------");
            String type = null;
            String operatorId = null;
            //发送群内系统消息
            GroupMessageData data = new GroupMessageData();

            //该用户退出群 用户只有一个&&退出的用户=操作者
            boolean flag = ((accountNoList.length == 1 && operator == null) || (operator != null && operator.equals(accountList.get(0).toString())));
            if (flag) {
                type = WalletConstants.GROUP_OPERATION_QUIT;
                operatorId = accountList.get(0).toString();
                data.setOperatorNickname(displayNameList.get(0).toString());
            } else {
                type = WalletConstants.GROUP_OPERATION_KICKED;
                operatorId = operator == null ? WalletConstants.GROUP_SYSTEM : operator;
            }

            Account operatorAccount = accountDao.findByAccountNo(operatorId);
            if (operatorAccount != null) {
                data.setOperatorNickname(operatorAccount.getAccountName());
            }
            data.setTargetUserIds(accountList);
            data.setTargetUserDisplayNames(displayNameList);
            sendMessageInGroup(operatorId, type, new String[]{groupNo}, data, "", "", "");
            logger.info("-------------------------------发送群内系统消息 end-----------------------------------------");

            if (!StringUtils.isEmpty(operator)) {

                // 发送群系统消息
                String content = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP", new String[]{"\"" + group.getName() + "\""});
                String pushContent = localeMessageUtil.getLocalMessage("KICK_FROM_GROUP_TITLE", null);
                String message = combineGroupMessage(content, group, null, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(accountNoList, message, pushContent);
            }
        }
    }

    @Override
    public void updateGroupMember(List<GroupMember> groupMembersList, String operator) {
        Group group = null;

        String type = null;
        List<GroupMember> gpMembersList = new ArrayList<>();
        List<UserGroup> userGroupList = new ArrayList<>();
        List<String> accountList = new ArrayList<>();

        List<String> displayNameList = new ArrayList<>();
        for (GroupMember member : groupMembersList) {
            if (group == null) {
                group = findAvailableGroupByGroupNo(member.getGroupNo());
            }

            Account account = accountDao.findByAccountNo(member.getMemberNo());
            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(member.getGroupNo(), member.getMemberNo());

            if (member.getRole().equals(WalletConstants.GROUP_ROLE_ADMIN) && groupMember.getRole().equals(WalletConstants.GROUP_ROLE_MEMBER)) {
                //设置管理员权限
                type = "KICK_FROM_GROUP_MSG_SET_MANAGER";
            } else if (member.getRole().equals(WalletConstants.GROUP_ROLE_MEMBER) && groupMember.getRole().equals(WalletConstants.GROUP_ROLE_ADMIN)) {
                //取消管理员权限
                type = "KICK_FROM_GROUP_MANAGEMENT_CANCEL";
            } else if (member.getRole().equals(WalletConstants.GROUP_ROLE_HOSTER)) {
                //设置群主权限
                type = "KICK_FROM_GROUP_MSG_SET_HOSTER";
            } else if (member.getRole().equals(WalletConstants.GROUP_ROLE_MEMBER) && groupMember.getRole().equals(WalletConstants.GROUP_ROLE_HOSTER)) {
                //取消群主权限
                type = "KICK_FROM_GROUP_MANAGE_CANCEL";
            }
            if (groupMember == null || (groupMember.getIsDeleted())) {
                continue;
            }
            groupMember.setRole(member.getRole());
            gpMembersList.add(groupMember);
            UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(member.getGroupNo(), member.getMemberNo());
            userGroup.setRole(member.getRole());
            userGroupList.add(userGroup);
            accountList.add(member.getMemberNo());
            String displayName = StringUtils.isEmpty(userGroup.getDisplayName()) ? account.getAccountName() : userGroup.getDisplayName();
            displayNameList.add(StringUtils.isEmpty(displayName) ? "" : displayName);
        }
        if (gpMembersList.size() > 0) {

            groupMemberDao.save(gpMembersList);
            userGroupDao.save(userGroupList);
            String[] accountNoList = accountList.toArray(new String[accountList.size()]);
            if (!StringUtils.isEmpty(operator)) {
                Account accountOperator = accountDao.findByAccountNo(operator);
                String content = localeMessageUtil.getLocalMessage(type, new String[]{accountOperator.getAccountName(), "\"" + group.getName() + "\""});
                String pushContent = localeMessageUtil.getLocalMessage("GROUP_SYSTEM_MSG", null);

                String message = combineGroupMessage(content, group, null, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(accountNoList, message, pushContent);
            }
        }
    }

    @Override
    public Group findAvailableGroupByGroupNo(String groupNo) {
        Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
        if (group == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXIST);
        }
        return group;
    }

    @Override
    public Group findByGroupNo(String groupNo) {
        Group group = groupDao.findGroupByGroupNo(groupNo);
        if (group == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXIST);
        }
        return group;
    }

    @Override
    public List<Group> findGroupAll() {
        return groupDao.findAll();
    }

    @Override
    public List<GroupMember> findGroupMembersByRoleAndIsDeletedFalse(Integer role) {
        return groupMemberDao.findGroupMembersByRoleAndIsDeletedFalseOrderByIdDesc(role);
    }

    @Override
    public List<Group> findGroupsByHoster(String accountNo) {
        return groupDao.findGroupsByHoster(accountNo);
    }
//
//    @Override
//    public Page<Group> findGroupsByPage(Integer page, Integer size) {
//        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "updateTime");
//        return groupDao.findAll(pageable);
//    }

    @Override
    public Page<Group> findGroupsByPage(Integer page, Integer size, String groupNo, String groupName, Integer level, Boolean isDeleted, Long limitUnit, String tag) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "isDeleted"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "level"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "sequence"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "updateTime"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        Page<Group> groupPage = groupDao.findAll(new Specification<Group>() {
            @Override
            public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                //群No
                if (!StringUtils.isEmpty(groupNo)) {
                    list.add(criteriaBuilder.like(root.get("groupNo").as(String.class), groupNo + "%"));
                }
                if (!StringUtils.isEmpty(groupName)) {
                    //群名称
                    list.add(criteriaBuilder.like(root.get("name").as(String.class), groupName + "%"));
                }
                //群类型
                if (level != null) {
                    list.add(criteriaBuilder.equal(root.get("level").as(Integer.class), level));
                }
                //群解除FLAG
                if (isDeleted != null) {
                    list.add(criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), isDeleted));
                }
                //群门槛币
                if (limitUnit != null) {
                    list.add(criteriaBuilder.equal(root.get("limitUnit").as(Long.class), limitUnit));
                }
                //群标签
                if (tag != null) {
                    list.add(criteriaBuilder.equal(root.get("tag").as(String.class), tag));
                    /*query.groupBy(root.get("tag"));
                    list.add(query.getGroupRestriction());*/
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return groupPage;
    }

    @Override
    public List<Group> findGroupsByPage(Integer page, Integer size) {
        return groupDao.getHotGroups();
    }

    @Override
    public Page<Group> findGroupsByPage(Integer page, Integer size, String groupNoOrName) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "updateTime");
        Page<Group> groupPage = groupDao.findAll(new Specification<Group>() {
            @Override
            public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                //群No
                list.add(criteriaBuilder.like(root.get("groupNo").as(String.class), groupNoOrName + "%"));
                //群名称
                list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + groupNoOrName + "%"));

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(criteriaBuilder.or(list.toArray(p)), criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), new Boolean(false)));
            }
        }, pageable);
        return groupPage;
    }

    @Override
    public List<GroupMember> queryGroupMember(String groupNo) {
        return groupMemberDao.findAllByGroupNoAndIsDeletedIsFalse(groupNo);
    }

    private List<GroupMember> queryGroupMemberTop4(String groupNo) {
        return groupMemberDao.findFirst4ByGroupNoAndIsDeletedIsFalseOrderByRoleDescCreateTimeAsc(groupNo);
    }

    @Override
    public Integer queryGroupAdminNumber(String groupNo) {
        return groupMemberDao.countByGroupNoAndIsDeletedIsFalseAndRoleIsGreaterThanEqual(groupNo, WalletConstants.GROUP_ROLE_ADMIN);
    }

    @Override
    public UserInfo syncGroupMember(String groupNo, String timestamp) {
        UserInfo groupInfo = new UserInfo();
        // 时间没有传，则给定本世纪最初时间。取全量数据。
        if (StringUtils.isEmpty(timestamp)) {
            timestamp = WalletConstants.TIMESTAMP_DEFAULT;
        }
        Date updateTime = DateUtil.stringToDate(timestamp);
        Date now = new Date();
//        groupInfo.setGroupMembersList(groupMemberDao.findAllByGroupNoAndIsDeletedIsFalseAndUpdateTimeAfter(groupNo, updateTime));
        groupInfo.setGroupMembersList(groupMemberDao.findAllByGroupNoAndUpdateTimeAfter(groupNo, updateTime));
        groupInfo.setVersion(DateUtil.dateToString(now));
        return groupInfo;
    }

    @Override
    public Page<GroupMember> queryGroupByPage(Integer page, Integer size, String groupNo, String memberNoOrName, Integer role, Boolean isGap) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "role");
        Page<GroupMember> groupMemberPage = groupMemberDao.findAll(new Specification<GroupMember>() {
            @Override
            public Predicate toPredicate(Root<GroupMember> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Boolean isDeleted = false;
                if (null != isGap && isGap == true) {
                    isDeleted = true;
                }

                // 群No
                Predicate p = cb.and(cb.equal(root.get("groupNo").as(String.class), groupNo),
                        cb.equal(root.get("isDeleted").as(Boolean.class), isDeleted));

                if (null != memberNoOrName && !"".equals(memberNoOrName)) {
                    //成员No
                    Predicate pf = cb.or(cb.like(root.get("displayName").as(String.class), memberNoOrName + "%"), cb.like(root.get("memberNo").as(String.class), memberNoOrName + "%"));
                    //成员名称
                    p = cb.and(p, pf);
                }
                if (null != role) {
                    if (role > 0) {
                        Predicate pf = cb.equal(root.get("role").as(Integer.class), role);
                        //成员类型
                        p = cb.and(p, pf);
                    } else {
                        Predicate pf = cb.or(cb.isNull(root.get("role").as(Integer.class)), cb.equal(root.get("role").as(Integer.class), 0));
                        //成员类型
                        p = cb.and(p, pf);
                    }
                }
                if (null != isGap) {
                    Predicate pf = cb.equal(root.get("isGap").as(Boolean.class), isGap);
                    //成员类型
                    p = cb.and(p, pf);
                }
                return p;
            }
        }, pageable);
        return groupMemberPage;
    }

    @Override
    public UserInfo syncUserGroupInfo(String accountNo, String timestamp) {
        UserInfo groupInfo = new UserInfo();
        // 时间没有传，则给定本世纪最初时间。取全量数据。
        if (StringUtils.isEmpty(timestamp)) {
            timestamp = WalletConstants.TIMESTAMP_DEFAULT;
        }
        Date updateTime = DateUtil.stringToDate(timestamp);
        Date now = new Date();
        groupInfo.setUserGroupsList(userGroupDao.findAllByAccountNoAndUpdateTimeAfter(accountNo, updateTime));
        // 暂时不要返回群成员列表
//        groupInfo.setGroupMembersList(groupMemberDao.findAllByUpdateTimeAfterForSync(accountNo, updateTime));
        groupInfo.setGroupList(groupDao.findAllByUpdateTimeAfterForSync(accountNo, updateTime));
        groupInfo.setVersion(DateUtil.dateToString(now));
        return groupInfo;
    }

    @Override
    public Page<Account> queryAddGroupUsersByPage(Integer page, Integer size, String groupNo, String memberNoOrName) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "create_Time");
        Page<Account> groupMemberPage;
        if (StringUtils.isEmpty(memberNoOrName)) {
            groupMemberPage = accountDao.findAllNotInGroup(groupNo, pageable);
        } else {
            groupMemberPage = accountDao.findAllByMemberNoOrNameAndNotInGroup(groupNo, memberNoOrName, memberNoOrName, pageable);
        }
        return groupMemberPage;
    }

    @Override
    public List<Group> getGroupsByLevel(Integer level) {
        return groupDao.findByLevelAndIsDeletedIsFalseOrderBySequenceAsc(level);
    }

    @Override
    public List<Data> queryGroupBuildFee() {
        Contract contractQBT = contractDao.findContractByNameAndType(WalletConstants.QBT_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY, WalletConstants.CONTRACT_QTUM_TYPE);
        Map map = new HashMap();
        String qbtFee = sysConfigDao.findSysConfigByName(WalletConstants.BUILD_GROUP_QBT_FEE).getValue();
        String qbeFee = sysConfigDao.findSysConfigByName(WalletConstants.BUILD_GROUP_QBE_FEE).getValue();

        List<Data> dataList = new ArrayList<>();
        Data dataQbt = new Data();
        dataQbt.setKey(contractQBT.getId().toString());
        dataQbt.setValue(qbtFee);
        dataList.add(dataQbt);
        Data dataQbe = new Data();
        dataQbe.setKey(contractQBE.getId().toString());
        dataQbe.setValue(qbeFee);
        dataList.add(dataQbe);


        return dataList;

    }

    @Override
    @Transactional
    public void transferOfGroup(String group, String oldGroupLord, String newGroupLord) {
        //check 群是否存在 oldGroupLord是否是群主 newGroupLord 是否是该群群成员

        GroupMember oldGroupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(group, oldGroupLord);
        GroupMember newGroupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(group, newGroupLord);
        if (oldGroupMember != null && oldGroupMember.getRole().equals(WalletConstants.GROUP_ROLE_HOSTER) && newGroupMember != null) {

            //1.设老群主为普通成员 且不提示消息
            oldGroupMember.setRole(WalletConstants.GROUP_ROLE_MEMBER);
            UserGroup userGroup = userGroupDao.findUserGroupByGroupNoAndAccountNo(group, oldGroupLord);
            userGroup.setRole(WalletConstants.GROUP_ROLE_MEMBER);
            groupMemberDao.save(oldGroupMember);
            userGroupDao.save(userGroup);

            //2.设置新群主
            List<GroupMember> groupMembersList = new ArrayList<GroupMember>();
            newGroupMember.setRole(WalletConstants.GROUP_ROLE_HOSTER);
            groupMembersList.add(newGroupMember);
            updateGroupMember(groupMembersList, WalletConstants.GROUP_SYSTEM);

            //3.踢出老群主
            ejectGroup(group, new String[]{oldGroupLord}, null, null, false);

        } else {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

    }

    private void syncUserGroupWithRongCloud(String accountNo, io.rong.models.GroupInfo[] groupInfos) {
        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            CodeSuccessResult groupQuitResult = rongCloud.group.sync(accountNo, groupInfos);
            System.out.println("quit:  " + groupQuitResult.toString());
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("syncGroupInfo error:" + ex.getMessage());
        }
    }

    @Override
    public GroupInfo queryGroupByGroupNoAndAccountNo(String groupNo, String accountNo) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupMembersList(queryGroupMemberTop4(groupNo));
        groupInfo.setGroup(findByGroupNo(groupNo));
        if (!StringUtils.isEmpty(accountNo)) {
            groupInfo.setGroupMember(groupMemberDao.findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(groupNo, accountNo));
            groupInfo.setUserGroup(userGroupDao.findUserGroupByGroupNoAndAccountNo(groupNo, accountNo));
        }
        return groupInfo;
    }

    // 发送群内系统消息
    private void sendMessageInGroup(String fromUserNo, String type, String[] groupNoList, GroupMessageData groupData, String message, String pushContent, String pushData) {
        String data = groupData.toString();
        GroupNtfMessage grpMessage = new GroupNtfMessage(fromUserNo, type, data, message);
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishGroupNotice(fromUserNo,
                    groupNoList,
                    grpMessage,
                    pushContent,
                    pushData, null, null, null);
            logger.info("----sendMessageInGroup :" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("---sendMessageInGroup error----", e);
            throw new RuntimeException("sendMessageInGroup error:" + e.getMessage());
        }
    }

    /**
     * 发送群相关消息
     */
    private void sendGroupMessage(String[] toUserNoList, String message, String pushContent) {
        logger.info("sendGroupMessage content:" + message);
        Locale locale = accountService.getLocaleByAccount(toUserNoList[0]);

        QbaoSocialMessage txtMessage = new QbaoSocialMessage(message);
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishPrivate(WalletConstants.GROUP_SYSTEM,
                    toUserNoList,
                    txtMessage,
                    pushContent,
                    null,
                    null,
                    null, null, null, null);
            logger.info("----sendGroupMessage :" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("---sendGroupMessage error----", e);
            throw new RuntimeException("sendGroupMessage error:" + e.getMessage());
        }
    }

    // 发送群系统消息
    @Override
    public void sendInVite(String fromUserNo, String[] toUserNoList, String message, String pushContent, String pushData) {
        TxtMessage txtMessage = new TxtMessage(message, "");
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishPrivate(fromUserNo,
                    toUserNoList,
                    txtMessage,
                    pushContent,
                    pushData,
                    null,
                    null,null,null,null);
            logger.info("----sendInVite :" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("---sendInVite error----", e);
            throw new RuntimeException("sendInVite error:" + e.getMessage());
        }
    }

    @Override
    public GroupMember findByGroupNoAndRole(String groupNo) {
        return groupMemberDao.findGroupMemberByGroupNoAndRoleAndIsDeletedFalse(groupNo, WalletConstants.GROUP_ROLE_HOSTER);
    }

    @Override
    public GroupMember getGroupMemberInfo(String groupNo, String memberNo) {

        return groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, memberNo);
    }

    @Override
    public void setGroupConfirmStatus(String groupNo, String accountNo, Integer type, String commandInfo) {
        //判断是否为管理员或群主，否 不能修改进群验证
        GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNo, accountNo);
        if (groupMember == null || null == groupMember.getRole() || WalletConstants.GROUP_ROLE_MEMBER.equals(groupMember.getRole())) {
            throw new AppException(ErrorCode.INVALID_OPERATOR);
        }
        //是管理员或群主
        Group group = groupDao.findByGroupNo(groupNo);
        if (group == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXIST);
        }
        if (type.equals(WalletConstants.JOIN_GROUP_STATUS_COMMAND)) {
            //设置 口令验证
            group.setCommandInfo(commandInfo);
        }
        group.setConfirmStatus(type);
        groupDao.save(group);

    }

    @Override
    public void dealWithIdentityInfo(String groupNo, String accountNo, Integer type, String operator) {
        //获取redis中请求处理结果
        Object status = redisTemplate.opsForValue().get(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo);
        if (status == null) {
            //请求已过期
            throw new AppException(ErrorCode.APPLICATION_OVERDUE);
        } else if (WalletConstants.APPLICATION_RESULT_PENDING.equals(Integer.valueOf(status.toString()))) {
            //申请待处理
            Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
            if (group == null) {
                throw new AppException(ErrorCode.GROUP_NOT_EXIST);
            }

            //获取群主和管理员
            List<GroupMember> groupMemberList = groupMemberDao.findAllByGroupNoAndRoleGreaterThanEqualAndIsDeletedIsFalse(groupNo, WalletConstants.GROUP_ROLE_ADMIN);
            List<String> memberNoList = new ArrayList<>();
            for (GroupMember groupMember : groupMemberList) {
                String memberNo = groupMember.getMemberNo();
                memberNoList.add(memberNo);
            }
            //如果这个群没有群主和管理员
            if (memberNoList.size() == 0) {
                throw new AppException(ErrorCode.INVALID_OPERATOR);
            }
            //处理结果存入redis
            redisTemplate.opsForValue().set(RedisConstants.REDIS_NAME_JOIN_GROUP + groupNo + accountNo, type);
            //将群主和管理员的accountNo放进数组
            String[] members = memberNoList.toArray(new String[memberNoList.size()]);
            Account account = accountService.findAccountByAccountNo(accountNo);
            Account operatorAccount = accountService.findAccountByAccountNo(operator);
            Locale accountLang = accountService.getLocaleByAccount(accountNo);
            String pushContent = localeMessageUtil.getLocalMessage("JOIN_GROUP_NOTICE", accountLang);
            if (WalletConstants.APPLICATION_RESULT_AGREED.equals(type)) {

                //同意进群
                joinGroupWithEx(groupNo, new String[]{accountNo}, operator, "",WalletConstants.JOIN_GROUP_STATUS_DEFAULT, true);
                //向申请人发送同意消息
                String content = localeMessageUtil.getLocalMessage("JOIN_GROUP_APPLICATION_AGREED", accountLang, new String[]{group.getName()});
                String message = combineGroupMessage(content, group, account, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(new String[]{accountNo}, message, pushContent);
                //向管理发送处理结果
                //发送消息多语言
                for (String member : members) {
                    Account memberAccount = accountService.findAccountByAccountNo(member);
                    Locale memberlocal = accountService.getLocaleByAccount(member);
                    String contentForAdmin = localeMessageUtil.getLocalMessage("JOIN_GROUP_ADMIN_AGREED", memberlocal, new String[]{operatorAccount.getAccountName(), account.getAccountName()});
                    String messageForAdmin = combineGroupMessage(contentForAdmin, group, account, WalletConstants.OPERATION_STATUS_AGREED, null);
                    sendGroupMessage(new String[]{member}, messageForAdmin, pushContent);
                }
            } else {
                //向申请人发送拒绝消息
                String content = localeMessageUtil.getLocalMessage("JOIN_GROUP_APPLICATION_REFUSED", accountLang, new String[]{group.getName()});
                String message = combineGroupMessage(content, group, account, WalletConstants.OPERATION_STATUS_NO_SHOWN, null);
                sendGroupMessage(new String[]{accountNo}, message, pushContent);
                //向管理发送处理结果
                //发送消息多语言
                for (String member : members) {
                    Account memberAccount = accountService.findAccountByAccountNo(member);
                    Locale memberlocal = accountService.getLocaleByAccount(member);
                    String contentForAdmin = localeMessageUtil.getLocalMessage("JOIN_GROUP_ADMIN_REFUSED", memberlocal, new String[]{operatorAccount.getAccountName(), account.getAccountName()});
                    String messageForAdmin = combineGroupMessage(contentForAdmin, group, account, WalletConstants.OPERATION_STATUS_REFUSED, null);
                    sendGroupMessage(new String[]{member}, messageForAdmin, pushContent);
                }

            }
        } else {
            //申请已处理
            throw new AppException(ErrorCode.APPLICATION_MANAGED);
        }
    }

    private String combineGroupMessage(String content, Group group, Account account, String operationStatus, String note) {

        SocialMessage message = new SocialMessage();
        message.setDescriptions(content);
        if (group != null) {
            message.setGroupNo(group.getGroupNo());
            message.setGroupName(group.getName());
            message.setGroupPortrait(group.getLogoUrl());
        }
        if (account != null) {
            message.setAccountNo(account.getAccountNo());
            message.setUserName(account.getAccountName());
            message.setUserPortrait(account.getHeader());
            message.setUserLevel(account.getLevel() == null ? "0" : account.getLevel().toString());
            message.setUserLevelStatus(account.getLevelStatus() ? "1" : "0");
        }
        if (StringUtils.isNotBlank(note)) {
            message.setJoinGroupNote(note);
        } else {
            message.setJoinGroupNote(content);
        }
        message.setSocialType(WalletConstants.GROUP_SYSTEM);
        message.setOperationStatus(operationStatus);

        return message.toString();
    }

    @Override
    public String getGroupShareUrl(String groupNo, String language) {
        //获取群分享地址
        SysConfig urlConfig = sysConfigService.findSysConfigByName(WalletConstants.GROUP_URL);

        System.out.println();
        return urlConfig.getValue() + "?groupNo=" + groupNo  + "&lang=" + language;
    }

    @Override
    public Map getGroupShareUrlInfo(String groupNo) {
        //获取群
        Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupNo);
        if (group == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXIST);
        }
        //群名称
        String name = group.getName();
        Map map = new HashMap();
        map.put("groupName",name);
        //加群手续费币种
        Long unit = group.getLimitUnit();
        map.put("unit",unit);
        //加群手续费币种名称
        String contractName = null;
        if (unit != null){
            Contract contract = contractService.findContractById(unit);
            if (contract == null) {
                throw new AppException(ErrorCode.CONTRACT_NAME_NOT_EXIST);
            }
            contractName = contract.getName();
        }
        map.put("contractName",contractName);
        //进群手续费
        Integer limitAmount = group.getLimitAmount();
        map.put("limitAmount",limitAmount);
        //群log
        String logoUrl = group.getLogoUrl();
        map.put("logoUrl",logoUrl);
        return map;
    }
}
