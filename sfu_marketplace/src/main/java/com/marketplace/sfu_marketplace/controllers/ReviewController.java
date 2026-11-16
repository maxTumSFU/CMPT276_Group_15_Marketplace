package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Review;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;

    public ReviewController(ReviewRepository reviewRepository, AccountRepository accountRepository) {
        this.reviewRepository = reviewRepository;
        this.accountRepository = accountRepository;
    }

    // Display reviews for a particular seller
    @GetMapping("/reviews/{sellerId}")
    public String showReviews(@PathVariable int sellerId, Model model, HttpSession session) {

        Account seller = accountRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            return "redirect:/listings";
        }

        List<Review> reviews = reviewRepository.findBySeller(seller);

        Account account = (Account) session.getAttribute("session_account");

        // Calculate average rating
        double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        // Check whether current user has already reviewed seller
        boolean alreadyReviewed = false;

        if (account != null) {
            alreadyReviewed = reviewRepository.existsByRaterAndSeller(account, seller);
        }

        model.addAttribute("seller", seller);
        model.addAttribute("reviews", reviews);
        model.addAttribute("review", new Review());
        model.addAttribute("account", account);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("alreadyReviewed", alreadyReviewed);

        return "reviews";
    }

    // Submit a review
    @PostMapping("/reviews/{sellerId}")
    public String submitReview(@PathVariable int sellerId, @Valid @ModelAttribute("review") Review review, BindingResult result, HttpSession session, Model model) {

        Account sessionAccount = (Account) session.getAttribute("session_account");

        // User must be logged in
        if (sessionAccount == null) {
            return "redirect:/login";
        }

        Account seller = accountRepository.findById(sellerId).orElse(null);

        if (seller == null) {
            return "redirect:/listings";
        }

        // Prevent users from reviewing themselves
        if (sessionAccount.getAccountId() == sellerId) {
            return "redirect:/reviews/" + sellerId;
        }

        // Prevent duplicate reviews
        if (reviewRepository.existsByRaterAndSeller(sessionAccount, seller)) {
            return "redirect:/reviews/" + sellerId;
        }

        // Display validation errors
        if (result.hasErrors()) {

            List<Review> reviews = reviewRepository.findBySeller(seller);

            double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

            model.addAttribute("seller", seller);
            model.addAttribute("reviews", reviews);
            model.addAttribute("account", sessionAccount);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("alreadyReviewed", false);
            model.addAttribute("review", review);

            return "reviews";
        }

        // Connect review to reviewer and seller
        review.setRater(sessionAccount);
        review.setSeller(seller);

        // Save review
        reviewRepository.save(review);

        // Redirect to prevent duplicate form submissions
        return "redirect:/reviews/" + sellerId;
    }
}