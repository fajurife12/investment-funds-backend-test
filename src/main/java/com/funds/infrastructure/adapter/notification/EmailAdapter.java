package com.funds.infrastructure.adapter.notification;

import com.funds.domain.model.Notification;
import com.funds.domain.port.NotificationOutputPort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class EmailAdapter implements NotificationOutputPort {

    @Override
    public Mono<Void> send(Notification notification) {
        return Mono.fromRunnable(() -> log.info("[EMAIL] to: {} | subject: {} | body: {}", notification.getRecipient(),
                notification.getSubject(), notification.getMessage()));
    }
}
