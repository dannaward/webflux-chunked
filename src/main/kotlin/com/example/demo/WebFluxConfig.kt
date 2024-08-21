package com.example.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*


@Configuration
class RouterConfig {
    @Bean
    fun route(chunkedRequestHandler: ChunkedRequestHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(RequestPredicates.POST("/chunk"), chunkedRequestHandler::handleChunkedRequest)
    }
}