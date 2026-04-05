package com.example.movieinfoservice.repositries;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.movieinfoservice.models.Movie;

public interface MovieRepository extends MongoRepository<Movie, String> {
    
}
