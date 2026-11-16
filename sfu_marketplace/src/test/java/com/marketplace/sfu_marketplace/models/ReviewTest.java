package com.marketplace.sfu_marketplace.models;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ReviewTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void maxRatingTest() {
        Review review = new Review(5, "Great seller!");
        assertTrue(validator.validate(review).isEmpty());
    }

    @Test
    void minimumRatingTest() {
        Review review = new Review(1, "Good seller!");
        assertTrue(validator.validate(review).isEmpty());
    }

    @Test
    void invalidRatingTest1() {
        Review review = new Review(6, "Great seller!");
        assertFalse(validator.validate(review).isEmpty());
    }

    @Test
    void invalidRatingTest2() {
        Review review = new Review(0, "Invalid rating");
        assertFalse(validator.validate(review).isEmpty());
    }

    @Test
    void validCommentLengthTest() {
        String comment = "a".repeat(1000);
        Review review = new Review(5, comment);
        assertTrue(validator.validate(review).isEmpty());
    }

    @Test
    void invalidCommentLengthTest() {
        String comment = "a".repeat(1001);
        Review review = new Review(5, comment);
        assertFalse(validator.validate(review).isEmpty());
    }
}
