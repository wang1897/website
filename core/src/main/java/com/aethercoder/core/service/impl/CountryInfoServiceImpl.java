package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.CountryInformationDao;
import com.aethercoder.core.entity.wallet.CountryInformation;
import com.aethercoder.core.service.CountryInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/21
 * @modified By:
 */
@Service
public class CountryInfoServiceImpl implements CountryInformationService {

    @Autowired
    CountryInformationDao countryInformationDao;

    @Override
    public List<CountryInformation> getCountryInfo() {

        return countryInformationDao.findAll();
    }

    @Override
    public void setLocale(Long countryInfoId) {
        CountryInformation countryInformation = countryInformationDao.findOne(countryInfoId);

        Locale currentLocale = null;
        String country = WalletConstants.LANGUAGE_TYPE_EN;
        if (countryInformation != null) {
            if (countryInformation.getCountry() != null) {
                country = countryInformation.getCountry();
            }
        }
        currentLocale = new Locale(country);
        LocaleContextHolder.setLocale(currentLocale);
    }
}
