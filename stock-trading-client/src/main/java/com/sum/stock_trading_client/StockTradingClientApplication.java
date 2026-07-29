package com.sum.stock_trading_client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sum.stock_trading_client.service.StockClientService;
import com.sum.stock_trading_server.grpc.StockRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

	private final StockClientService service;

	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		StockRequest stockRequest = StockRequest.newBuilder()
				.setStockSymbol("GOOGL")
				.build();

		log.info("{}", service.getStock(stockRequest));
		service.subscribeStock(stockRequest);
	}

}
