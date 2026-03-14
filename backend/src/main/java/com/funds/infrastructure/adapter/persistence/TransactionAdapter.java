package com.funds.infrastructure.adapter.persistence;

import com.funds.domain.model.Transaction;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import com.funds.infrastructure.adapter.persistence.document.TransactionDocument;
import com.funds.infrastructure.adapter.persistence.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TransactionAdapter implements TransactionRepositoryOutputPort {

    private final TransactionRepository repository;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return repository.save(toDocument(transaction))
                .map(this::toDomain);
    }

    @Override
    public Flux<Transaction> findByClientId(String clientId) {
        return repository.findByClientIdOrderByCreatedAtDesc(clientId)
                .map(this::toDomain);
    }

    private Transaction toDomain(TransactionDocument doc) {
        return Transaction.builder()
                .id(doc.getId())
                .clientId(doc.getClientId())
                .fundId(doc.getFundId())
                .fundName(doc.getFundName())
                .type(doc.getType())
                .amount(doc.getAmount())
                .createdAt(doc.getCreatedAt())
                .build();
    }

    private TransactionDocument toDocument(Transaction transaction) {
        return TransactionDocument.builder()
                .id(transaction.getId())
                .clientId(transaction.getClientId())
                .fundId(transaction.getFundId())
                .fundName(transaction.getFundName())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
