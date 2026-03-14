package com.funds.infrastructure.adapter.persistence.repository;

import com.funds.infrastructure.adapter.persistence.document.TransactionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<TransactionDocument, String> {
}
