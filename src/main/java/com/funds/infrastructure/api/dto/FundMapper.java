package com.funds.infrastructure.api.dto;

import com.funds.domain.model.Fund;
import com.funds.domain.model.Transaction;
import com.funds.infrastructure.adapter.persistence.document.FundDocument;

public class FundMapper {
    public static FundDtos.TransactionResponse toTransactionResponse(Transaction tx) {
        return new FundDtos.TransactionResponse(
                tx.getId(),
                tx.getClientId(),
                tx.getFundId(),
                tx.getFundName(),
                tx.getType(),
                tx.getAmount(),
                tx.getCreatedAt()
        );
    }

    public static FundDtos.FundResponse toFundResponse(FundDocument fund) {
        return new FundDtos.FundResponse(
                fund.getId(),
                fund.getName(),
                fund.getMinimumAmount(),
                fund.getCategory()
        );
    }
}
