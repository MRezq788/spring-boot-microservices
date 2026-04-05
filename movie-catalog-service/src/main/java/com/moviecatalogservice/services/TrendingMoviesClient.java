package com.moviecatalogservice.services;

import com.example.trending.grpc.MovieRating;
import com.example.trending.grpc.TopMoviesRequest;
import com.example.trending.grpc.TopMoviesResponse;
import com.example.trending.grpc.TrendingMoviesServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TrendingMoviesClient {

    private static final Logger logger = Logger.getLogger(TrendingMoviesClient.class.getName());

    // The gRPC channel — like in HelloWorldClient
    private ManagedChannel channel;

    // The blocking stub — like blockingStub in HelloWorldClient
    private TrendingMoviesServiceGrpc.TrendingMoviesServiceBlockingStub blockingStub;

    // Called automatically when Spring starts this bean
    @PostConstruct
    public void init() {
        String target = "localhost:50051"; // where TrendingMoviesServer is listening

        // Same as HelloWorldClient:
        // ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();

        blockingStub = TrendingMoviesServiceGrpc.newBlockingStub(channel);
        logger.info("gRPC channel opened to " + target);
    }

    // Called automatically when Spring shuts down — cleans up like HelloWorldClient's finally block
    @PreDestroy
    public void shutdown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("gRPC channel closed");
    }

    public List<MovieRating> getTop10Movies() {
        TopMoviesRequest request = TopMoviesRequest.newBuilder()
                .setLimit(10)
                .build();

        TopMoviesResponse response;
        try {
            // This is the actual gRPC call — same as blockingStub.sayHello(request)
            response = blockingStub.getTopMovies(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "gRPC call failed: {0}", e.getStatus());
            return new ArrayList<>(); // return empty list on failure
        }

        return response.getMoviesList();
    }
}