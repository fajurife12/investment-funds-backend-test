package com.funds.domain.port;

import com.funds.domain.model.Notification;

import reactor.core.publisher.Mono;

public interface NotificationOutputPort {
  Mono<Void> send(Notification notification);
}
