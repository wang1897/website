package com.aethercoder.core.dao.error;

import com.aethercoder.core.entity.error.QtumTxError;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hepengfei on 22/03/2018.
 */
public interface QtumTxErrorDao extends JpaRepository<QtumTxError, Long> {
}
