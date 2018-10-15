package com.aethercoder.core.service.impl;

import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.dao.SysConfigDao;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Service
public class SysconfigSerivceImpl implements SysConfigService{
    @Autowired
    private SysConfigDao sysConfigDao;
    @Override
    public List<SysConfig> findAllSysConfig() {
        return sysConfigDao.findAll();
    }

    @Override
    public SysConfig saveSysConfig(SysConfig sysConfig) {
        return sysConfigDao.save(sysConfig);
    }

    @Override
    public SysConfig updateSysConfig(SysConfig sysConfig) {
        SysConfig sysConfig1 = sysConfigDao.findOne(sysConfig.getId());
        BeanUtils.copyPropertiesWithoutNull(sysConfig, sysConfig1);
        return sysConfigDao.save(sysConfig1);
    }

    @Override
    public void deleteSysConfig(Long id) {
        sysConfigDao.delete(id);
    }

    @Override
    public SysConfig findSysConfigByName(String name) {
        return sysConfigDao.findSysConfigByName(name);
    }

    @Override
    public List<SysConfig> findSysConfigsLikeNameAhead(String name) {
        return sysConfigDao.findSysConfigByNameIsStartingWith(name);
    }
}
