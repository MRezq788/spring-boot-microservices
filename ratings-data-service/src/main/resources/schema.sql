DROP TABLE IF EXISTS ratings;

CREATE TABLE ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    movie_id VARCHAR(255),
    rating INT
);