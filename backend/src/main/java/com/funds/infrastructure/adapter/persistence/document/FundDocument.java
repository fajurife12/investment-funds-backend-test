package com.funds.infrastructure.adapter.persistence.document;

import com.funds.domain.model.FundCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "funds")
public class FundDocument {
    @Id
    private String id;
    private String name;
    private BigDecimal minimumAmount;
    private FundCategory category;
}
