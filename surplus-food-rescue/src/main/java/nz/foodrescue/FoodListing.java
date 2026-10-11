package nz.foodrescue;

import java.time.Instant;

/** A surplus food listing posted by a donor. */
public record FoodListing(String donorId, String itemName, int quantity,
                          Instant expiry, Instant pickupStart, Instant pickupEnd) {
}