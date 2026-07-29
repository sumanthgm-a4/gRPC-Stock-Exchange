package com.sum.stock_trading_client.service;

import org.springframework.stereotype.Service;

import com.sum.stock_trading_server.grpc.AddStockRequest;
import com.sum.stock_trading_server.grpc.Empty;
import com.sum.stock_trading_server.grpc.StockListResponse;
import com.sum.stock_trading_server.grpc.StockRequest;
import com.sum.stock_trading_server.grpc.StockResponse;
import com.sum.stock_trading_server.grpc.StockTradingServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockClientService {

    // Blocking stub for Unary
    private final StockTradingServiceGrpc.StockTradingServiceBlockingStub blockingStub;

    // Non-blocking stub for Server-streaming
    private final StockTradingServiceGrpc.StockTradingServiceStub serviceStub;

    public StockResponse getStock(StockRequest stockRequest) {
        StockResponse stockResponse = blockingStub.getStock(stockRequest);
        log.info("StockClientService :: getStock :: stockResponse :: {}", stockResponse);

        return stockResponse;
    }

    public StockListResponse getAllStocks() {
        return blockingStub.getAllStocks(Empty.newBuilder().build());
    }

    public Empty addStock(AddStockRequest request) {
        return blockingStub.addStock(request);
    }

    public Empty deleteStock(StockRequest request) {
        return blockingStub.deleteStock(request);
    }

    public void subscribeStock(StockRequest request) {
        serviceStub.subscribeStock(request, new StreamObserver<StockResponse>() {

            @Override
            public void onNext(StockResponse value) {
                log.info("{}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Stream Completed");
            }
            
        });
    }
}
