package com.marketplace.sfu_marketplace.models;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "listings")
public class Listing implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int listingId;

    private String title;
    private String description;
    private double price;
    //could add condition, category, date, location etc here

    public Listing() {
    }

    public Listing(int listingId, String title, String description, double price) {

        this.listingId = listingId;
        this.title = title;
        this.description = description;
        this.price = price;
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
}
