package com.example.trendingmoviesservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TrendingMoviesServiceApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TrendingMoviesServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Spring is fully started here, so jdbcTemplate is ready
        TrendingMoviesServer server = new TrendingMoviesServer(jdbcTemplate);
        server.start();
        server.blockUntilShutdown();
    }
}