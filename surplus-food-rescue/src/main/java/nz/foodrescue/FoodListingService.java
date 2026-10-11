package nz.foodrescue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Creates surplus food listings (NZ-01). */
public class FoodListingService {
    private static final Duration MIN_WINDOW = Duration.ofMinutes(30);

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
        if (!donorVerifier.isVerified(donorId)) {
            throw new UnauthorizedDonorException("Action Unauthorized: Only verified food"
                    + " donors are permitted to set up listings");
        }
        if (!expiry.isAfter(clock.instant())) {
            throw new InvalidListingException("Invalid Expiry Date: Food expiry date and"
                    + " time must be set in the future");
        }
        if (Duration.between(pickupStart, pickupEnd).compareTo(MIN_WINDOW) < 0) {
            throw new InvalidListingException("Invalid Pickup Window: The end time must be"
                    + " at least 30 minutes after the start time");
        }
        if (quantity <= 0) {
            throw new InvalidListingException(
                    "Invalid Quantity: Please enter a positive number");
        }
        return listingRepository.save(
                new FoodListing(donorId, itemName, quantity, expiry, pickupStart, pickupEnd));
    }
}