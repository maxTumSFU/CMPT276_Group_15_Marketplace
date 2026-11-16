package com.marketplace.sfu_marketplace.controllers;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;

import org.springframework.stereotype.Controller;
import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Listing;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class FavouritesController {
    
    private final AccountRepository accountRepository;
    private final ListingRepository listingRepository;

    public FavouritesController(
        AccountRepository accountRepository,
        ListingRepository listingRepository){

            this.accountRepository = accountRepository;
            this.listingRepository = listingRepository;
        }

    @Transactional
    @PostMapping("/favourites/add/{listingId}")
    public String addFavourite(
            @PathVariable int listingId,
            HttpSession session) {

        // Get the logged-in student
        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        // If nobody is logged in, redirect to login
        if (sessionAccount == null) {
            return "redirect:/login";
        }

        // Find the student's account in PostgreSQL
        Account account = accountRepository
                .findById(sessionAccount.getAccountId())
                .orElseThrow();

        // Find the listing they want to save
        Listing listing = listingRepository
                .findById(listingId)
                .orElseThrow();

        // Add the listing to their favourites
        account.addFavourite(listing);

        // Save the relationship in PostgreSQL
        accountRepository.save(account);

        // Return to the listings page
        return "redirect:/listings";
    }
    @Transactional
@PostMapping("/favourites/remove/{listingId}")
public String removeFavourite(
        @PathVariable int listingId,
        HttpSession session) {

    // Get the logged-in student
    Account sessionAccount =
            (Account) session.getAttribute("session_account");

    // Redirect if nobody is logged in
    if (sessionAccount == null) {
        return "redirect:/login";
    }

    // Find the student in PostgreSQL
    Account account = accountRepository
            .findById(sessionAccount.getAccountId())
            .orElseThrow();

    // Find the listing
    Listing listing = listingRepository
            .findById(listingId)
            .orElseThrow();

    // Remove the listing from favourites
    account.removeFavourite(listing);

    // Save the updated favourites
    accountRepository.save(account);

    return "redirect:/listings";
}
}
