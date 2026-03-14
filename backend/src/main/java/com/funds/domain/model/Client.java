package com.funds.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Client {

    String id;
    String name;
    String email;
    String phoneNumber;
    NotificationPreference notificationPreference;
    BigDecimal balance;
    List<String> activeFundIds;
    
    public boolean isSubscribedTo(String fundId) {
        return activeFundIds != null && activeFundIds.contains(fundId);
    }

    public Client subscribe(String fundId, BigDecimal amount) {
        var updateFunds = new ArrayList<>(activeFundIds != null ? activeFundIds : List.of());
        updateFunds.remove(fundId);
        return this.withBalance(this.balance.add(amount))
            .withActiveFundIds(updateFunds);
        
    }

    public Client unsubscribe(String fundId, BigDecimal amount) {
        var updatedFunds = new ArrayList<>(activeFundIds != null ? activeFundIds : List.of());
        updatedFunds.remove(fundId);
        return this.withBalance(this.balance.add(amount))
                .withActiveFundIds(updatedFunds);
    }
}
