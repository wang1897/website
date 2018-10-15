package com.aethercoder.core.service.error;

import com.aethercoder.core.dao.error.QtumTxErrorDao;
import com.aethercoder.core.entity.error.QtumTxError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hepengfei on 22/03/2018.
 */
@Service
public class QtumTxErrorService {
    @Autowired
    private QtumTxErrorDao qtumTxErrorDao;

    public QtumTxError saveError(QtumTxError qtumTxError) {
        return qtumTxErrorDao.save(qtumTxError);
    }
}
