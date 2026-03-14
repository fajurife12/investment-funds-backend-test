package com.funds.infrastructure.adapter.notification;

import com.funds.domain.model.Notification;
import com.funds.domain.port.NotificationOutputPort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class SmsAdapter implements NotificationOutputPort {

    @Override
    public Mono<Void> send(Notification notification) {
        return Mono.fromRunnable(() -> {
            log.info("[SMS] to: {} | Message: {}", notification.getRecipient(), notification.getMessage());
        });
    }
}
