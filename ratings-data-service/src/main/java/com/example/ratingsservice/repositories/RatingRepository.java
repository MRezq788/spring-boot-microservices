package com.example.ratingsservice.repositories;

import com.example.ratingsservice.entities.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findByUserId(String userId);
}