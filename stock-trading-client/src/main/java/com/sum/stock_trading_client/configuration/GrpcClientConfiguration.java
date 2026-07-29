package com.sum.stock_trading_client.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import com.sum.stock_trading_server.grpc.StockTradingServiceGrpc;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    StockTradingServiceGrpc.StockTradingServiceBlockingStub blockingStub(
            GrpcChannelFactory channels) {

        return StockTradingServiceGrpc.newBlockingStub(
                channels.createChannel("stock-server"));
    }

    @Bean
    StockTradingServiceGrpc.StockTradingServiceStub serviceStub(GrpcChannelFactory channels) {
        return StockTradingServiceGrpc.newStub(channels.createChannel("stock-server"));
    }
}