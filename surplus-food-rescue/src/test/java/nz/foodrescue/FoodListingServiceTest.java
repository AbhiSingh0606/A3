package nz.foodrescue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FoodListingServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-11T10:00:00Z");
    private static final Instant EXPIRY = NOW.plus(Duration.ofHours(3));
    private static final String DONOR_ID = "donor-1";
    private static final String ITEM = "Sourdough loaves";

    @Mock
    private DonorVerifier donorVerifier;
    @Mock
    private ListingRepository listingRepository;
    private FoodListingService service;

    @BeforeEach
    void setUp() {
        service = new FoodListingService(donorVerifier, listingRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private String create(String item, int quantity, Instant expiry, long windowMinutes) {
        Instant start = NOW.plus(Duration.ofHours(1));
        return service.createListing(DONOR_ID, item, quantity, expiry, start,
                start.plus(Duration.ofMinutes(windowMinutes)));
    }

    @Test
    void createListing_validInputs_returnsListingId() {
        when(donorVerifier.isVerified(DONOR_ID)).thenReturn(true);
        when(listingRepository.save(any(FoodListing.class))).thenReturn("L-1");

        String id = create(ITEM, 5, EXPIRY, 60);

        assertEquals("L-1", id);
    }

    @Test
    void createListing_unverifiedDonor_throwsUnauthorizedAndSavesNothing() {
        when(donorVerifier.isVerified(DONOR_ID)).thenReturn(false);

        UnauthorizedDonorException ex = assertThrows(UnauthorizedDonorException.class,
                () -> create(ITEM, 5, EXPIRY, 60));

        assertEquals("Action Unauthorized: Only verified food donors are permitted"
                + " to set up listings", ex.getMessage());
        verify(listingRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createListing_nonPositiveQuantity_throwsInvalidQuantity(int quantity) {
        when(donorVerifier.isVerified(DONOR_ID)).thenReturn(true);

        InvalidListingException ex = assertThrows(InvalidListingException.class,
                () -> create(ITEM, quantity, EXPIRY, 60));

        assertEquals("Invalid Quantity: Please enter a positive number", ex.getMessage());
        verify(listingRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {29, 0, -10})
    void createListing_windowUnder30MinutesOrReversed_throwsInvalidWindow(long minutes) {
        when(donorVerifier.isVerified(DONOR_ID)).thenReturn(true);

        InvalidListingException ex = assertThrows(InvalidListingException.class,
                () -> create(ITEM, 5, EXPIRY, minutes));

        assertEquals("Invalid Pickup Window: The end time must be at least 30 minutes"
                + " after the start time", ex.getMessage());
        verify(listingRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {-60, 0})
    void createListing_expiryNotInFuture_throwsInvalidExpiry(long secondsFromNow) {
        when(donorVerifier.isVerified(DONOR_ID)).thenReturn(true);

        InvalidListingException ex = assertThrows(InvalidListingException.class,
                () -> create(ITEM, 5, NOW.plusSeconds(secondsFromNow), 60));

        assertEquals("Invalid Expiry Date: Food expiry date and time must be set"
                + " in the future", ex.getMessage());
        verify(listingRepository, never()).save(any());
    }
}