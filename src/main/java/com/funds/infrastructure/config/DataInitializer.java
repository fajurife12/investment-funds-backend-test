package com.funds.infrastructure.config;

import com.funds.domain.model.FundCategory;
import com.funds.domain.model.NotificationPreference;
import com.funds.infrastructure.adapter.persistence.document.ClientDocument;
import com.funds.infrastructure.adapter.persistence.document.FundDocument;
import com.funds.infrastructure.adapter.persistence.repository.ClientRepository;
import com.funds.infrastructure.adapter.persistence.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seed(){
        seedFunds();
        seedTestClient();
    }

    private void seedFunds() {
        fundRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(c -> fundRepository.saveAll(buildFunds()))
                .doOnNext(f -> log.info("Seeded fund: {}", f.getName()))
                .subscribe();
    }

    private void seedTestClient() {
        clientRepository.count()
                .filter(count -> count == 0)
                .flatMap(c -> clientRepository.save(
                        ClientDocument.builder()
                                .id("client-001")
                                .name("Juan Pérez")
                                .email("juan.perez@example.com")
                                .phone("+573001234567")
                                .notificationPreference(NotificationPreference.EMAIL)
                                .balance(new BigDecimal("500000"))
                                .activeFundIds(new ArrayList<>())
                                .build()))
                .doOnSuccess(c -> log.info("Seeded test client: {}", c.getName()))
                .subscribe();
    }


    private List<FundDocument> buildFunds() {
        return List.of(
                FundDocument.builder().id("1").name("FPV_BTG_PACTUAL_RECAUDADORA")
                        .minimumAmount(new BigDecimal("75000")).category(FundCategory.FPV).build(),
                FundDocument.builder().id("2").name("FPV_BTG_PACTUAL_ECOPETROL")
                        .minimumAmount(new BigDecimal("125000")).category(FundCategory.FPV).build(),
                FundDocument.builder().id("3").name("DEUDAPRIVADA")
                        .minimumAmount(new BigDecimal("50000")).category(FundCategory.FIC).build(),
                FundDocument.builder().id("4").name("FDO-ACCIONES")
                        .minimumAmount(new BigDecimal("250000")).category(FundCategory.FIC).build(),
                FundDocument.builder().id("5").name("FPV_BTG_PACTUAL_DINAMICA")
                        .minimumAmount(new BigDecimal("100000")).category(FundCategory.FPV).build()
        );
    }
}
