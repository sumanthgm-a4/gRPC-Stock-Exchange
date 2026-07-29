package com.sum.stock_trading_server.service;

import com.sum.stock_trading_server.repository.StockRepository;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.grpc.server.service.GrpcService;

import com.sum.stock_trading_server.entity.Stock;
import com.sum.stock_trading_server.grpc.AddStockRequest;
import com.sum.stock_trading_server.grpc.Empty;
import com.sum.stock_trading_server.grpc.StockListResponse;
import com.sum.stock_trading_server.grpc.StockRequest;
import com.sum.stock_trading_server.grpc.StockResponse;
import com.sum.stock_trading_server.grpc.StockTradingServiceGrpc.StockTradingServiceImplBase;

import io.grpc.stub.StreamObserver;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceImplBase {

    private final StockRepository stockRepository;

    StockTradingServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void addStock(AddStockRequest request, StreamObserver<Empty> responseObserver) {
        Stock stockEntity = new Stock();

        stockEntity.setStockSymbol(request.getStockSymbol());
        stockEntity.setPrice(request.getPrice());
        
        stockRepository.save(stockEntity);
        
        Empty empty = Empty.newBuilder().build();
        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteStock(StockRequest request, StreamObserver<Empty> responseObserver) {
        Stock stockEntity = stockRepository.findByStockSymbol(request.getStockSymbol());

        stockRepository.delete(stockEntity);

        Empty empty = Empty.newBuilder().build();

        responseObserver.onNext(empty);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllStocks(Empty request, StreamObserver<StockListResponse> responseObserver) {
        List<Stock> stocks = stockRepository.findAll();

        List<StockResponse> stockResponse = stocks.stream()
                .map(stock -> {
                    StockResponse response = StockResponse.newBuilder()
                            .setStockSymbol(stock.getStockSymbol())
                            .setPrice(stock.getPrice())
                            .setTimestamp(stock.getLastUpdated().toString())
                            .build();

                    return response;
                })
                .toList();

        StockListResponse stockListResponse = StockListResponse.newBuilder()
                .addAllStockList(stockResponse)
                .build();

        responseObserver.onNext(stockListResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        // stock name -> DB -> map response -> return 

        String stockSymbol = request.getStockSymbol();
        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);

        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastUpdated().toString())
                .build();

        responseObserver.onNext(stockResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {

        String stockSymbol = request.getStockSymbol();

        // Simulate a stream of 10 StockResponses for each stock
        try {
            for (int i = 0; i < 10; i ++) {
                StockResponse stockResponse = StockResponse.newBuilder()
                    .setStockSymbol(stockSymbol)
                    .setPrice(new Random().nextDouble(200))
                    .setTimestamp(Instant.now().toString())
                    .build();
                    
                responseObserver.onNext(stockResponse);

                TimeUnit.SECONDS.sleep(1);
            }

            responseObserver.onCompleted();
        } catch (InterruptedException e) {}
    }
}
