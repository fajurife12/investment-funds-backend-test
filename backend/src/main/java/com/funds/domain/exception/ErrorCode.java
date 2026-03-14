package com.funds.domain.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FUND_NOT_FOUND("Fondo no encontrado con id: %s", HttpStatus.NOT_FOUND),
    CLIENT_NOT_FOUND("Cliente no encontrado con id: %s", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE("No tiene saldo disponible para vincularse al fondo %s", HttpStatus.UNPROCESSABLE_ENTITY),
    ALREADY_SUBSCRIBED("El cliente ya está suscrito al fondo %s", HttpStatus.UNPROCESSABLE_ENTITY),
    NOT_SUBSCRIBED("El cliente no está suscrito al fondo %s", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String messageTemplate;
    private final HttpStatus httpStatus;

    public String formatMessage(String param) {
        return String.format(messageTemplate, param);
    }
}
