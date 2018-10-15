package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.InvitingReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/9
 * @modified By:
 */
@Repository
public interface InvitingRewardDao extends JpaRepository<InvitingReward,Long> {
    InvitingReward findByRewardName(String rewardName);
}
