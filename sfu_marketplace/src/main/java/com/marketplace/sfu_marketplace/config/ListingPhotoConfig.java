package com.marketplace.sfu_marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@Configuration
public class ListingPhotoConfig implements WebMvcConfigurer {

    // Confirms that Spring detects this configuration.
    public ListingPhotoConfig() {
        System.out.println("LISTING PHOTO CONFIGURATION LOADED");
    }

    // Allows the browser to access uploaded listing photos.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Gets the absolute location of the uploaded photos folder.
        Path uploadPath = Paths.get("uploads", "listings")
                .toAbsolutePath()
                .normalize();

        // Prints the folder location for debugging.
        System.out.println("PHOTO FOLDER LOCATION: " + uploadPath);
        System.out.println("PHOTO FOLDER EXISTS: "
                + Files.exists(uploadPath));

        // Converts the folder location into a file URL.
        String uploadLocation = uploadPath.toUri().toString();

        if (!uploadLocation.endsWith("/")) {
            uploadLocation += "/";
        }

        // Allows the browser to access uploaded photos.
        registry.addResourceHandler("/uploads/listings/**")
                .addResourceLocations(uploadLocation);
    }
}