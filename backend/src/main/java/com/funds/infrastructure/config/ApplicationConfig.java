package com.funds.infrastructure.config;

import com.funds.application.usecase.CancelFundUseCase;
import com.funds.application.usecase.GetTransactionHistoryUseCase;
import com.funds.application.usecase.SubscribeFundUseCase;
import com.funds.domain.port.ClientRepositoryOutputPort;
import com.funds.domain.port.FundRepositoryOutputPort;
import com.funds.domain.port.NotificationOutputPort;
import com.funds.domain.port.TransactionRepositoryOutputPort;
import com.funds.infrastructure.adapter.notification.EmailAdapter;
import com.funds.infrastructure.adapter.notification.NotificationDispatcher;
import com.funds.infrastructure.adapter.notification.SmsAdapter;
import com.funds.infrastructure.adapter.persistence.ClientAdapter;
import com.funds.infrastructure.adapter.persistence.FundAdapter;
import com.funds.infrastructure.adapter.persistence.TransactionAdapter;
import com.funds.infrastructure.adapter.persistence.repository.ClientRepository;
import com.funds.infrastructure.adapter.persistence.repository.FundRepository;
import com.funds.infrastructure.adapter.persistence.repository.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationConfig {

    @Bean
    public FundRepositoryOutputPort fundRepositoryOutputPort(FundRepository repository) {
        return new FundAdapter(repository);
    }

    @Bean
    public ClientRepositoryOutputPort clientRepositoryOutputPort(ClientRepository repository) {
        return new ClientAdapter(repository);
    }

    @Bean
    public TransactionRepositoryOutputPort transactionRepositoryOutputPort(TransactionRepository repository) {
        return new TransactionAdapter(repository);
    }

    @Bean
    public EmailAdapter emailAdapter() {
        return new EmailAdapter();
    }

    @Bean
    public SmsAdapter smsAdapter() {
        return new SmsAdapter();
    }

    @Bean
    @Primary
    public NotificationOutputPort notificationOutputPort(
            EmailAdapter emailAdapter,
            SmsAdapter smsAdapter) {
        return new NotificationDispatcher(emailAdapter, smsAdapter);
    }

    @Bean
    public SubscribeFundUseCase subscribeFundUseCase(
            ClientRepositoryOutputPort clientRepository,
            FundRepositoryOutputPort fundRepository,
            TransactionRepositoryOutputPort transactionRepository,
            NotificationOutputPort notificationPort) {
        return new SubscribeFundUseCase(clientRepository, fundRepository, transactionRepository, notificationPort);
    }

    @Bean
    public CancelFundUseCase cancelFundUseCase(
            ClientRepositoryOutputPort clientRepository,
            FundRepositoryOutputPort fundRepository,
            TransactionRepositoryOutputPort transactionRepository) {
        return new CancelFundUseCase(clientRepository, fundRepository, transactionRepository);
    }

    @Bean
    public GetTransactionHistoryUseCase getTransactionHistoryUseCase(
            ClientRepositoryOutputPort clientRepository,
            TransactionRepositoryOutputPort transactionRepository) {
        return new GetTransactionHistoryUseCase(clientRepository, transactionRepository);
    }
}
