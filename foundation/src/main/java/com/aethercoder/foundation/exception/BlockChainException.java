package com.aethercoder.foundation.exception;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.foundation.exception.entity.ErrorCode;

/**
 * Created by hepengfei on 07/03/2018.
 */
public class BlockChainException extends AppException {
    public BlockChainException(ErrorCode errorCode) {
        super(errorCode.name());
    }

    public BlockChainException(String message) {
        super(message);
    }

    public BlockChainException(String message, ErrorCode errorCode) {
        super(message, errorCode.name());
    }

    public BlockChainException(ErrorCode errorCode, String[] messageArgs) {
        super(errorCode, messageArgs);
    }
}
