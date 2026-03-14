package com.funds.infrastructure.adapter.notification;

import com.funds.domain.model.Notification;
import com.funds.domain.model.NotificationPreference;
import com.funds.domain.port.NotificationOutputPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class NotificationDispatcher implements NotificationOutputPort {

    private final EmailAdapter emailAdapter;
    private final SmsAdapter smsAdapter;


    @Override
    public Mono<Void> send(Notification notification) {
        return Mono.just(notification.getChannel())
                .flatMap(channel -> channel == NotificationPreference.SMS
                        ? smsAdapter.send(notification)
                        : emailAdapter.send(notification));
    }
}
