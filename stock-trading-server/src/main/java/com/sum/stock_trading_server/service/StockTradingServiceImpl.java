package com.sum.stock_trading_server.service;

import com.sum.stock_trading_server.repository.StockRepository;
import org.springframework.grpc.server.service.GrpcService;

import com.sum.stock_trading_server.entity.Stock;
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
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
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
    
}
