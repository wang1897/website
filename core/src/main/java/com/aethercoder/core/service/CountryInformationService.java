package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.CountryInformation;

import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/21
 * @modified By:
 */
public interface CountryInformationService {

    List<CountryInformation> getCountryInfo();

    void setLocale(Long countryInfoId);
}
