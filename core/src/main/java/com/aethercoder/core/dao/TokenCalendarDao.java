package com.aethercoder.core.dao;
import com.aethercoder.core.entity.media.TokenCalendar;
import com.aethercoder.foundation.util.DateUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**@author lialngfeng
 * @date 2018/01/18
 */
@Repository
public interface TokenCalendarDao extends JpaRepository<TokenCalendar,Long> {
    TokenCalendar findTokenCalendarByIdAndIsDeleteIsFalse(Long id);

    List<TokenCalendar> findTokenCalendarByLanguageTypeAndIsDeleteIsFalse(String languageType);

    @Query(value = "SELECT t.* FROM qbao_schema.token_calendar t where t.language_type = :languageType and t.is_delete=0 and t.status = :status and (date_format(t.start_time,'%Y-%m') = :startTime or date_format(t.end_time,'%Y-%m') =:endTime )", nativeQuery = true)
    List<TokenCalendar> findByLanguageTypeAndMonths(@Param("languageType")String languageType, @Param("status")Integer status,
                                                    @Param("startTime")String startTime,@Param("endTime")String endTime);

    List<TokenCalendar> findByLanguageTypeAndIsDeleteIsFalseAndStartTimeIsLessThanEqualAndEndTimeIsGreaterThanEqual(String languageType,Date endTimeOfDay, Date startTimeOfDay);
}