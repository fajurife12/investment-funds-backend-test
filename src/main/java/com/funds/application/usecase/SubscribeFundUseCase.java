package com.funds.application.usecase;

import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.Client;
import com.funds.domain.model.Fund;
import com.funds.domain.model.Notification;
import com.funds.domain.model.Transaction;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.FundRepositoryOutputPort;
import com.funds.domain.port.NotificationOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class SubscribeFundUseCase {

    private final ClientRepositoryOutputPort clientRepository;
    private final FundRepositoryOutputPort fundRepository;
    private final TransactionRepositoryOutputPort transactionRepository;
    private final NotificationOutputPort notificationPort;

    public Mono<Transaction> execute(String clientId, String fundId) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.CLIENT_NOT_FOUND, clientId)))
                .flatMap(client -> fundRepository.findById(fundId)
                        .switchIfEmpty(Mono.error(new DomainException(ErrorCode.FUND_NOT_FOUND, fundId)))
                        .flatMap(fund -> validateNotSubscribed(client, fund, fundId)
                                .then(validateSufficientBalance(client, fund))
                                .thenReturn(fund))
                        .flatMap(fund -> {
                            var updatedClient = client.subscribe(fundId, fund.getMinimumAmount());
                            var transaction = Transaction.createSubscription(clientId, fund);
                            var notification = Notification.subscriptionNotification(client, fund);

                            return clientRepository.save(updatedClient)
                                    .then(transactionRepository.save(transaction))
                                    .flatMap(savedTx -> notificationPort.send(notification)
                                            .thenReturn(savedTx))
                                    .doOnSuccess(tx -> log.info(
                                            "Client {} suscrito al fondo {} - tx: {}",
                                            clientId, fund.getName(), tx.getId()));
                        }));
    }


    private Mono<Void> validateNotSubscribed(Client client, Fund fund, String fundId) {
        return Mono.just(client.isSubscribedTo(fundId))
                .filter(subscribed -> !subscribed)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.ALREADY_SUBSCRIBED, fund.getName())))
                .then();
    }

    private Mono<Void> validateSufficientBalance(Client client, Fund fund) {
        return Mono.just(fund.hasSufficientBalance(client.getBalance()))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new DomainException(ErrorCode.INSUFFICIENT_BALANCE, fund.getName())))
                .then();
    }
}
