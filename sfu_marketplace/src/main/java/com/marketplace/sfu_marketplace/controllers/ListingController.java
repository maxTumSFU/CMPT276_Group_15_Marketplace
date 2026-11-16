
package com.marketplace.sfu_marketplace.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.AccountRole;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.ListingPhoto;

import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;
import com.marketplace.sfu_marketplace.repositories.ListingPhotoRepository;
import com.marketplace.sfu_marketplace.repositories.MessageRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ListingController {

    private final ListingRepository listingRepository;
    private final AccountRepository accountRepository;
    private final ListingPhotoRepository listingPhotoRepository;
    private final MessageRepository messageRepository;
    private final Cloudinary cloudinary;

    // Maximum number of photos is 10/listing
    private static final int MAX_PHOTOS = 10;

    // Maximum size allowed for each photo is 5MB
    private static final long MAX_PHOTO_SIZE = 5 * 1024 * 1024;

    public ListingController(
            ListingRepository listingRepository,
            AccountRepository accountRepository,
            ListingPhotoRepository listingPhotoRepository,
            MessageRepository messageRepository,
            Cloudinary cloudinary) {

        this.listingRepository = listingRepository;
        this.accountRepository = accountRepository;
        this.listingPhotoRepository = listingPhotoRepository;
        this.messageRepository = messageRepository;
        this.cloudinary = cloudinary;
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

        if (result.hasErrors()) {
            System.out.println("LISTING VALIDATION FAILED:");

            result.getAllErrors().forEach(error ->
                    System.out.println(error));

            return "create-listing";
        }

        System.out.println("LISTING VALIDATION PASSED");

    
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

        // Check the max number of photos
        if (selectedPhotos.size() > MAX_PHOTOS) {
            model.addAttribute("photoError",
                    "You can upload a maximum of 10 photos.");
            return "create-listing";
        }

        List<String> savedImagePaths = new ArrayList<>();
        List<String> uploadedPublicIds = new ArrayList<>();

        try {
            // Validate and upload each selected photo
            for (MultipartFile photo : selectedPhotos) {

                String contentType = photo.getContentType();

                // Allow JPG, PNG, and WebP files
                if (!"image/jpeg".equals(contentType)
                        && !"image/png".equals(contentType)
                        && !"image/webp".equals(contentType)) {

                    model.addAttribute("photoError",
                            "Only JPG, PNG, and WebP photos are allowed.");

                    deleteCloudinaryImages(uploadedPublicIds);
                    return "create-listing";
                }

                // Check the max size of each photo
                if (photo.getSize() > MAX_PHOTO_SIZE) {
                    model.addAttribute("photoError",
                            "Each photo must be 5 MB or smaller.");

                    deleteCloudinaryImages(uploadedPublicIds);
                    return "create-listing";
                }

                // Upload the photo to Cloudinary
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        photo.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "sfu-marketplace/listings"
                        )
                );

                // Get the permanent image URL and Cloudinary public ID
                String secureUrl =
                        (String) uploadResult.get("secure_url");

                String publicId =
                        (String) uploadResult.get("public_id");

                // Store the URL and public ID for later use
                savedImagePaths.add(secureUrl);
                uploadedPublicIds.add(publicId);
            }

        } catch (Exception e) {

            System.out.println("CLOUDINARY PHOTO UPLOAD ERROR:");
            e.printStackTrace();

            // Remove photos already uploaded if an error occurs
            deleteCloudinaryImages(uploadedPublicIds);

            model.addAttribute("photoError",
                    "There was a problem uploading your photos. Please try again.");

            return "create-listing";
        }

        try {
            // Connect the listing to its seller
            listing.setSeller(account);
            Listing savedListing = listingRepository.save(listing);

            System.out.println("LISTING SAVED WITH ID: "
                    + savedListing.getListingId());

            // Save each Cloudinary image URL and connect it to the listing
            for (String imagePath : savedImagePaths) {

                ListingPhoto listingPhoto = new ListingPhoto();

                listingPhoto.setImagePath(imagePath);
                listingPhoto.setListing(savedListing);

                listingPhotoRepository.save(listingPhoto);
            }


            account.addListingId(savedListing.getListingId());
            accountRepository.save(account);

            System.out.println("LISTING PUBLISHED SUCCESSFULLY");

            // Open the newly created listing details page
            return "redirect:/listings/" + savedListing.getListingId();

        } catch (Exception e) {

            System.out.println("LISTING SAVE ERROR:");
            e.printStackTrace();

            // Remove Cloudinary photos if saving the listing fails
            deleteCloudinaryImages(uploadedPublicIds);

            model.addAttribute("photoError",
                    "The listing could not be saved. Please try again.");

            return "create-listing";
        }
    }

    // Deletes a listing if the user is its owner or an admin
    @Transactional
    @PostMapping("/listings/{id}/delete")
    public String deleteListing(
            @PathVariable int id, HttpSession session) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        if (sessionAccount == null) {
            return "redirect:/login";
        }

        // Fetch account from the database
        Account account = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (account == null) {
            return "redirect:/login";
        }

        Listing listing = listingRepository.findById(id).orElse(null);

        if (listing == null) {
            return "redirect:/listings";
        }

        boolean isAdmin = account.getRole() == AccountRole.ADMIN;
        boolean isOwner = account.getListingIds().contains(id);

        if (!isAdmin && !isOwner) {
            return "redirect:/listings";
        }

        // Remove the listing ID from the owner's account
        // This prevents ghost listings
        List<Account> accounts = accountRepository.findAll();

        for (Account a : accounts) {
            if (a.getListingIds().contains(id)) {
                a.removeListingId(id);
                accountRepository.save(a);
            }
        }

        // Delete all messages associated with this listing
        messageRepository.deleteByListing_ListingId(id);

        // Delete the listing
        listingRepository.delete(listing);

        return "redirect:/listings";
    }

    // Deletes uploaded Cloudinary photos if an error occurs
    private void deleteCloudinaryImages(List<String> publicIds) {

        for (String publicId : publicIds) {
            try {
                cloudinary.uploader().destroy(
                        publicId,
                        ObjectUtils.emptyMap()
                );

            } catch (Exception e) {
                System.out.println(
                        "Could not delete Cloudinary photo: " + publicId
                );
            }
        }
    }
}
