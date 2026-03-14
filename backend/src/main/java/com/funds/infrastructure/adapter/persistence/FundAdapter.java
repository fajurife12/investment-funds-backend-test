package com.funds.infrastructure.adapter.persistence;

import com.funds.domain.model.Fund;
import com.funds.domain.port.FundRepositoryOutputPort;
import com.funds.infrastructure.adapter.persistence.document.FundDocument;
import com.funds.infrastructure.adapter.persistence.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FundAdapter implements FundRepositoryOutputPort {
    private final FundRepository repository;

    @Override
    public Flux<Fund> findAll() {
        return repository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Mono<Fund> findById(String id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    private Fund toDomain(FundDocument doc) {
        return Fund.builder()
                .id(doc.getId())
                .name(doc.getName())
                .minimumAmount(doc.getMinimumAmount())
                .category(doc.getCategory())
                .build();
    }
}
