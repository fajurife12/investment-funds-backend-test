package com.funds.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String param) {
        super(errorCode.formatMessage(param));
        this.errorCode = errorCode;
    }
}