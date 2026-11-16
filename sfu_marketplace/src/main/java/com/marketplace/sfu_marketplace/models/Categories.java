package com.marketplace.sfu_marketplace.models;



public enum Categories {
    TEXTBOOKS("Textbooks"),
    BOOKS_COMICS("Books & Comics"),
    STATIONERY_SCHOOL_SUPPLIES("Stationery & School Supplies"),
    ELECTRONICS("Electronics"),
    CLOTHING_ACCESSORIES("Clothing & Accessories"),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care"),
    FURNITURE("Furniture"),
    HOME_KITCHEN("Home & Kitchen"),
    FOOD("Food"),
    VEHICLES("Vehicles"),
    BIKES_SCOOTERS("Bikes & Scooters"),
    HOUSING("Housing"),
    SPORTS_OUTDOORS("Sports & Outdoors"),
    ART_CRAFT_SUPPLIES("Art & Craft Supplies"),
    TOOLS_EQUIPMENT("Tools & Equipment"),
    PET_SUPPLIES("Pet Supplies"),
    OTHER("Other");

    private final String displayName;

    Categories(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

