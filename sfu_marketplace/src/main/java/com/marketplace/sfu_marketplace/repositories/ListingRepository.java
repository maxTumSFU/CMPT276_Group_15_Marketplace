package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

// Provide database operations for marketplace listings
public interface ListingRepository extends JpaRepository<Listing, Integer> {
}