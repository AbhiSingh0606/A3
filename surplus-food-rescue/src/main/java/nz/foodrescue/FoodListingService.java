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

    /**
     * Creates a listing and returns its ID.
     *
     * @throws UnauthorizedDonorException if the donor is not verified
     * @throws InvalidListingException if any listing detail is invalid
     */
    public String createListing(String donorId, String itemName, int quantity,
                                Instant expiry, Instant pickupStart, Instant pickupEnd) {
        requireVerifiedDonor(donorId);
        requireItemName(itemName);
        requireFutureExpiry(expiry);
        requirePickupWindow(pickupStart, pickupEnd);
        requirePositiveQuantity(quantity);
        return listingRepository.save(
                new FoodListing(donorId, itemName, quantity, expiry, pickupStart, pickupEnd));
    }

    private void requireVerifiedDonor(String donorId) {
        if (!donorVerifier.isVerified(donorId)) {
            throw new UnauthorizedDonorException("Action Unauthorized: Only verified food"
                    + " donors are permitted to set up listings");
        }
    }

    private void requireItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new InvalidListingException("Invalid Item Name: Item name is required");
        }
    }

    private void requireFutureExpiry(Instant expiry) {
        if (!expiry.isAfter(clock.instant())) {
            throw new InvalidListingException("Invalid Expiry Date: Food expiry date and"
                    + " time must be set in the future");
        }
    }

    private void requirePickupWindow(Instant start, Instant end) {
        if (Duration.between(start, end).compareTo(MIN_WINDOW) < 0) {
            throw new InvalidListingException("Invalid Pickup Window: The end time must be"
                    + " at least 30 minutes after the start time");
        }
    }

    private void requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidListingException(
                    "Invalid Quantity: Please enter a positive number");
        }
    }
}