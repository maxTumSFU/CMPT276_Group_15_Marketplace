
package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.ListingPhoto;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;
import com.marketplace.sfu_marketplace.repositories.ListingPhotoRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class ListingController {

    private final ListingRepository listingRepository;
    private final AccountRepository accountRepository;
    private final ListingPhotoRepository listingPhotoRepository;

    // Maximum number of photos allowed per listing
    private static final int MAX_PHOTOS = 10;

    // Maximum size allowed for each photo (5 MB)
    private static final long MAX_PHOTO_SIZE = 5 * 1024 * 1024;

    // Folder where uploaded photos are stored
    private static final String UPLOAD_DIRECTORY = "uploads/listings";

    // Constructor for the required repositories
    public ListingController(
            ListingRepository listingRepository,
            AccountRepository accountRepository,
            ListingPhotoRepository listingPhotoRepository) {

        this.listingRepository = listingRepository;
        this.accountRepository = accountRepository;
        this.listingPhotoRepository = listingPhotoRepository;
    }

    // Displays the create listing form
    @GetMapping("/listings/new")
    public String showCreateListingForm(
            Model model, HttpSession session) {

        Account account =
                (Account) session.getAttribute("session_account");

        // Only logged-in users can create listings
        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("listing", new Listing());

        return "create-listing";
    }

    // Receives, validates, and saves a new listing
    @PostMapping("/listings")
    public String createListing(
            @Valid @ModelAttribute("listing") Listing listing,
            BindingResult result,
            @RequestParam(value = "uploadedPhotos", required = false)
            List<MultipartFile> photos,
            HttpSession session,
            Model model) {

        System.out.println("PUBLISH LISTING REQUEST RECEIVED");

        // Check whether the user is logged in
        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        if (sessionAccount == null) {
            System.out.println("ERROR: User is not logged in");
            return "redirect:/login";
        }

        // Check for listing validation errors
        if (result.hasErrors()) {
            System.out.println("LISTING VALIDATION FAILED:");

            result.getAllErrors().forEach(error ->
                    System.out.println(error));

            return "create-listing";
        }

        System.out.println("LISTING VALIDATION PASSED");

        // Retrieve the seller's account from the database
        Account account = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (account == null) {
            System.out.println("ERROR: Account not found");
            session.invalidate();
            return "redirect:/login";
        }

        // Remove empty photo entries
        List<MultipartFile> selectedPhotos = new ArrayList<>();

        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo != null && !photo.isEmpty()) {
                    selectedPhotos.add(photo);
                }
            }
        }

        System.out.println("NUMBER OF PHOTOS: " + selectedPhotos.size());

        // Check the maximum number of photos
        if (selectedPhotos.size() > MAX_PHOTOS) {
            model.addAttribute("photoError",
                    "You can upload a maximum of 10 photos.");
            return "create-listing";
        }

        // Store uploaded photo paths
        List<String> savedImagePaths = new ArrayList<>();

        try {
            // Create the upload folder if it does not exist
            Path uploadPath = Paths.get(UPLOAD_DIRECTORY)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            // Validate and save each selected photo
            for (MultipartFile photo : selectedPhotos) {

                String contentType = photo.getContentType();
                String extension;

                // Allow JPG, PNG, and WebP files
                if ("image/jpeg".equals(contentType)) {
                    extension = ".jpg";
                } else if ("image/png".equals(contentType)) {
                    extension = ".png";
                } else if ("image/webp".equals(contentType)) {
                    extension = ".webp";
                } else {
                    model.addAttribute("photoError",
                            "Only JPG, PNG, and WebP photos are allowed.");

                    deleteUploadedFiles(savedImagePaths);
                    return "create-listing";
                }

                // Check the maximum size of each photo
                if (photo.getSize() > MAX_PHOTO_SIZE) {
                    model.addAttribute("photoError",
                            "Each photo must be 5 MB or smaller.");

                    deleteUploadedFiles(savedImagePaths);
                    return "create-listing";
                }

                // Generate a unique filename
                String fileName = UUID.randomUUID() + extension;

                Path filePath = uploadPath.resolve(fileName);

                // Save the uploaded photo
                try (var inputStream = photo.getInputStream()) {
                    Files.copy(inputStream, filePath,
                            StandardCopyOption.REPLACE_EXISTING);
                }

                // Store the image path for the database
                savedImagePaths.add("/uploads/listings/" + fileName);
            }

        } catch (IOException e) {

            System.out.println("PHOTO UPLOAD ERROR: " + e.getMessage());

            deleteUploadedFiles(savedImagePaths);

            model.addAttribute("photoError",
                    "There was a problem uploading your photos. Please try again.");

            return "create-listing";
        }

        try {
            // Connect the listing to its seller
            listing.setSeller(account);

            // Save the listing in PostgreSQL
            Listing savedListing = listingRepository.save(listing);

            System.out.println("LISTING SAVED WITH ID: "
                    + savedListing.getListingId());

            // Save each photo and connect it to the listing
            for (String imagePath : savedImagePaths) {

                ListingPhoto listingPhoto = new ListingPhoto();

                listingPhoto.setImagePath(imagePath);
                listingPhoto.setListing(savedListing);

                listingPhotoRepository.save(listingPhoto);
            }

            // Add the listing ID to the seller's account
            account.addListingId(savedListing.getListingId());
            accountRepository.save(account);

            System.out.println("LISTING PUBLISHED SUCCESSFULLY");

            // Open the newly created listing details page
            return "redirect:/listings/" + savedListing.getListingId();

        } catch (Exception e) {

            System.out.println("LISTING SAVE ERROR:");
            e.printStackTrace();

            // Clean up uploaded files if saving fails
            deleteUploadedFiles(savedImagePaths);

            model.addAttribute("photoError",
                    "The listing could not be saved. Please try again.");

            return "create-listing";
        }
    }

    // Deletes uploaded files if an error occurs
    private void deleteUploadedFiles(List<String> imagePaths) {

        for (String imagePath : imagePaths) {

            // Extract the filename from the image path
            String fileName = imagePath.substring(
                    imagePath.lastIndexOf("/") + 1);

            try {
                Path filePath = Paths.get(UPLOAD_DIRECTORY)
                        .toAbsolutePath()
                        .normalize()
                        .resolve(fileName);

                Files.deleteIfExists(filePath);

            } catch (IOException e) {
                System.out.println("Could not delete uploaded file: "
                        + fileName);
            }
        }
    }
}
