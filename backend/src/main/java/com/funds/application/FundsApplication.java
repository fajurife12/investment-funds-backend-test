package com.funds.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.funds")
@EnableReactiveMongoRepositories(basePackages = "com.funds.infrastructure.adapter.persistence.repository")

public class FundsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundsApplication.class, args);
	}

}
