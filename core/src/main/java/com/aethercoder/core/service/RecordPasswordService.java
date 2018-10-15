package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.RecordPassword;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface RecordPasswordService {

    RecordPassword checkUrl(String code);

}
