package com.marketplace.sfu_marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.marketplace.sfu_marketplace.models.Review;
import com.marketplace.sfu_marketplace.models.Account;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Finds all reviews written about a seller
    List<Review> findBySeller(Account seller);

    // Checks whether a user has already reviewed a seller
    boolean existsByRaterAndSeller(Account rater, Account seller);

}