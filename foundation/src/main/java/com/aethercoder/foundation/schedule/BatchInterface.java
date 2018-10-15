package com.aethercoder.foundation.schedule;

import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;

/**
 * Created by hepengfei on 08/12/2017.
 */
public interface BatchInterface {
    BatchResult run(BatchTask task) throws Exception;
}
