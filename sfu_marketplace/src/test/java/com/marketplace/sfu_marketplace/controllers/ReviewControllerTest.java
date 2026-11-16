package com.marketplace.sfu_marketplace.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Review;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ReviewRepository;

public class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        ReviewController controller = new ReviewController(reviewRepository, accountRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // Helper method to create accounts
    private Account createAccount(int id, String username) {

        Account account = new Account();

        account.setAccountId(id);
        account.setUsername(username);
        account.setEmail(username + "@example.com");

        return account;
    }

    // Test displaying reviews and calculating average rating
    @Test
    void testShowReviews() throws Exception {

        Account seller = createAccount(1, "Alice");

        Review r1 = new Review(5, "Great seller!");
        Review r2 = new Review(3, "Good experience.");

        List<Review> reviews = List.of(r1, r2);

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(seller));

        when(reviewRepository.findBySeller(seller))
                .thenReturn(reviews);

        mockMvc.perform(get("/reviews/1"))

                .andExpect(status().isOk())
                .andExpect(view().name("reviews"))
                .andExpect(model().attribute("seller", seller))
                .andExpect(model().attribute("reviews", reviews))
                .andExpect(model().attribute("averageRating", 4.0));

        verify(accountRepository).findById(1);
        verify(reviewRepository).findBySeller(seller);
    }

    // Test successfully submitting a review
    @Test
    void testSubmitReviewSuccess() throws Exception {

        Account reviewer = createAccount(2, "Bob");
        Account seller = createAccount(1, "Alice");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", reviewer);

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(seller));

        when(reviewRepository.existsByRaterAndSeller(reviewer, seller))
                .thenReturn(false);

        mockMvc.perform(post("/reviews/1")
                .session(session)
                .param("rating", "5")
                .param("comment", "Great seller!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/1"));

        ArgumentCaptor<Review> reviewCaptor =
                ArgumentCaptor.forClass(Review.class);

        verify(reviewRepository).save(reviewCaptor.capture());

        Review savedReview = reviewCaptor.getValue();

        org.junit.jupiter.api.Assertions.assertEquals(
                5, savedReview.getRating());

        org.junit.jupiter.api.Assertions.assertEquals(
                "Great seller!", savedReview.getComment());

        org.junit.jupiter.api.Assertions.assertEquals(
                reviewer, savedReview.getRater());

        org.junit.jupiter.api.Assertions.assertEquals(
                seller, savedReview.getSeller());
    }

    // Test preventing users from reviewing themselves
    @Test
    void testSubmitReviewSelf() throws Exception {

        Account seller = createAccount(1, "Alice");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", seller);

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(seller));

        mockMvc.perform(post("/reviews/1")
                .session(session)
                .param("rating", "5")
                .param("comment", "My own review"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/1"));

        org.mockito.Mockito.verifyNoInteractions(reviewRepository);
    }

    // Test preventing duplicate reviews
    @Test
    void testSubmitReviewDuplicate() throws Exception {

        Account reviewer = createAccount(2, "Bob");
        Account seller = createAccount(1, "Alice");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", reviewer);

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(seller));

        when(reviewRepository.existsByRaterAndSeller(reviewer, seller))
                .thenReturn(true);

        mockMvc.perform(post("/reviews/1")
                .session(session)
                .param("rating", "5")
                .param("comment", "Great seller!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/1"));

        verify(reviewRepository)
                .existsByRaterAndSeller(reviewer, seller);

        verify(reviewRepository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Review.class));
    }
}
