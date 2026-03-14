package com.funds.infrastructure.config;

import com.funds.application.usecase.CancelFundUseCase;
import com.funds.application.usecase.GetTransactionHistoryUseCase;
import com.funds.application.usecase.SubscribeFundUseCase;
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

@Configuration
public class ApplicationConfig {

    @Bean
    public FundAdapter fundAdapter(FundRepository repository) {
        return new FundAdapter(repository);
    }

    @Bean
    public ClientAdapter clientAdapter(ClientRepository repository) {
        return new ClientAdapter(repository);
    }

    @Bean
    public TransactionAdapter transactionAdapter(TransactionRepository repository) {
        return new TransactionAdapter(repository);
    }

    @Bean
    public EmailAdapter emailNotificationAdapter() {
        return new EmailAdapter();
    }

    @Bean
    public SmsAdapter smsNotificationAdapter() {
        return new SmsAdapter();
    }

    @Bean
    public NotificationDispatcher notificationDispatcher(
            EmailAdapter emailAdapter,
            SmsAdapter smsAdapter) {
        return new NotificationDispatcher(emailAdapter, smsAdapter);
    }

    @Bean
    public SubscribeFundUseCase subscribeFundUseCase(
            ClientAdapter clientAdapter,
            FundAdapter fundAdapter,
            TransactionAdapter transactionAdapter,
            NotificationDispatcher notificationDispatcher) {
        return new SubscribeFundUseCase(clientAdapter, fundAdapter, transactionAdapter, notificationDispatcher);
    }

    @Bean
    public CancelFundUseCase cancelFundUseCase(
            ClientAdapter clientAdapter,
            FundAdapter fundAdapter,
            TransactionAdapter transactionAdapter) {
        return new CancelFundUseCase(clientAdapter, fundAdapter, transactionAdapter);
    }

    @Bean
    public GetTransactionHistoryUseCase getTransactionHistoryUseCase(
            ClientAdapter clientAdapter,
            TransactionAdapter transactionAdapter) {
        return new GetTransactionHistoryUseCase(clientAdapter, transactionAdapter);
    }
}
