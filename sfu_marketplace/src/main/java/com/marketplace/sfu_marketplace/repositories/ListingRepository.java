package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.ListingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Provide database operations for marketplace listings
public interface ListingRepository extends JpaRepository<Listing, Integer> {
    List<Listing> findByStatus(ListingStatus status);
}