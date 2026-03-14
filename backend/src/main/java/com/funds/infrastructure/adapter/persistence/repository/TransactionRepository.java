package com.funds.infrastructure.adapter.persistence.repository;

import com.funds.infrastructure.adapter.persistence.document.TransactionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<TransactionDocument, String> {
    Flux<TransactionDocument> findByClientIdOrderByCreatedAtDesc(String clientId);
}
