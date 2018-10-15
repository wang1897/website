package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.Clearance;
import com.aethercoder.core.entity.wallet.ClearanceDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2017/1/16 下午5:41
 */
public interface ClearanceService {

    Page<Clearance> findClearanceByPage(Integer page, Integer size, String clearanceDay, Integer type, Long unit, Integer accountStatus, Boolean isClear);

    Page<ClearanceDetail> findClearanceDetailByPage(Integer page, Integer size, Long clearanceId);

    Page<ClearanceDetail> findAllClearanceDetailByPage(Integer page, Integer size, String clearanceDay, Long clearanceId, Integer qbaoType, Integer chainType, Long qbaoUnit,
                                                       Long chainUnit, Boolean isClear);

    List<ClearanceDetail> findUnClearClearanceDetail(Long clearanceId);

    List<ClearanceDetail> findClearanceDetail(Long clearanceId);

    Clearance updateClearance(Clearance clearance);
}
