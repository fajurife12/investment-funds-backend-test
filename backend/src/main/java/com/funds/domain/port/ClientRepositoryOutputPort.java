package com.funds.domain.port;

import com.funds.domain.model.Client;

import reactor.core.publisher.Mono;

public interface ClientRepositoryOutputPort  {
    
    Mono<Client> findById(String id);

    Mono<Client> save(Client client);
}
