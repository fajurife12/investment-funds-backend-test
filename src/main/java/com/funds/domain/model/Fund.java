package com.funds.domain.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Fund {
    String id;
    String name;
    FundCategory category;
    BigDecimal minimumAmount;

    public boolean hasSufficientBalance(BigDecimal clientBalance) {
        return clientBalance.compareTo(minimumAmount) >= 0;
    }
    
}
