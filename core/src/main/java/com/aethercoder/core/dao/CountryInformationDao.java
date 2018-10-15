package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.CountryInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/21
 * @modified By:
 */
@Repository
public interface CountryInformationDao extends JpaRepository<CountryInformation,Long>{

}
