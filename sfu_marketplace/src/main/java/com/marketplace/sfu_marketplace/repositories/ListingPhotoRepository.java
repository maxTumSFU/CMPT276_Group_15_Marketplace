package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.ListingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingPhotoRepository
        extends JpaRepository<ListingPhoto, Integer> {

              List<ListingPhoto> findByListingOrderByPhotoIdAsc(Listing listing);

        }
