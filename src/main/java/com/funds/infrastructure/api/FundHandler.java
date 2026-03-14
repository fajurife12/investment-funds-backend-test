package com.funds.infrastructure.api;

import com.funds.application.usecase.CancelFundUseCase;
import com.funds.application.usecase.GetTransactionHistoryUseCase;
import com.funds.application.usecase.SubscribeFundUseCase;
import com.funds.domain.model.Fund;
import com.funds.domain.model.Transaction;
import com.funds.infrastructure.adapter.persistence.repository.FundRepository;
import com.funds.infrastructure.api.dto.FundDtos;
import com.funds.infrastructure.api.dto.FundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.funds.infrastructure.api.dto.FundDtos.*;

@Component
@RequiredArgsConstructor
public class FundHandler {
    private final SubscribeFundUseCase subscribeFundUseCase;
    private final CancelFundUseCase cancelFundUseCase;
    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
    private final FundRepository fundRepository;
    private final TransactionResponse transactionResponse;

    public Mono<ServerResponse> listFunds(ServerRequest request) {
        return fundRepository.findAll()
                .map(FundMapper::toFundResponse)
                .collectList()
                .flatMap(funds -> ServerResponse.ok().bodyValue(funds));
    }

    public Mono<ServerResponse> subscribe(ServerRequest request) {
        String fundId = request.pathVariable("fundId");
        return request.bodyToMono(FundDtos.SubscribeRequest.class)
                .flatMap(req-> subscribeFundUseCase.execute(req.clientId(), fundId))
                .map(FundMapper::toTransactionResponse)
                .flatMap(tx-> ServerResponse.status(HttpStatus.CREATED).bodyValue(tx));
    }

    public Mono<ServerResponse> cancel(ServerRequest request) {
        String fundId = request.pathVariable("fundId");
        String clientId = request.queryParam("clientId")
                .orElseThrow(() -> new IllegalArgumentException("clientId es requerido"));
        return cancelFundUseCase.execute(clientId, fundId)
                .map(FundMapper::toTransactionResponse)
                .flatMap(tx -> ServerResponse.ok().bodyValue(tx));
    }

    public Mono<ServerResponse> getHistory(ServerRequest request) {
        String clientId = request.pathVariable("clientId");
        return getTransactionHistoryUseCase.execute(clientId)
                .map(FundMapper::toTransactionResponse)
                .collectList()
                .flatMap(history -> ServerResponse.ok().bodyValue(history));
    }

}
