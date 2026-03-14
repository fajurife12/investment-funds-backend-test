package com.funds.application.usecase;

import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.Transaction;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTransactionHistoryUseCase {
    private final ClientRepositoryOutputPort clientRepository;
    private final TransactionRepositoryOutputPort transactionRepository;

    public Flux<Transaction> execute(String clientId) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.CLIENT_NOT_FOUND, clientId)))
                .thenMany(transactionRepository.findByClientId(clientId));
    }
}
