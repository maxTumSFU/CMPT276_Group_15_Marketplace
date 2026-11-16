
package com.marketplace.sfu_marketplace.models;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_photos")
public class ListingPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int photoId;

    // Stores the location of the image file.
    @Column(nullable = false)
    private String imagePath;

    // Connects each photo to its listing.
    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    public ListingPhoto() {
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}
