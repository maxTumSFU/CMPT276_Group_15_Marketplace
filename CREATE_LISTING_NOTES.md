# Create Listing - Work Notes

## Overview
Worked on the initial Create Listing feature for the SFU Marketplace project.

## Files Created

### Models
- ItemCondition.java
  - Defines the item conditions: NEW and USED.
- ListingStatus.java
  - Defines the listing statuses: AVAILABLE and SOLD.

### Repositories
- ListingRepository.java
  - Handles database operations for listings.
- AccountRepository.java
  - Handles database operations for accounts.

### Controller
- ListingController.java
  - Shows the Create Listing form at /listings/new.
  - Receives the submitted form at /listings.
  - Checks the information entered by the user.
  - Shows a temporary success message when the form is valid.
  - Saving listings to the database is not implemented yet.

### Templates
- create-listing.html
  - Contains the Create Listing form.
  - Includes title, description, price, condition, and category fields.
  - Shows validation errors and a temporary success message.

### Styling
- static/css/style.css
  - Adds styling to the Create Listing page and form.

## Files Modified
- Listing.java
  - Added condition, category, status, dateCreated, and seller fields.
  - Added validation rules.
  - Automatically sets the creation date using @PrePersist.
- pom.xml
  - Added spring-boot-starter-validation.

## Testing Completed
- Project compiled successfully using Maven.
- Spring Boot application started using a local H2 database.
- Create Listing page opened successfully.
- CSS styling loaded correctly.
- Form fields and condition dropdown worked.
- Temporary success message appeared after submitting valid information.
- Tested the title by entering only spaces. The form showed "Title is required."
- Tested a negative price. The browser did not allow it.
- Tested a price of 0. The form accepted it.
- Tested the condition dropdown without selecting an option. The browser asked me to select a condition.
- Tested the category field while leaving it empty. The browser showed "Please fill out this field."
- Tested the description by entering only spaces. The form showed "Description is required."
- Tested the category by entering only spaces. The form showed "Category is required."

## Remaining Work
- Connect each listing to the logged-in seller.
- Save listings to the database.
- Test creating and retrieving listings.
- Coordinate with the group before changing shared database relationships.