package com.funds.infrastructure.api;

import com.funds.domain.exception.DomainException;
import com.funds.infrastructure.api.dto.FundDtos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";

        log.error("Request error [{}]: {}", status.value(), message);

        FundDtos.ErrorResponse errorResponse = new FundDtos.ErrorResponse(
                status.getReasonPhrase(),
                message,
                status.value()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorResponse))
                .flatMap(bytes -> {
                    DataBuffer buffer = exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes);
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                });
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof DomainException domainException) {
            return domainException.getErrorCode().getHttpStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
