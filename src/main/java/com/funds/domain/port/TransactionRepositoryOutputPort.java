package com.funds.domain.port;

import com.funds.domain.model.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryOutputPort {
   Mono<Transaction> save(Transaction transaction);

    Flux<Transaction> findByClientId(String clientId);
}
