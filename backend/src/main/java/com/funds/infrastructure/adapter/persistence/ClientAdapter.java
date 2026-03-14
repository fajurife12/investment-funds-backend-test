package com.funds.infrastructure.adapter.persistence;

import com.funds.domain.model.Client;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.infrastructure.adapter.persistence.document.ClientDocument;
import com.funds.infrastructure.adapter.persistence.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ClientAdapter implements ClientRepositoryOutputPort {
    private final ClientRepository repository;

    @Override
    public Mono<Client> findById(String id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<Client> save(Client client) {
        return repository.save(toDocument(client))
                .map(this::toDomain);
    }

    private Client toDomain(ClientDocument doc) {
        return Client.builder()
                .id(doc.getId())
                .name(doc.getName())
                .email(doc.getEmail())
                .phoneNumber(doc.getPhone())
                .notificationPreference(doc.getNotificationPreference())
                .balance(doc.getBalance())
                .activeFundIds(doc.getActiveFundIds() != null
                        ? doc.getActiveFundIds()
                        : List.of())
                .build();
    }

    private ClientDocument toDocument(Client client) {
        return ClientDocument.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhoneNumber())
                .notificationPreference(client.getNotificationPreference())
                .balance(client.getBalance())
                .activeFundIds(client.getActiveFundIds())
                .build();
    }
}
