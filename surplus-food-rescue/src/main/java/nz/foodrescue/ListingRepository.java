package nz.foodrescue;

/** Stores listings. */
public interface ListingRepository {
    /** Saves the listing and returns its unique listing ID. */
    String save(FoodListing listing);
}