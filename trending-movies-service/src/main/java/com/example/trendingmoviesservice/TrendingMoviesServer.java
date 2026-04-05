package com.example.trendingmoviesservice;

import com.example.trending.grpc.MovieRating;
import com.example.trending.grpc.TopMoviesRequest;
import com.example.trending.grpc.TopMoviesResponse;
import com.example.trending.grpc.TrendingMoviesServiceGrpc;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TrendingMoviesServer {

    private static final Logger logger = Logger.getLogger(TrendingMoviesServer.class.getName());

    private Server server;

    // JdbcTemplate is injected from Spring context — used to run SQL queries
    private final JdbcTemplate jdbcTemplate;

    public TrendingMoviesServer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void start() throws Exception {
        int port = 50051;

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new TrendingMoviesImpl(jdbcTemplate))
                .build()
                .start();

        logger.info("Trending Movies gRPC Server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** Shutting down gRPC server ***");
                try {
                    TrendingMoviesServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** Server shut down ***");
            }
        });
    }

    void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    // ---------------------------------------------------------------
    // The gRPC service implementation
    // ---------------------------------------------------------------
    static class TrendingMoviesImpl extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

        private final JdbcTemplate jdbcTemplate;

        public TrendingMoviesImpl(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void getTopMovies(TopMoviesRequest request, StreamObserver<TopMoviesResponse> responseObserver) {

            int limit = 10;
            logger.info("Received request for top " + limit + " movies");

            List<MovieRating> movies = getTopMoviesFromMySQL(limit);

            TopMoviesResponse response = TopMoviesResponse.newBuilder()
                    .addAllMovies(movies)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private List<MovieRating> getTopMoviesFromMySQL(int limit) {

            // This query reads from the Rating table (movieId, rating columns)
            // Groups by movieId, averages the ratings, returns top N sorted by avg
            String sql = "SELECT movie_id, AVG(rating) as avg_rating " +
                    "FROM ratings " +
                    "GROUP BY movie_id " +
                    "ORDER BY avg_rating DESC " +
                    "LIMIT ?";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, limit);

            List<MovieRating> movies = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                MovieRating movie = MovieRating.newBuilder()
                        .setMovieId((String) row.get("movie_id"))
                        .setTitle("")
                        .setAverageRating(((Number) row.get("avg_rating")).doubleValue())
                        .build();
                movies.add(movie);
            }

            return movies;
        }

        private static final Logger logger =
                Logger.getLogger(TrendingMoviesImpl.class.getName());
    }
}