package com.marketplace.sfu_marketplace.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"rater_id", "seller_id"}
        )
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating must be at most 5 stars")
    private int rating;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    @Column(length = 1000)
    private String comment;

    private LocalDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name = "rater_id", nullable = false)
    private Account rater;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Account seller;

    public Review() {
    }

    public Review(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    @PrePersist
    protected void onCreate() {
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Account getRater() {
        return rater;
    }

    public void setRater(Account rater) {
        this.rater = rater;
    }

    public Account getSeller() {
        return seller;
    }

    public void setSeller(Account seller) {
        this.seller = seller;
    }
}