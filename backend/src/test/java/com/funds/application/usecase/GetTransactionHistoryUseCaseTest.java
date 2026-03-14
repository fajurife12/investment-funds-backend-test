package com.funds.application.usecase;

import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.*;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTransactionHistoryUseCase")
class GetTransactionHistoryUseCaseTest {

    @Mock private ClientRepositoryOutputPort clientRepository;
    @Mock private TransactionRepositoryOutputPort transactionRepository;

    private GetTransactionHistoryUseCase useCase;
    private Client defaultClient;

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionHistoryUseCase(clientRepository, transactionRepository);

        defaultClient = Client.builder()
                .id("client-001")
                .name("Juan Pérez")
                .email("juan@test.com")
                .phoneNumber("+57300000000")
                .notificationPreference(NotificationPreference.EMAIL)
                .balance(new BigDecimal("500000"))
                .activeFundIds(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("debe retornar el historial de transacciones")
    void shouldReturnHistorySuccessfully() {
        Transaction tx1 = Transaction.builder()
                .id("tx-001").clientId("client-001").fundId("1")
                .fundName("FPV_BTG_PACTUAL_RECAUDADORA")
                .type(TransactionType.SUBSCRIPTION)
                .amount(new BigDecimal("75000"))
                .createdAt(LocalDateTime.now())
                .build();

        Transaction tx2 = Transaction.builder()
                .id("tx-002").clientId("client-001").fundId("1")
                .fundName("FPV_BTG_PACTUAL_RECAUDADORA")
                .type(TransactionType.CANCELLATION)
                .amount(new BigDecimal("75000"))
                .createdAt(LocalDateTime.now())
                .build();

        when(clientRepository.findById("client-001")).thenReturn(Mono.just(defaultClient));
        when(transactionRepository.findByClientId("client-001")).thenReturn(Flux.just(tx1, tx2));

        StepVerifier.create(useCase.execute("client-001"))
                .assertNext(tx -> assertThat(tx.getType()).isEqualTo(TransactionType.SUBSCRIPTION))
                .assertNext(tx -> assertThat(tx.getType()).isEqualTo(TransactionType.CANCELLATION))
                .verifyComplete();
    }

    @Test
    @DisplayName("debe retornar lista vacía si no hay transacciones")
    void shouldReturnEmptyWhenNoTransactions() {
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(defaultClient));
        when(transactionRepository.findByClientId("client-001")).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute("client-001"))
                .verifyComplete();
    }

    @Test
    @DisplayName("debe fallar si el cliente no existe")
    void shouldFailWhenClientNotFound() {
        when(clientRepository.findById("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("unknown"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.CLIENT_NOT_FOUND)
                .verify();
    }
}