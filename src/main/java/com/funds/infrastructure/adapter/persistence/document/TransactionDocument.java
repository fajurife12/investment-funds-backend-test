package com.funds.infrastructure.adapter.persistence.document;

import com.funds.domain.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class TransactionDocument {

    @Id
    private String id;

    @Indexed
    private String clientId;

    private String fundId;
    private String fundName;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
