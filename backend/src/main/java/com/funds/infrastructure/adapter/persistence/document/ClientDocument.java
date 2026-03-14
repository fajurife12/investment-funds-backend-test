package com.funds.infrastructure.adapter.persistence.document;

import com.funds.domain.model.NotificationPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clients")
public class ClientDocument {
    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String email;

    private String phone;
    private NotificationPreference notificationPreference;
    private BigDecimal balance;
    private List<String> activeFundIds;
}
