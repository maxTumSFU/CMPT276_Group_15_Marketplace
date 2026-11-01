package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ListingController {

    private final ListingRepository listingRepository;

    public ListingController(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    // Displays the form for creating a new listing.
    @GetMapping("/listings/new")
    public String showCreateListingForm(Model model) {
        model.addAttribute("listing", new Listing());
        return "create-listing";
    }

    // Receives the form, validates the information, and displays a result.
    @PostMapping("/listings")
    public String createListing(
            @Valid @ModelAttribute("listing") Listing listing,
            BindingResult result,
            Model model) {

        // Displays validation errors if the information is invalid.
        if (result.hasErrors()) {
            return "create-listing";
        }

        // Temporary success message until database saving is implemented.
        model.addAttribute("successMessage",
                "Your listing form was submitted successfully! (Not saved yet.)");

        return "create-listing";
    }
}