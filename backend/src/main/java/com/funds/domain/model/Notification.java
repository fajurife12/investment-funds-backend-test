package com.funds.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Notification {

    String recipient;
    String subject;
    String message;
    NotificationPreference channel;

    public static Notification subscriptionNotification(Client client, Fund fund) {
        String message = String.format(
                "Hola {}, tu suscripción al fondo {} por COP {}, fue exitosa.",
                client.getName(), fund.getName(), fund.getMinimumAmount()
        );
        return Notification.builder()
                .recipient(client.getNotificationPreference() == NotificationPreference.EMAIL
                        ? client.getEmail()
                        : client.getPhoneNumber())
                .subject("Suscripción exitosa - " + fund.getName())
                .message(message)
                .channel(client.getNotificationPreference())
                .build();
    }
}