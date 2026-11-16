
# SFU Marketplace

## Project Overview

SFU Marketplace is a web application for the SFU community to buy and sell used/new items. Users can create accounts, post listings, browse items, and message other users about items they are interested in.

## Technologies Used

- Java and Spring Boot
- HTML, CSS, JavaScript
- Thymeleaf
- PostgreSQL
- Render
- Cloudinary

## Iteration 1 – Basic Features

In Iteration 1, we focused on building the main parts of the marketplace.

- Created registration and login/logout.
- Added Student and Admin roles.
- Created a form for users to post listings.
- Saved listing information in PostgreSQL.
- Built the marketplace page to display available listings.
- Set up the database and deployed the application to Render.

## Iteration 2 – Additional Features

In Iteration 2, we worked on adding more features to the marketplace.

- Added an Admin Dashboard to view registered users.
- Developed a messaging system for buyers and sellers.
- Added photo uploads for listings using Cloudinary.
- Added the ability to delete listings.
- Added the ability to add and remove favourite listings.
- Added a rating and review system. 

## Future Work

Some features and improvements we would like to add are:

- Allow users to edit their listings.
- Improve the website's styling and overall design.
- Require an SFU email address for registration so only SFU students and staff can join.
- Improve searching and filtering by keywords, categories, and price.
- Add notifications for new messages.
- Allow users to mark listings as sold or unavailable.
- Continue testing and fixing bugs.

## Running the Project

1. Clone the GitHub repository.
2. Open the `sfu_marketplace` folder.
3. Set up the required environment variables and database.
4. Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```
