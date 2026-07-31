package com.sum.stock_trading_client.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.sum.stock_trading_server.grpc.StockOrderSummary;
import com.sum.stock_trading_server.grpc.StockOrderRequest;
import com.sum.stock_trading_server.grpc.AddStockRequest;
import com.sum.stock_trading_server.grpc.Empty;
import com.sum.stock_trading_server.grpc.TradeStatus;
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

    public void bulkStockOrder() {

        StreamObserver<StockOrderSummary> reponseObserver = new StreamObserver<StockOrderSummary>() {

            @Override
            public void onNext(StockOrderSummary value) {
                log.info("Order Summary from server : {}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Stream is completed");
            }
            
        }; 

        StreamObserver<StockOrderRequest> requestObserver = serviceStub.bulkStockOrder(reponseObserver);

        // Send stream of stock order requests
        try {
            requestObserver.onNext(StockOrderRequest.newBuilder()
                    .setOrderId("1")
                    .setStockSymbol("AAPL")
                    .setOrderType("Buy")
                    .setPrice(150.5)
                    .setQuantity(10)
                    .build()
            );
            requestObserver.onNext(StockOrderRequest.newBuilder()
                    .setOrderId("2")
                    .setStockSymbol("GOOGL")
                    .setOrderType("Buy")
                    .setPrice(110.5)
                    .setQuantity(10)
                    .build()
            );
            requestObserver.onNext(StockOrderRequest.newBuilder()
                    .setOrderId("3")
                    .setStockSymbol("AMZN")
                    .setOrderType("Sell")
                    .setPrice(150.5)
                    .setQuantity(3)
                    .build()
            );

            requestObserver.onCompleted();
        } catch (Exception ex) {
            reponseObserver.onError(ex);
        }
    }

    public void startTrading() throws InterruptedException {
        StreamObserver<StockOrderRequest> requestObserver = serviceStub.liveTrading(new StreamObserver<>() {
        
                @Override
                public void onNext(TradeStatus value) {
                    System.out.println("Server Response: " + value);
                }
    
                @Override
                public void onError(Throwable t) {
                    System.out.println(t.getMessage());
                }
    
                @Override
                public void onCompleted() {
                    System.out.println("Stream completed");
                }
            });

        for (int i = 0; i < 10; i ++) {
            StockOrderRequest request = StockOrderRequest.newBuilder()
                    .setOrderId("ORDER-" + i)
                    .setStockSymbol("AAPL")
                    .setQuantity(i * 10)
                    .setPrice(150.0 + i)
                    .setOrderType("Sell")
                    .build();

            requestObserver.onNext(request);
            Thread.sleep(1);
        }
        requestObserver.onCompleted();
    }
}