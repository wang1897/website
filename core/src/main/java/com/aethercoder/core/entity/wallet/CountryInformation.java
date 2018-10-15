package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/21
 * @modified By:
 */
@Entity
@Table(name = "t_country_information")
public class CountryInformation extends BaseEntity{
    protected static final long serialVersionUID = -1L;

    @Column(name = "country")
    private String country;

    @Column(name = "tel_number")
    private String telNumber;

    @Column(name = "country_name")
    private String countryName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }
}
