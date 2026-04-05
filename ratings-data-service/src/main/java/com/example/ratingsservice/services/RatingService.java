package com.example.ratingsservice.services;

import com.example.ratingsservice.entities.RatingEntity;
import com.example.ratingsservice.models.Rating;
import com.example.ratingsservice.models.UserRating;
import com.example.ratingsservice.repositories.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public UserRating getUserRating(String userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId)
                .stream()
                .map(r -> new Rating(r.getMovieId(), r.getRating()))
                .collect(Collectors.toList());
        return new UserRating(ratings);
    }
}