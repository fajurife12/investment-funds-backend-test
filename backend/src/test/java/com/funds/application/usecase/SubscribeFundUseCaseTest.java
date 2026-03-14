package com.funds.application.usecase;

import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.*;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.FundRepositoryOutputPort;
import com.funds.domain.port.NotificationOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscribeFundUseCase")
class SubscribeFundUseCaseTest {

    @Mock private ClientRepositoryOutputPort clientRepository;
    @Mock private FundRepositoryOutputPort fundRepository;
    @Mock private TransactionRepositoryOutputPort transactionRepository;
    @Mock private NotificationOutputPort notificationPort;

    private SubscribeFundUseCase useCase;
    private Client defaultClient;
    private Fund defaultFund;

    @BeforeEach
    void setUp() {
        useCase = new SubscribeFundUseCase(
                clientRepository, fundRepository,
                transactionRepository, notificationPort);

        defaultClient = Client.builder()
                .id("client-001")
                .name("Juan Pérez")
                .email("juan@test.com")
                .phoneNumber("+57300000000")
                .notificationPreference(NotificationPreference.EMAIL)
                .balance(new BigDecimal("500000"))
                .activeFundIds(new ArrayList<>())
                .build();

        defaultFund = Fund.builder()
                .id("1")
                .name("FPV_BTG_PACTUAL_RECAUDADORA")
                .minimumAmount(new BigDecimal("75000"))
                .category(FundCategory.FPV)
                .build();
    }

    @Test
    @DisplayName("debe suscribir al cliente exitosamente")
    void shouldSubscribeSuccessfully() {
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Mono.just(defaultFund));
        when(clientRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(transactionRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(notificationPort.send(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("client-001", "1"))
                .assertNext(tx -> {
                    assertThat(tx.getClientId()).isEqualTo("client-001");
                    assertThat(tx.getFundId()).isEqualTo("1");
                    assertThat(tx.getType()).isEqualTo(TransactionType.SUBSCRIPTION);
                    assertThat(tx.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
                    assertThat(tx.getId()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("debe fallar si el cliente no tiene saldo suficiente")
    void shouldFailWhenInsufficientBalance() {
        Client poorClient = defaultClient.withBalance(new BigDecimal("10000"));
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(poorClient));
        when(fundRepository.findById("1")).thenReturn(Mono.just(defaultFund));

        StepVerifier.create(useCase.execute("client-001", "1"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.INSUFFICIENT_BALANCE)
                .verify();
    }

    @Test
    @DisplayName("debe fallar si el cliente ya está suscrito")
    void shouldFailWhenAlreadySubscribed() {
        Client subscribedClient = defaultClient.withActiveFundIds(List.of("1"));
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(subscribedClient));
        when(fundRepository.findById("1")).thenReturn(Mono.just(defaultFund));

        StepVerifier.create(useCase.execute("client-001", "1"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.ALREADY_SUBSCRIBED)
                .verify();
    }

    @Test
    @DisplayName("debe fallar si el cliente no existe")
    void shouldFailWhenClientNotFound() {
        when(clientRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("unknown", "1"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.CLIENT_NOT_FOUND)
                .verify();
    }

    @Test
    @DisplayName("debe fallar si el fondo no existe")
    void shouldFailWhenFundNotFound() {
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(defaultClient));
        when(fundRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute("client-001", "99"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.FUND_NOT_FOUND)
                .verify();
    }
}