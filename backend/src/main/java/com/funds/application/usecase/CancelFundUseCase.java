package com.funds.application.usecase;

import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.Client;
import com.funds.domain.model.Fund;
import com.funds.domain.model.Transaction;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.FundRepositoryOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CancelFundUseCase {
    private final ClientRepositoryOutputPort clientRepository;
    private final FundRepositoryOutputPort fundRepository;
    private final TransactionRepositoryOutputPort transactionRepository;

    public Mono<Transaction> execute(String clientId, String fundId) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.CLIENT_NOT_FOUND, clientId)))
                .flatMap(client -> fundRepository.findById(fundId)
                        .switchIfEmpty(Mono.error(new DomainException(ErrorCode.FUND_NOT_FOUND, fundId)))
                        .flatMap(fund -> validateIsSubscribed(client, fund, fundId)
                                .thenReturn(fund))
                        .flatMap(fund -> {
                            var updatedClient = client.unsubscribe(fundId, fund.getMinimumAmount());
                            var transaction = Transaction.createCancellation(clientId, fund);

                            return clientRepository.save(updatedClient)
                                    .then(transactionRepository.save(transaction))
                                    .doOnSuccess(tx -> log.info(
                                            "Client {} cancelled fund {} - tx: {}",
                                            clientId, fund.getName(), tx.getId()));
                        }));
    }

    private Mono<Void> validateIsSubscribed(Client client, Fund fund, String fundId) {
        return Mono.just(client.isSubscribedTo(fundId))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.NOT_SUBSCRIBED, fund.getName())))
                .then();
    }
}
