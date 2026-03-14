package com.funds.infrastructure.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
public class FundRouter {

    private static final String FUNDS_BASE = "/api/v1/funds";
    private static final String CLIENTS_BASE = "/api/v1/clients";

    @Bean
    public RouterFunction<ServerResponse> fundRoutes(FundHandler fundHandler) {
        return RouterFunctions.route()
                .GET(FUNDS_BASE, fundHandler::listFunds)
                .POST(FUNDS_BASE + "/{fundId}/subscribe", fundHandler::subscribe)
                .DELETE(FUNDS_BASE + "/{fundId}/subscribe", fundHandler::cancel)
                .GET(CLIENTS_BASE + "/{clientId}/transactions", fundHandler::getHistory)
                .build();
    }
}