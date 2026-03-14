package com.funds.infrastructure.api.dto;

import com.funds.domain.model.FundCategory;
import com.funds.domain.model.TransactionType;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundDtos {

    private FundDtos(){}

    public record SubscribeRequest(@NotBlank String clientId){}

    public record TransactionResponse(
            String id,
            String clientId,
            String fundId,
            String fundName,
            TransactionType type,
            BigDecimal amount,
            LocalDateTime createdAt
    ){}

    public record FundResponse(
            String id,
            String name,
            BigDecimal minimumAmount,
            FundCategory category
    ) {}

    public record ErrorResponse(
            String error,
            String message,
            int status
    ) {}
}
