#
# Create Listing - Work Notes

## Overview
Worked on implementing the Create Listing feature for the SFU Marketplace.

## Files Created
- `ItemCondition.java` – Defines the item conditions (NEW, USED).
- `ListingStatus.java` – Defines listing statuses (AVAILABLE, SOLD).
- `ListingRepository.java` – Handles listing database operations.
- `AccountRepository.java` – Handles account database operations.
- `ListingController.java` – Handles displaying the form, validating and saving listings, and connecting them to the logged-in seller.
- `create-listing.html` – Contains the listing form.

## Files Modified
- `Listing.java` – Added condition, category, status, creation date, seller, and validation rules.
- `pom.xml` – Added the validation dependency.
- `AccountRepository.java` – Added methods to find accounts by username and email.

## Testing
- Project compiled and ran successfully using a local H2 database.
- Tested registration and login.
- Successfully created and saved a listing.
- Confirmed the listing was connected to the logged-in account.
- Tested empty fields, spaces, and negative prices.
- Confirmed validation messages appeared.
- Confirmed users are redirected to login if they try to create a listing while logged out.

## Remaining Work
- Test integration with the Browse Listings feature.
- Test with the group's shared PostgreSQL database.
- Complete integration testing with the group.
