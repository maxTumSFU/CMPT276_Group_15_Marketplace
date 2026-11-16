package com.marketplace.sfu_marketplace.controllers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;
import com.marketplace.sfu_marketplace.models.ListingStatus;
import org.springframework.ui.Model;
import java.util.List;
import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import jakarta.servlet.http.HttpSession;
import com.marketplace.sfu_marketplace.models.Categories;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class ListingBrowseController {
    
    private final ListingRepository listingRepo;
    private final AccountRepository accountRepo;

    public ListingBrowseController(
        ListingRepository listingRepo,
        AccountRepository accountRepo) {

        this.listingRepo = listingRepo;
        this.accountRepo = accountRepo;
    }

    
    // Shows available listings, optionally filtered by category
    @GetMapping("/listings")
    public String getListings(
            @RequestParam(name = "categories", required = false)
            List<Categories> selectedCategories,
            Model model) {

        if (selectedCategories == null) {
            selectedCategories = List.of();
        }

        List<Listing> listings;

        if (selectedCategories.isEmpty()) {
            listings = listingRepo.findByStatus(ListingStatus.AVAILABLE);
        } else {
            listings = listingRepo.findByStatusAndCategoryIn(
                    ListingStatus.AVAILABLE,
                    selectedCategories
            );
        }

    model.addAttribute("listings", listings);
    model.addAttribute("categories", Categories.values());
    model.addAttribute("selectedCategories", selectedCategories);

    return "listings-page";
}


  
    // Shows a specific listing
    @GetMapping("/listings/{id}")
    @Transactional(readOnly = true)
    public String showListing(
            @PathVariable("id") int id,
            Model model,
            HttpSession session) {

        Listing listing = listingRepo.findById(id).orElse(null);

        if (listing == null) {
            return "redirect:/listings";
        }

        boolean isFavourite = false;

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        if (sessionAccount != null) {
            Account account = accountRepo
                    .findById(sessionAccount.getAccountId())
                    .orElse(null);

            if (account != null) {
                isFavourite = account.getFavourites()
                        .stream()
                        .anyMatch(fav -> fav.getListingId() == id);
            }
        }

        boolean isOwner = false;

        if (sessionAccount != null) {
            Account account = accountRepo.findById(sessionAccount.getAccountId()).orElse(null);

            if (account != null) {
                isOwner = account.getListingIds().contains(id);
            }
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isFavourite", isFavourite);
        model.addAttribute("listing", listing);
        model.addAttribute("sessionAccount", sessionAccount);

        return "listing-detail";
    }

}