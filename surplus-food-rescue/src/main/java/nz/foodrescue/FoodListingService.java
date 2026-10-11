package nz.foodrescue;

import java.time.Clock;
import java.time.Instant;

/** Creates surplus food listings (NZ-01). */
public class FoodListingService {
    private final DonorVerifier donorVerifier;
    private final ListingRepository listingRepository;
    private final Clock clock;

    /** Creates the service with its dependencies. */
    public FoodListingService(DonorVerifier donorVerifier,
                              ListingRepository listingRepository, Clock clock) {
        this.donorVerifier = donorVerifier;
        this.listingRepository = listingRepository;
        this.clock = clock;
    }

    /** Creates a listing and returns its ID. */
    public String createListing(String donorId, String itemName, int quantity,
                                Instant expiry, Instant pickupStart, Instant pickupEnd) {
        donorVerifier.isVerified(donorId);
        return listingRepository.save(
                new FoodListing(donorId, itemName, quantity, expiry, pickupStart, pickupEnd));
    }
}