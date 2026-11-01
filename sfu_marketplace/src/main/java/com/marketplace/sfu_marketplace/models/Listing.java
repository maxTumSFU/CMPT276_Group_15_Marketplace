package com.marketplace.sfu_marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
public class Listing implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int listingId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @PositiveOrZero(message = "Price cannot be negative")
    private double price;

    @NotNull(message = "Please select an item condition")
    @Enumerated(EnumType.STRING)
    private ItemCondition condition;

    @NotBlank(message = "Category is required")
    private String category;

    // New listings are available by default
    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.AVAILABLE;

    // Store date of listing
    private LocalDateTime dateCreated;

    // Store the account of the student who created this listing
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Account seller;

    public Listing() {
    }

    public Listing(int listingId, String title, String description, double price) {
        this.listingId = listingId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // Automatically sets the creation date before saving a new listing
    @PrePersist
    protected void onCreate() {
        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ItemCondition getCondition() {
        return condition;
    }

    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Account getSeller() {
        return seller;
    }

    public void setSeller(Account seller) {
        this.seller = seller;
    }
}