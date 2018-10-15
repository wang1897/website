package com.aethercoder.core.service;


import com.aethercoder.core.entity.member.MemberLevel;

import java.util.List;
import java.util.Map;

public interface MemberService {
    List getMemberInformation();

    List getMember();

    Map saveMemberLevelByMoney(MemberLevel memberLevel);

    Map updateMemberLevelByMoney(MemberLevel memberLevel);

    Map deleteMemberInformationByLevel(Long id);

    MemberLevel getMemberLevelByAccountNo(String accountNo);
}
