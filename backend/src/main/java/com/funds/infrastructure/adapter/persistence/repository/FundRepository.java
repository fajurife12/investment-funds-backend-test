package com.funds.infrastructure.adapter.persistence.repository;

import com.funds.infrastructure.adapter.persistence.document.FundDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends ReactiveMongoRepository<FundDocument,String> {
}
