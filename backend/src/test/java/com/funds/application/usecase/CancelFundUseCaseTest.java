package com.funds.application.usecase;


import com.funds.domain.exception.DomainException;
import com.funds.domain.exception.ErrorCode;
import com.funds.domain.model.*;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.FundRepositoryOutputPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelFundUseCase")
class CancelFundUseCaseTest {

    @Mock private ClientRepositoryOutputPort clientRepository;
    @Mock private FundRepositoryOutputPort fundRepository;
    @Mock private TransactionRepositoryOutputPort transactionRepository;

    private CancelFundUseCase useCase;
    private Client subscribedClient;
    private Fund defaultFund;

    @BeforeEach
    void setUp() {
        useCase = new CancelFundUseCase(clientRepository, fundRepository, transactionRepository);

        defaultFund = Fund.builder()
                .id("1")
                .name("FPV_BTG_PACTUAL_RECAUDADORA")
                .minimumAmount(new BigDecimal("75000"))
                .category(FundCategory.FPV)
                .build();

        subscribedClient = Client.builder()
                .id("client-001")
                .name("Juan Pérez")
                .email("juan@test.com")
                .phoneNumber("+57300000000")
                .notificationPreference(NotificationPreference.EMAIL)
                .balance(new BigDecimal("425000"))
                .activeFundIds(new ArrayList<>(List.of("1")))
                .build();
    }

    @Test
    @DisplayName("debe cancelar la suscripción y retornar el saldo")
    void shouldCancelSuccessfully() {
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(subscribedClient));
        when(fundRepository.findById("1")).thenReturn(Mono.just(defaultFund));
        when(clientRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(transactionRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.execute("client-001", "1"))
                .assertNext(tx -> {
                    assertThat(tx.getType()).isEqualTo(TransactionType.CANCELLATION);
                    assertThat(tx.getFundId()).isEqualTo("1");
                    assertThat(tx.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("debe fallar si el cliente no está suscrito al fondo")
    void shouldFailWhenNotSubscribed() {
        Client unsubscribedClient = subscribedClient.withActiveFundIds(new ArrayList<>());
        when(clientRepository.findById("client-001")).thenReturn(Mono.just(unsubscribedClient));
        when(fundRepository.findById("1")).thenReturn(Mono.just(defaultFund));

        StepVerifier.create(useCase.execute("client-001", "1"))
                .expectErrorMatches(ex -> ex instanceof DomainException
                        && ((DomainException) ex).getErrorCode() == ErrorCode.NOT_SUBSCRIBED)
                .verify();
    }
}