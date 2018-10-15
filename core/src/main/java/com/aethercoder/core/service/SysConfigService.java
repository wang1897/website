package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.SysConfig;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface SysConfigService {


    List<SysConfig> findAllSysConfig();

    SysConfig saveSysConfig(SysConfig sysConfig);

    SysConfig updateSysConfig(SysConfig sysConfig);

    void deleteSysConfig(Long id);

    SysConfig findSysConfigByName(String name);

    List<SysConfig> findSysConfigsLikeNameAhead(String name) ;

}
