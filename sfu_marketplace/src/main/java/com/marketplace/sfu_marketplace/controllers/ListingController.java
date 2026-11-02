package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ListingController {

    private final ListingRepository listingRepository;
    private final AccountRepository accountRepository;

    // Provides access to the listing and account databases
    public ListingController(ListingRepository listingRepository,
                             AccountRepository accountRepository) {
        this.listingRepository = listingRepository;
        this.accountRepository = accountRepository;
    }

    // Displays the form only if the user is logged in
    @GetMapping("/listings/new")
    public String showCreateListingForm(
            Model model, HttpSession session,
            @RequestParam(required = false) String success) {

        Account account = (Account) session.getAttribute("session_account");

        // Redirects users who are not logged in
        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("listing", new Listing());

        // Displays a message after a listing is saved.
        if (success != null) {
            model.addAttribute("successMessage",
                    "Your listing was created successfully!");
        }

        return "create-listing";
    }

    // Validates and saves a new listing
    @PostMapping("/listings")
    public String createListing(
            @Valid @ModelAttribute("listing") Listing listing,
            BindingResult result,
            HttpSession session) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        // Makes sure the user is logged in before creating a listing
        if (sessionAccount == null) {
            return "redirect:/login";
        }

        // Displays validation errors if the information is invalid
        if (result.hasErrors()) {
            return "create-listing";
        }

        // Gets the account from the database
        Account account = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (account == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // Connects the listing to the account that created it
        listing.setSeller(account);

        // Saves the listing to the database.
        Listing savedListing = listingRepository.save(listing);

        // Adds the new listing ID to the account's listing list
        account.addListingId(savedListing.getListingId());
        accountRepository.save(account);

        // Redirects to prevent submitting the same form twice
        return "redirect:/listings/new?success=true";
    }
}