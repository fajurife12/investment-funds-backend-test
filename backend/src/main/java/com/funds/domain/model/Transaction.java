package com.funds.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Transaction {
    String id;
    String clientId;
    String fundId;
    String fundName;
    TransactionType type;
    BigDecimal amount;
    LocalDateTime createdAt;

    public static Transaction createSubscription(String clientId, Fund fund) {
        return Transaction.builder()
                .id(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fund.getId())
                .fundName(fund.getName())
                .type(TransactionType.SUBSCRIPTION)
                .amount(fund.getMinimumAmount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Transaction createCancellation(String clientId, Fund fund) {
        return Transaction.builder()
                .id(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fund.getId())
                .fundName(fund.getName())
                .type(TransactionType.CANCELLATION)
                .amount(fund.getMinimumAmount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    
}
