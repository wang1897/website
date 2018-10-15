package com.aethercoder.core.dao;

import com.aethercoder.core.entity.android.Android;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @auther Guo Feiyan
 * @date 2017/9/26 上午11:13
 */
@Repository
public interface AndroidDao  extends JpaRepository<Android, Long> {


    Android findFirstBySourceIsTrueOrderByIdDesc();

    Android findFirstBySourceIsFalseOrderByIdDesc();

}
