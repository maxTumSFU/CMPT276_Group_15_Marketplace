package com.marketplace.sfu_marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;
import com.marketplace.sfu_marketplace.models.ListingStatus;
import org.springframework.ui.Model;
import java.util.List;

@Controller 
public class ListingBrowseController {
    
    private final ListingRepository listingRepo;

    public ListingBrowseController(ListingRepository listingRepo) {
        this.listingRepo = listingRepo;
    }

    // Shows the listings page
    @GetMapping("/listings")
    public String getListings(Model model) {
        List<Listing> listings = listingRepo.findByStatus(ListingStatus.AVAILABLE); // Shows only available listings
        model.addAttribute("listings", listings);
        return "listings-page";
    }

    // Shows a specific listing
    @GetMapping("/listings/{id}")
    public String showListing(@PathVariable("id") int id, Model model) {
        Listing listing = listingRepo.findById(id).orElse(null);

        if (listing == null) {
            return "redirect:/listings";
        }

        model.addAttribute("listing", listing);
        return "listing-detail";
    }
}