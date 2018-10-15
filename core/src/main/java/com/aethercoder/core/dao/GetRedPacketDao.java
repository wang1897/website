package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.GetRedPacket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:39
 */
@Repository
public interface GetRedPacketDao extends JpaRepository<GetRedPacket, Long> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,value = "UPDATE get_red_packet grp inner join (select id from get_red_packet where red_packet_id = :redPacketId and account_no is null order by sequence limit 1) as son on son.id = grp.id set grp.account_no = :accountNo, grp.get_time = now() ")
    Integer updateRedPacket(@Param("redPacketId")Long redPacketId, @Param("accountNo")String accountNo);

    GetRedPacket getGetRedPacketByRedPacketIdAndAccountNo(@Param("redPacketId")Long redPacketId,@Param("accountNo")String accountNo);

    List<GetRedPacket> getGetRedPacketByRedPacketIdAndAccountNoIsNotNullOrderByGetTimeDesc(@Param("redPacketId")Long redPacketId);

    Page<GetRedPacket> findAll(Specification<GetRedPacket> specification, Pageable pageable);

}
