package com.funds.domain.port;

import com.funds.domain.model.Fund;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FundRepositoryOutputPort {
 
     Flux<Fund> findAll();

    Mono<Fund> findById(String id);
} 